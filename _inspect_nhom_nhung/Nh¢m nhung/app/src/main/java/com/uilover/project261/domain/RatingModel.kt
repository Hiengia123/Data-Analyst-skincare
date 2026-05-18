package com.uilover.project261.domain

/**
 * Represents a single user's rating for a product.
 * Stored in Firebase Realtime Database:
 * ratings/{productId}/{userId} → RatingModel
 *
 * Why this structure is scalable:
 * - One node per user per product → auto-prevents duplicate ratings
 * - Direct key lookup O(1) to check if user already rated
 * - productStats/{productId} stores aggregated avg + count for fast reads
 */
data class RatingModel(
    val userId: String = "",
    val productId: String = "",
    val stars: Int = 0,           // 1–5 stars
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

/**
 * Aggregated rating stats per product.
 * Stored at: productStats/{productId}
 * Updated atomically whenever a user rates.
 */
data class ProductRatingStats(
    val averageRating: Double = 0.0,
    val totalRatings: Int = 0,
    val totalStars: Long = 0L   // stored in DB; average = totalStars / totalRatings
)
