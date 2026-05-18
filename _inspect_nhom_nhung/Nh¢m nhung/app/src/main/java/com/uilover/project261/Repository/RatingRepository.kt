package com.uilover.project261.Repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.uilover.project261.domain.ProductRatingStats
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "RatingRepository"

/**
 * ═══════════════════════════════════════════════════════════════════
 * ARCHITECTURE DECISION: one-shot reads + optimistic local updates
 * ═══════════════════════════════════════════════════════════════════
 *
 * WHY the previous realtime (callbackFlow) design was unstable:
 *
 * 1. LaunchedEffect(item.id, currentUserId) fires every time Auth
 *    emits a new UserModel object — even if the UID didn't change.
 *    Each fire calls reset() → cancels the live listener → restarts
 *    it. On slow auth resolution the listener flickers in/out.
 *
 * 2. The currentProductId == productId guard is neutralised by the
 *    reset() that precedes loadRatings() in every LaunchedEffect
 *    invocation. The guard never skips → always restarts.
 *
 * 3. addValueEventListener onCancelled emitted empty stats, wiping
 *    the UI to "Chưa có đánh giá" on any transient network blip.
 *
 * 4. Old schema had averageRating + totalRatings. New schema uses
 *    totalStars + totalRatings. Products rated before the migration
 *    have totalStars = null → avg computed as 0 → looks like no
 *    ratings even though data exists.
 *
 * NEW DESIGN:
 *   • getProductStats()  — one-shot read at product open
 *   • getUserRating()    — one-shot read at product open
 *   • submitRating()     — two isolated writes; WRITE 2 is non-fatal
 *   • ViewModel          — after success, updates stats optimistically
 *                          from local arithmetic (no re-fetch needed)
 *
 * This removes all listener lifecycle complexity while giving the
 * user immediate UI feedback and correct persistence.
 *
 * Rating stats do not need sub-second realtime updates — Shopee and
 * Lazada both use eventual-consistency for rating counts.
 * ═══════════════════════════════════════════════════════════════════
 */
class RatingRepository {

