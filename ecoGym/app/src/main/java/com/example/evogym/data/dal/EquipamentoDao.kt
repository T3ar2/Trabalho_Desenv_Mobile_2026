package com.example.evogym.data.dal

import android.content.Context
import com.example.evogym.data.local.DatabaseHelper
import com.example.evogym.data.model.Equipamento

internal class EquipamentoDao(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(nome: String, categoria: String, quantidade: Int, status: String): Long =
        DatabaseHelper(appContext).use {
            it.inserirEquipamento(nome, categoria, quantidade, status)
        }

    fun listar(): List<Equipamento> =
        DatabaseHelper(appContext).use {
            it.listarEquipamentos()
        }

    fun alterar(equipamento: Equipamento): Int =
        DatabaseHelper(appContext).use {
            it.atualizarEquipamento(equipamento)
        }

    fun excluir(id: Long): Int =
        DatabaseHelper(appContext).use {
            it.excluirEquipamento(id)
        }
}