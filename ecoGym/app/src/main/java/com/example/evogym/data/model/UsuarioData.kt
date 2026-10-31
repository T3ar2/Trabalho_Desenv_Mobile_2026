package com.example.evogym.data.model

data class UsuarioData(
    val id: Long = 0,
    val nome: String,
    val email: String,
    val senha: String,
    val idade: Int = 25,
    val altura: String = "1.70 m",
    val peso: String = "70 kg",
    val plano: String = "Mensal",
    val professor: String = "Joan Mendes",
    val treinos: Int = 0,
    val seguidores: Int = 0,
    val diasSeguidos: Int = 0
)
