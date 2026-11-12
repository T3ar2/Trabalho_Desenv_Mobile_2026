package com.example.evogym.data.dal

import android.content.Context
import com.example.evogym.data.local.DatabaseHelper
import com.example.evogym.data.model.Professor

internal class ProfessorDao(context: Context) {

    private val appContext = context.applicationContext

    fun cadastrar(nome: String, especialidade: String, foto: String = ""): Long =
        DatabaseHelper(appContext).use { database ->
            database.inserirProfessor(nome, especialidade, foto)
        }

    fun listar(): List<Professor> = DatabaseHelper(appContext).use { database ->
        database.listarProfessores()
    }

    fun alterar(professor: Professor): Int = DatabaseHelper(appContext).use { database ->
        database.atualizarProfessor(professor)
    }

    fun excluir(id: Long): Int = DatabaseHelper(appContext).use { database ->
        database.excluirProfessor(id)
    }
}