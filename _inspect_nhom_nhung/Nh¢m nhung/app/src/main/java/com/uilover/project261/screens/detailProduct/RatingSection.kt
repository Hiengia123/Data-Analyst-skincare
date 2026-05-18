package com.uilover.project261.screens.detailProduct

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.R
import com.uilover.project261.domain.ProductRatingStats
import com.uilover.project261.viewModel.RatingState
import com.uilover.project261.viewModel.RatingViewModel
import java.util.Locale

/**
 * Full rating section composable – displayed inside Product Detail screen.
 *
 * Shows:
 *  - Average rating + total reviews
 *  - 5 interactive star buttons (only for logged-in users)
 *  - Submission feedback
 */
@Composable
fun RatingSection(
    ratingViewModel: RatingViewModel,
    userId: String?,           // null = not logged in
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ratingStats by ratingViewModel.ratingStats.collectAsState()
    val userRating by ratingViewModel.userRating.collectAsState()
    val ratingState by ratingViewModel.ratingState.collectAsState()

    // Temp selected stars while user is choosing (before submit)
    var hoverStars by remember { mutableIntStateOf(0) }

    // Show toast on success/error
    LaunchedEffect(ratingState) {
        when (ratingState) {
            is RatingState.Success -> {
                Toast.makeText(context, "Đánh giá của bạn đã được lưu! ⭐", Toast.LENGTH_SHORT).show()
                ratingViewModel.resetState()
                hoverStars = 0
            }
            is RatingState.Error -> {
                Toast.makeText(context, (ratingState as RatingState.Error).message, Toast.LENGTH_SHORT).show()
                ratingViewModel.resetState()
            }
            else -> {}
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // ── Section header ──────────────────────────────
            Text(
                text = "Đánh giá sản phẩm",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Average rating display ───────────────────────
            AverageRatingDisplay(stats = ratingStats)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = Color(0xFFF0F0F0)
            )

            // ── Interactive stars for logged-in users ────────
            if (userId != null) {
                UserRatingInput(
                    currentRating = if (hoverStars > 0) hoverStars else userRating,
                    isLoading = ratingState is RatingState.Loading,
                    hasRated = userRating > 0,
                    onHover = { stars -> hoverStars = stars },
                    onRate = { stars ->
                        hoverStars = 0
                        ratingViewModel.submitRating(userId, stars)
                    }
                )
            } else {
                // Prompt for guest users
                Text(
                    text = "Đăng nhập để đánh giá sản phẩm này",
                    fontSize = 14.sp,
                    color = colorResource(R.color.primary_pink),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Shows big average score + total reviews + display-only star row.
 * Example:  4.7 ★  (128 đánh giá)
 */
@Composable
private fun AverageRatingDisplay(stats: ProductRatingStats) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Big number
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                    text = if (stats.totalRatings == 0) "–" else
                    String.format(Locale.getDefault(), "%.1f", stats.averageRating),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "/ 5",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.offset(y = (-4).dp)
            )
        }

        // Stars + count
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Display-only star row
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    val filled = stats.averageRating >= (index + 1)
                    val half = !filled && stats.averageRating > index
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (filled || half) Color(0xFFFFC107) else Color(0xFFDDDDDD),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Text(
                text = if (stats.totalRatings == 0) "Chưa có đánh giá"
                else "${stats.totalRatings} đánh giá",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * Interactive star input – only shown to logged-in users.
 * Tapping a star immediately submits (Shopee-style).
 */
@Composable
private fun UserRatingInput(
    currentRating: Int,
    isLoading: Boolean,
    hasRated: Boolean,
    onHover: (Int) -> Unit,
    onRate: (Int) -> Unit
) {
    Column {
        Text(
            text = if (hasRated) "Đánh giá của bạn:" else "Chạm vào sao để đánh giá:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF555555)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                val starIndex = index + 1
                val isSelected = currentRating >= starIndex

                // Animate star size for tactile feedback
                val starSize by animateDpAsState(
                    targetValue = if (isSelected) 38.dp else 32.dp,
                    animationSpec = tween(150),
                    label = "starSize"
                )
                // Animate star color
                val starColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFFFFC107) else Color(0xFFDDDDDD),
                    animationSpec = tween(150),
                    label = "starColor"
                )

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Đánh giá $starIndex sao",
                    tint = starColor,
                    modifier = Modifier
                        .size(starSize)
                        .clickable(enabled = !isLoading) {
                            onHover(starIndex)
                            onRate(starIndex)
                        }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Loading spinner while submitting
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = colorResource(R.color.primary_pink)
                )
            } else if (currentRating > 0) {
                // Star label
                Text(
                    text = starLabel(currentRating),
                    fontSize = 13.sp,
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun starLabel(stars: Int): String = when (stars) {
    1 -> "Rất tệ 😞"
    2 -> "Tệ 😐"
    3 -> "Bình thường 🙂"
    4 -> "Tốt 😊"
    5 -> "Tuyệt vời! 🤩"
    else -> ""
}
