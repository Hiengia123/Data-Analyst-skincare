package com.uilover.project261.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project261.Repository.RatingRepository
import com.uilover.project261.domain.ProductRatingStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

private const val TAG = "RatingViewModel"

sealed class RatingState {
    object Idle    : RatingState()
    object Loading : RatingState()
    object Success : RatingState()
    data class Error(val message: String) : RatingState()
}

/**
 * ═══════════════════════════════════════════════════════════════════
 * REFACTORED ARCHITECTURE: one-shot reads + optimistic local updates
 * ═══════════════════════════════════════════════════════════════════
 *
 * WHY the old ViewModel was unstable:
 *
 * PROBLEM 1 — reset() + LaunchedEffect(item.id, currentUserId) loop:
 *   LaunchedEffect calls reset() then loadRatings() every time
 *   currentUserId changes. Auth emits a new UserModel object on each
 *   recomposition even if the UID value is identical. This caused the
 *   stats observer to be cancelled and restarted continuously.
 *
 * PROBLEM 2 — currentProductId guard was neutralised:
 *   reset() sets currentProductId = "", so the
 *   "if (currentProductId == productId) return" guard in loadRatings()
 *   NEVER skips — always restarts the observer.
 *
 * PROBLEM 3 — statsObserverJob cancellation race:
 *   If loadRatings() was called twice in rapid succession (e.g. from
 *   auth + recomposition), the new job could start before the old
 *   callbackFlow's awaitClose {} finished removing its listener,
 *   resulting in two concurrent listeners on the same path.
 *
 * NEW DESIGN: no persistent listeners at all.
 *
 *   loadRatings()  → fires two one-shot reads in parallel (stats + user rating)
 *   submitRating() → writes to Firebase, then updates StateFlows locally
 *                    using local arithmetic — no re-read from Firebase
 *
 *   Local optimistic update after submit:
 *     newTotalStars   = currentTotalStars + (newStars - oldStars)
 *     newTotalRatings = currentTotalRatings + (1 if first-time else 0)
 *     newAvg          = newTotalStars / newTotalRatings
 *
 *   This gives the user INSTANT visual feedback without any round-trip.
 * ═══════════════════════════════════════════════════════════════════
 */
class RatingViewModel : ViewModel() {

    private val repository = RatingRepository()

    // ── currently loaded product ────────────────────────────────────
    // Stored as a simple String — compared by VALUE, not reference.
    // No more "same object, different instance" false mismatches.
    private var loadedProductId: String = ""
    private var loadedUserId:    String = ""

    // ── exposed state ───────────────────────────────────────────────
    private val _ratingStats = MutableStateFlow(ProductRatingStats())
    val ratingStats: StateFlow<ProductRatingStats> = _ratingStats

    private val _userRating = MutableStateFlow(0)
    val userRating: StateFlow<Int> = _userRating

    private val _ratingState = MutableStateFlow<RatingState>(RatingState.Idle)
    val ratingState: StateFlow<RatingState> = _ratingState

    // ── loading flag for initial fetch (not submit) ─────────────────
    private val _isLoadingStats = MutableStateFlow(false)
    val isLoadingStats: StateFlow<Boolean> = _isLoadingStats

    // ───────────────────────────────────────────────────────────────
    // loadRatings
    //
    // Safe to call on every recomposition — guards by VALUE comparison,
    // not object reference. Does NOT call reset() internally.
    // ───────────────────────────────────────────────────────────────
    fun loadRatings(productId: String, userId: String?) {
        val uid = userId ?: ""

        // Skip if BOTH product AND user are already loaded.
        // This prevents redundant reads on recomposition.
        if (loadedProductId == productId && loadedUserId == uid) {
            Log.d(TAG, "loadRatings: already loaded — product=$productId uid=$uid (skip)")
            return
        }

        Log.d(TAG, "loadRatings: product=$productId uid=$uid " +
                "(prev: product=$loadedProductId uid=$loadedUserId)")

        loadedProductId = productId
        loadedUserId    = uid

        // Reset visual state for the new product
        _ratingStats.value  = ProductRatingStats()
        _userRating.value   = 0
        _ratingState.value  = RatingState.Idle
        _isLoadingStats.value = true

        // ── Fetch stats (always) and user rating (if logged in) in parallel
        viewModelScope.launch {
            Log.d(TAG, "Fetching product stats…")
            repository.getProductStats(productId).onSuccess { stats ->
                Log.d(TAG, "Stats loaded: avg=${stats.averageRating} total=${stats.totalRatings} " +
                        "totalStars=${stats.totalStars}")
                _ratingStats.value = stats
            }.onFailure {
                Log.e(TAG, "Stats load failed: ${it.message}")
            }
            _isLoadingStats.value = false
        }

        if (uid.isNotBlank()) {
            viewModelScope.launch {
                Log.d(TAG, "Fetching user rating…")
                repository.getUserRating(productId, uid).onSuccess { stars ->
                    Log.d(TAG, "User rating loaded: $stars stars")
                    _userRating.value = stars
                }
            }
        }
    }

