package com.example.myapplication.data.repository

import com.example.myapplication.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(
    private val firestore: FirebaseFirestore
) {
    private val userProfileFlow = MutableStateFlow<UserProfile?>(null)
    
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val profile = snapshot.toObject(UserProfile::class.java)
            userProfileFlow.value = profile
            profile
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateUserProfile(userProfile: UserProfile): Boolean {
        return try {
            firestore.collection("users").document(userProfile.userId)
                .set(userProfile).await()
            userProfileFlow.value = userProfile
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun observeUserProfile(): Flow<UserProfile?> {
        return userProfileFlow.asStateFlow()
    }
}