package com.example.dompetkuapp.database

import java.io.Serializable

data class TransactionModel(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String,      // "PEMASUKAN" or "PENGELUARAN"
    val category: String,  // "Makanan", "Transportasi", "Gaji", dll.
    val date: String
) : Serializable
