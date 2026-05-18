package com.uilover.project261.domain

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val provider: String = "email", // email, google, facebook
    val createdAt: Long = System.currentTimeMillis()
)

