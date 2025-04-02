package com.example.myapplication

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseTest {
    private const val TAG = "FirebaseTest"
    
    fun testConnection() {
        val db = FirebaseFirestore.getInstance()
        db.collection("test")
            .document("connection")
            .set(mapOf("timestamp" to System.currentTimeMillis()))
            .addOnSuccessListener {
                Log.d(TAG, "Firebase connection successful!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firebase connection failed: ${e.message}")
            }
    }
}