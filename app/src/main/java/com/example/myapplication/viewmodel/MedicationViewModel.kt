package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Medication
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class MedicationViewModel(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ViewModel() {
    private val medicationsRef = database.reference.child("medications")

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MedicationViewModel() as T
            }
        }
    }

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeMedications()
    }

    private fun observeMedications() {
        medicationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicationList = snapshot.children.mapNotNull { child ->
                    val genericTypeIndicator = object : com.google.firebase.database.GenericTypeIndicator<Map<String, Any?>>() {}
                    child.getValue(genericTypeIndicator)?.let { map ->
                        Medication.fromMap(map)
                    }
                }
                _medications.value = medicationList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun addMedication(
        name: String,
        dosage: String,
        frequency: String,
        timeOfDay: List<LocalDateTime>,
        instructions: String,
        userId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newMedicationRef = medicationsRef.push()
                val medication = Medication(
                    id = newMedicationRef.key ?: "",
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    timeOfDay = timeOfDay,
                    instructions = instructions,
                    userId = userId
                )
                newMedicationRef.setValue(medication.toMap()).await()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedication(medication: Medication) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                medicationsRef.child(medication.id).setValue(medication.toMap()).await()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                medicationsRef.child(medicationId).removeValue().await()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicationsForUser(userId: String): List<Medication> {
        return medications.value.filter { it.userId == userId }
    }
    
    fun getMedicationsForDate(date: java.time.LocalDate): List<Medication> {
        return medications.value.filter { medication ->
            medication.timeOfDay.any { dateTime ->
                dateTime.toLocalDate() == date
            }
        }
    }
}