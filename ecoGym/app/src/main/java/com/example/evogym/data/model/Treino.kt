package com.example.evogym.data.model

data class Treino (
    val id: Long = 0,
    val exercicioId: Long = 0,
    val professorId: Long = 0,
    val usuarioId: Long? = null,
    val series: Int,
    val repeticoes: Int
)

data class TreinoDetalhado(
    val id: Long,
    val exercicioId: Long,
    val exercicioNome: String,
    val professorId: Long,
    val professorNome: String,
    val usuarioId: Long?,
    val usuarioNome: String?,
    val series: Int,
    val repeticoes: Int
)