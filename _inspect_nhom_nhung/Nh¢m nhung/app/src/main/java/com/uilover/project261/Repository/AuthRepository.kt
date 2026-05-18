package com.uilover.project261.Repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.uilover.project261.domain.UserModel
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance(
        "https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/"
    )

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Register with email and password
    suspend fun register(email: String, password: String, name: String): Result<UserModel> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Update display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Create user model
                val user = UserModel(
                    uid = firebaseUser.uid,
                    email = email,
                    name = name,
                    provider = "email",
                    createdAt = System.currentTimeMillis()
                )

                // Save to Realtime Database
                database.reference
                    .child("users")
                    .child(firebaseUser.uid)
                    .setValue(user)
                    .await()

                Log.d("AuthRepository", "User registered successfully: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Đăng ký thất bại"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration error", e)
            Result.failure(e)
        }
    }

    // Login with email and password
    suspend fun login(email: String, password: String): Result<UserModel> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Fetch user data from database
                val snapshot = database.reference
                    .child("users")
                    .child(firebaseUser.uid)
                    .get()
                    .await()

                val user = snapshot.getValue(UserModel::class.java) ?: UserModel(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: "",
                    provider = "email"
                )

                Log.d("AuthRepository", "Login successful: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Đăng nhập thất bại"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            Result.failure(e)
        }
    }

    // Get current user data
    suspend fun getCurrentUserData(): UserModel? {
        return try {
            val firebaseUser = currentUser ?: return null

            val snapshot = database.reference
                .child("users")
                .child(firebaseUser.uid)
                .get()
                .await()

            snapshot.getValue(UserModel::class.java)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting user data", e)
            null
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
        Log.d("AuthRepository", "User logged out")
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    // Update user profile
    suspend fun updateUserProfile(name: String, phone: String): Result<Boolean> {
        return try {
            val firebaseUser = currentUser ?: return Result.failure(Exception("Chưa đăng nhập"))

            // Update Firebase Auth display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Update Realtime Database
            val updates = mapOf(
                "name" to name,
                "phone" to phone
            )

            database.reference
                .child("users")
                .child(firebaseUser.uid)
                .updateChildren(updates)
                .await()

            Log.d("AuthRepository", "Profile updated successfully")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating profile", e)
            Result.failure(e)
        }
    }
}

