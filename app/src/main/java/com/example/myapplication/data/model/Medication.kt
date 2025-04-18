package com.example.myapplication.data.model

import java.time.LocalDateTime

data class Medication(
    val id: String = "", // Firebase document ID
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "", // e.g., "daily", "twice daily", etc.
    val timeOfDay: List<LocalDateTime> = listOf(),
    val instructions: String = "", // e.g., "Take with food"
    val userId: String = "", // Reference to user
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // Convert to Map for Firebase
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "dosage" to dosage,
        "frequency" to frequency,
        "timeOfDay" to timeOfDay.map { it.toString() },
        "instructions" to instructions,
        "userId" to userId,
        "isActive" to isActive,
        "createdAt" to createdAt.toString(),
        "updatedAt" to updatedAt.toString()
    )

    companion object {
        // Create from Firebase data
        fun fromMap(map: Map<String, Any?>): Medication = Medication(
            id = map["id"] as? String ?: "",
            name = map["name"] as? String ?: "",
            dosage = map["dosage"] as? String ?: "",
            frequency = map["frequency"] as? String ?: "",
            timeOfDay = (map["timeOfDay"] as? List<*>)?.mapNotNull { 
                it?.toString()?.let { LocalDateTime.parse(it) }
            } ?: listOf(),
            instructions = map["instructions"] as? String ?: "",
            userId = map["userId"] as? String ?: "",
            isActive = map["isActive"] as? Boolean ?: true,
            createdAt = map["createdAt"]?.toString()?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now(),
            updatedAt = map["updatedAt"]?.toString()?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
        )
    }
}