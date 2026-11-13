package com.example.evogym.data.dal

import android.content.Context
import com.example.evogym.data.local.DatabaseHelper
import com.example.evogym.data.model.Treino

internal class TreinoDao(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(treino: Treino): Long = DatabaseHelper(appContext).use { db ->
        db.inserirTreino(
            treino.exercicioId,
            treino.professorId,
            treino.usuarioId,
            treino.series,
            treino.repeticoes
        )
    }

    fun listar(): List<Treino> = DatabaseHelper(appContext).use { db ->
        db.listarTreinos()
    }

    fun alterar(treino: Treino): Int = DatabaseHelper(appContext).use { db ->
        db.atualizarTreino(treino)
    }

    fun excluir(id: Long): Int = DatabaseHelper(appContext).use { db ->
        db.excluirTreino(id)
    }
}
