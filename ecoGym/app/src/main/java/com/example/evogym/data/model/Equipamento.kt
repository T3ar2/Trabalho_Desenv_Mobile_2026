package com.example.evogym.data.model

data class Equipamento(
    val id: Long = 0,
    val nome: String,
    val categoria: String,
    val quantidade: Int,
    val status: String
)
