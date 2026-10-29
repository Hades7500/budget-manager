package com.example.passwordmanager.data.model

data class TransactionModel(
    val id: Long = 0,
    val title: String,
    val category: String,
    val amount: Double,
    val type: String = "EXPENSE",
    val paymentMethod: String = "Card",
    val timestamp: Long = System.currentTimeMillis(),
    val year: Int = 2026,
    val month: Int = 10, // 10 = October
    val day: Int = 3
)