    companion object {
        private var persistenceEnabled = false

        /** Call once from Application.onCreate() before any DB access. */
        fun enablePersistence() {
            if (!persistenceEnabled) {
                try {
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true)
                    persistenceEnabled = true
                    Log.d(TAG, "Firebase disk persistence ENABLED ✅")
                } catch (e: Exception) {
                    Log.d(TAG, "Persistence already enabled (safe): ${e.message}")
                    persistenceEnabled = true
                }
            }
        }
    }

    private val db          = FirebaseDatabase.getInstance()
    private val ratingsRef  = db.getReference("ratings")
    private val statsRef    = db.getReference("productStats")

    // Generous budget: cold WebSocket + token refresh + write ≈ 10-15s
    private val WRITE_TIMEOUT_MS = 20_000L
    private val READ_TIMEOUT_MS  = 8_000L

    // ─────────────────────────────────────────────────────────────────
    // getProductStats — one-shot read
    //
    // Reads totalStars + totalRatings and computes average locally.
    // Falls back gracefully if node doesn't exist yet (new product).
    //
    // SCHEMA MIGRATION NOTE:
    // Old schema stored { averageRating, totalRatings }.
    // New schema stores { totalStars, totalRatings }.
    // We attempt totalStars first; if absent, fall back to
    // averageRating × totalRatings to reconstruct totalStars so old
    // data displays correctly without a migration script.
    // ─────────────────────────────────────────────────────────────────
    suspend fun getProductStats(productId: String): Result<ProductRatingStats> {
        return try {
            Log.d(TAG, "getProductStats: reading $productId")
            val stats = withTimeout(READ_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val ref = statsRef.child(productId)
                    val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val totalRatings = snapshot.child("totalRatings")
                                .getValue(Long::class.java) ?: 0L

                            // Try new schema field first
                            val totalStarsNode = snapshot.child("totalStars")
                                .getValue(Long::class.java)

                            val (totalStars, avg) = if (totalStarsNode != null) {
                                // ✅ New schema: totalStars field exists
                                val ts = totalStarsNode
                                val a  = if (totalRatings > 0)
                                    Math.round((ts.toDouble() / totalRatings) * 10.0) / 10.0
                                else 0.0
                                Pair(ts, a)
                            } else {
                                // ⚠️ Old schema fallback: reconstruct from averageRating
                                val legacyAvg = snapshot.child("averageRating")
                                    .getValue(Double::class.java) ?: 0.0
                                val reconstructedStars =
                                    Math.round(legacyAvg * totalRatings)
                                val a = if (totalRatings > 0) legacyAvg else 0.0
                                Log.w(TAG, "getProductStats: OLD schema detected for $productId " +
                                        "— legacyAvg=$legacyAvg reconstructedStars=$reconstructedStars")
                                Pair(reconstructedStars, a)
                            }

                            Log.d(TAG, "getProductStats: totalStars=$totalStars " +
                                    "totalRatings=$totalRatings avg=$avg")

                            val result = ProductRatingStats(
                                averageRating = avg,
                                totalRatings  = totalRatings.toInt(),
                                totalStars    = totalStars
                            )
                            if (continuation.isActive) continuation.resume(result)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "getProductStats cancelled: ${error.message}")
                            // Return empty stats — non-fatal, product still usable
                            if (continuation.isActive) continuation.resume(
                                ProductRatingStats()
                            )
                        }
                    }
                    ref.addListenerForSingleValueEvent(listener)
                    continuation.invokeOnCancellation { ref.removeEventListener(listener) }
                }
            }
            Result.success(stats)
        } catch (e: Exception) {
            Log.e(TAG, "getProductStats error (non-fatal): ${e.message}")
            Result.success(ProductRatingStats()) // return empty, don't crash
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // getUserRating — one-shot read
    //
    // Uses addListenerForSingleValueEvent (uses disk cache when
    // persistence is enabled) instead of .get() (always server).
    // ─────────────────────────────────────────────────────────────────
    suspend fun getUserRating(productId: String, userId: String): Result<Int> {
        return try {
            Log.d(TAG, "getUserRating: product=$productId user=$userId")
            val stars = withTimeout(READ_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val ref = ratingsRef.child(productId).child(userId).child("stars")
                    val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.getValue(Int::class.java) ?: 0
                            Log.d(TAG, "getUserRating: $value stars")
                            if (continuation.isActive) continuation.resume(value)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "getUserRating cancelled: ${error.message}")
                            if (continuation.isActive) continuation.resume(0)
                        }
                    }
                    ref.addListenerForSingleValueEvent(listener)
                    continuation.invokeOnCancellation { ref.removeEventListener(listener) }
                }
            }
            Result.success(stars)
        } catch (e: Exception) {
            Log.e(TAG, "getUserRating error (returning 0): ${e.message}")
            Result.success(0)
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // submitRating
    //
    // WRITE 1 — user rating node — FATAL (must succeed)
    // WRITE 2 — productStats increment — NON-FATAL (best-effort)
    //
    // Each write has its OWN isolated try/catch and timeout.
    // A WRITE 2 failure can never report false "no internet" for
    // a WRITE 1 that already succeeded.
    // ─────────────────────────────────────────────────────────────────
    suspend fun submitRating(
        productId: String,
        userId: String,
        stars: Int,
        existingStars: Int  // passed from ViewModel cache — zero network reads
    ): Result<Unit> {
        Log.d(TAG, "══ submitRating START — product=$productId stars=$stars existing=$existingStars ══")

        val userRatingRef = ratingsRef.child(productId).child(userId)
        val now = System.currentTimeMillis()

        // ── WRITE 1: user's rating (FATAL) ───────────────────────────
        Log.d(TAG, "WRITE 1 START")
        try {
            withTimeout(WRITE_TIMEOUT_MS) {
                suspendCancellableCoroutine { cont ->
                    val task = if (existingStars == 0) {
                        Log.d(TAG, "  → setValue() [first-time rater]")
                        userRatingRef.setValue(
                            mapOf(
                                "userId"    to userId,
                                "productId" to productId,
                                "stars"     to stars,
                                "createdAt" to now,
                                "updatedAt" to now
                            )
                        )
                    } else {
                        Log.d(TAG, "  → updateChildren() [re-rating, preserves createdAt]")
                        userRatingRef.updateChildren(
                            mapOf("stars" to stars, "updatedAt" to now)
                        )
                    }
                    task.addOnSuccessListener {
                        Log.d(TAG, "  WRITE 1 ACK ✅")
                        if (cont.isActive) cont.resume(Unit)
                    }.addOnFailureListener { ex ->
                        Log.e(TAG, "  WRITE 1 REJECTED: ${ex.message}")
                        if (cont.isActive) {
                            cont.resumeWithException(
                                if (ex.message?.contains("Permission denied") == true)
                                    PermissionDeniedException(ex.message!!)
                                else ex
                            )
                        }
                    }
                    cont.invokeOnCancellation {
                        Log.d(TAG, "  WRITE 1 coroutine cancelled")
                    }
                }
            }
            Log.d(TAG, "WRITE 1 SUCCESS ✅")
        } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "WRITE 1 TIMEOUT ⏰ (${WRITE_TIMEOUT_MS}ms) — no server ACK")
            return Result.failure(Exception("Mất kết nối mạng, vui lòng thử lại"))
        } catch (_: PermissionDeniedException) {
            Log.e(TAG, "WRITE 1 PERMISSION DENIED — rules: auth.uid === \$uid, userId=$userId")
            return Result.failure(Exception("Không có quyền đánh giá. Vui lòng đăng nhập lại"))
        } catch (e: Exception) {
            Log.e(TAG, "WRITE 1 ERROR: ${e.message}", e)
            return Result.failure(Exception("Không thể lưu đánh giá: ${e.message}"))
        }

        // ── WRITE 2: productStats increment (NON-FATAL) ──────────────
        val starsDelta   = (stars - existingStars).toLong()
        val ratingsDelta = if (existingStars == 0) 1L else 0L
        Log.d(TAG, "WRITE 2 START: starsDelta=$starsDelta ratingsDelta=$ratingsDelta")
        try {
            withTimeout(WRITE_TIMEOUT_MS) {
                suspendCancellableCoroutine { cont ->
                    statsRef.child(productId).updateChildren(
                        mapOf(
                            "totalStars"   to ServerValue.increment(starsDelta),
                            "totalRatings" to ServerValue.increment(ratingsDelta)
                        )
                    ).addOnSuccessListener {
                        Log.d(TAG, "  WRITE 2 ACK ✅")
                        if (cont.isActive) cont.resume(Unit)
                    }.addOnFailureListener { ex ->
                        Log.e(TAG, "  WRITE 2 REJECTED: ${ex.message}")
                        if (cont.isActive) cont.resumeWithException(ex)
                    }
                    cont.invokeOnCancellation { Log.d(TAG, "  WRITE 2 coroutine cancelled") }
                }
            }
            Log.d(TAG, "WRITE 2 SUCCESS ✅")
        } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
            Log.w(TAG, "WRITE 2 TIMEOUT ⚠️ — stats deferred, rating WAS saved")
        } catch (e: Exception) {
            Log.w(TAG, "WRITE 2 ERROR ⚠️ (non-fatal): ${e.message}")
        }

        Log.d(TAG, "══ submitRating COMPLETE ✅ ══")
        return Result.success(Unit)
    }
}

/** Typed exception so PermissionDenied shows a distinct user message. */
class PermissionDeniedException(message: String) : Exception(message)
