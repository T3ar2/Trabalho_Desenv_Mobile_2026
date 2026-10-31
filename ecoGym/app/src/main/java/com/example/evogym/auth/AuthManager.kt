package com.example.evogym.auth

import android.content.Context
import com.example.evogym.data.local.DatabaseHelper
import com.example.evogym.data.model.UsuarioData

object AuthManager {
    var usuarioLogado: UsuarioData? = null
        private set

    fun login(context: Context, email: String, senha: String): Boolean {
        val db = DatabaseHelper(context)
        val usuario = db.autenticar(email.trim(), senha)
        return if (usuario != null) {
            usuarioLogado = usuario
            true
        } else {
            false
        }
    }

    fun cadastro(context: Context, nome: String, email: String, senha: String): Boolean {
        val db = DatabaseHelper(context)
        val resultado = db.inserirUsuario(nome.trim(), email.trim(), senha)
        return resultado != -1L
    }

    fun atualizarPerfil(
        nome: String,
        idade: Int,
        altura: String,
        peso: String,
        plano: String,
        professor: String,
        context: Context
    ): Boolean {
        val email = usuarioLogado?.email ?: return false
        val db = DatabaseHelper(context)
        val linhas = db.atualizarUsuario(
            emailOriginal = email,
            novoNome = nome,
            novaIdade = idade,
            novaAltura = altura,
            novoPeso = peso,
            novoPlano = plano,
            novoProfessor = professor
        )
        if (linhas > 0) {
            usuarioLogado = db.buscarPorEmail(email)
            return true
        }
        return false
    }

    fun excluirConta(context: Context): Boolean {
        val email = usuarioLogado?.email ?: return false
        val db = DatabaseHelper(context)
        val linhas = db.excluirUsuario(email)
        if (linhas > 0) {
            usuarioLogado = null
            return true
        }
        return false
    }

    fun logout() {
        usuarioLogado = null
    }
}
