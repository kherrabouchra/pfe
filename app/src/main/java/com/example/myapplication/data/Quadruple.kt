package com.example.myapplication.data

/**
 * Generic data class to hold four values of the same type
 */
data class Quadruple<T>(
    val first: T,
    val second: T,
    val third: T,
    val fourth: T
)