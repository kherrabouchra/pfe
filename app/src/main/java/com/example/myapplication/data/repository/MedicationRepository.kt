package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Medication
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MedicationRepository {
    private val database = FirebaseDatabase.getInstance().reference
    private val medicationsRef = database.child("medications")

    fun observeMedications(): Flow<List<Medication>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medications = snapshot.children.mapNotNull { child ->
                    child.getValue(Map::class.java)?.let { map ->
                        Medication.fromMap(map as Map<String, Any?>)
                    }
                }
                trySend(medications)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                close(error.toException())
            }
        }

        medicationsRef.addValueEventListener(listener)
        awaitClose { medicationsRef.removeEventListener(listener) }
    }

    suspend fun addMedication(medication: Medication): Result<Unit> = try {
        val newMedicationRef = medicationsRef.push()
        val medicationWithId = medication.copy(id = newMedicationRef.key ?: "")
        newMedicationRef.setValue(medicationWithId.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateMedication(medication: Medication): Result<Unit> = try {
        medicationsRef.child(medication.id).setValue(medication.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteMedication(medicationId: String): Result<Unit> = try {
        medicationsRef.child(medicationId).removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getMedicationById(medicationId: String): Result<Medication?> = try {
        val snapshot = medicationsRef.child(medicationId).get().await()
        val medication = snapshot.getValue(Map::class.java)?.let { map ->
            Medication.fromMap(map as Map<String, Any?>)
        }
        Result.success(medication)
    } catch (e: Exception) {
        Result.failure(e)
    }
}