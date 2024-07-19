package com.example.projekatmobilne.Repository

import android.util.Log
import com.example.projekatmobilne.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(userId: String): User? {
        return try{
        Log.d("UserRepository", "Fetching user profile for userId: $userId")
        val document = firestore.collection("users").document(userId).get().await()
        Log.d("UserRepository", "Document snapshot: ${document.data}")

        if (document.exists()) {
            val user = document.toObject(User::class.java)
            Log.d("UserRepository", "Parsed user: $user")
            user
        } else {
            Log.e("UserRepository", "No document found for userId: $userId")
            null
        }
    } catch (e: Exception) {
        Log.e("UserRepository", "Error fetching user profile", e)
        null
    }
    }

    suspend fun addUserProfile(userId: String, user: User): Boolean {
        return try {
            firestore.collection("users").document(userId).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}