    // ───────────────────────────────────────────────────────────────
    // submitRating
    //
    // After a confirmed server write, updates StateFlows LOCALLY using
    // pure arithmetic. No Firebase re-read required → instant UI update.
    //
    // LOCAL OPTIMISTIC FORMULA:
    //   newTotalStars   = currentTotalStars + (newStars - existingStars)
    //   newTotalRatings = currentTotalRatings + (1 if new rater else 0)
    //   newAvg          = round(newTotalStars / newTotalRatings, 1 decimal)
    // ───────────────────────────────────────────────────────────────
    fun submitRating(userId: String, stars: Int) {
        if (loadedProductId.isBlank()) {
            Log.w(TAG, "submitRating: loadedProductId is blank — ignored")
            return
        }

        val existingStars = _userRating.value
        Log.d(TAG, "submitRating: stars=$stars existingStars=$existingStars " +
                "product=$loadedProductId user=$userId")

        viewModelScope.launch {
            _ratingState.value = RatingState.Loading

            try {
                val result = repository.submitRating(
                    productId     = loadedProductId,
                    userId        = userId,
                    stars         = stars,
                    existingStars = existingStars
                )

                result.onSuccess {
                    // ── Optimistic local stats update ─────────────────────
                    val current    = _ratingStats.value
                    val oldStars   = existingStars.toLong()
                    val newStarsL  = stars.toLong()
                    val isNew      = existingStars == 0

                    val newTotalStars   = current.totalStars + (newStarsL - oldStars)
                    val newTotalRatings = current.totalRatings + (if (isNew) 1 else 0)
                    val newAvg = if (newTotalRatings > 0)
                        round((newTotalStars.toDouble() / newTotalRatings) * 10.0) / 10.0
                    else 0.0

                    Log.d(TAG, "Optimistic update: totalStars ${ current.totalStars}→$newTotalStars " +
                            "totalRatings ${current.totalRatings}→$newTotalRatings avg→$newAvg")

                    _ratingStats.value = ProductRatingStats(
                        averageRating = newAvg,
                        totalRatings  = newTotalRatings,
                        totalStars    = newTotalStars
                    )
                    _userRating.value  = stars
                    _ratingState.value = RatingState.Success

                }.onFailure { e ->
                    Log.e(TAG, "submitRating failed: ${e.message}")
                    _ratingState.value = RatingState.Error(
                        e.message ?: "Không thể gửi đánh giá"
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "submitRating unexpected: ${e.message}", e)
                _ratingState.value = RatingState.Error("Đã xảy ra lỗi không mong muốn")
            }
        }
    }

    fun resetState() {
        Log.d(TAG, "resetState → Idle")
        _ratingState.value = RatingState.Idle
    }

    /**
     * Call when NAVIGATING AWAY from the product — not on recomposition.
     * Clears all state so next product loads fresh.
     * Do NOT call this from LaunchedEffect — it causes the reload loop.
     */
    fun clearProduct() {
        Log.d(TAG, "clearProduct() — was product=$loadedProductId")
        loadedProductId   = ""
        loadedUserId      = ""
        _ratingStats.value  = ProductRatingStats()
        _userRating.value   = 0
        _ratingState.value  = RatingState.Idle
        _isLoadingStats.value = false
    }
}
