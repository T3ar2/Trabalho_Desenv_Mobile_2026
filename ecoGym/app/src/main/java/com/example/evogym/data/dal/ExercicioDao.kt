package com.example.evogym.data.dal

import android.content.Context
import com.example.evogym.data.local.DatabaseHelper
import com.example.evogym.data.model.Exercicio
internal class ExercicioDao(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(nome: String, categoria: String): Long =
        DatabaseHelper(appContext).use { database ->
            database.inserirExercicio(nome, categoria)
        }

    fun listar(): List<Exercicio> = DatabaseHelper(appContext).use { database ->
        database.listarExercicios()
    }

    fun alterar(exercicio: Exercicio): Int = DatabaseHelper(appContext).use { database ->
        database.atualizarExercicio(exercicio)
    }

    fun excluir(id: Long): Int = DatabaseHelper(appContext).use { database ->
        database.excluirExercicio(id)
    }
}
