package com.example.evogym.data.dal

import android.content.Context
import com.example.evogym.data.local.DatabaseHelper
import com.example.evogym.data.model.UsuarioData

internal class CredenciaisUsuario(val id: Long, val hash: ByteArray, val salt: ByteArray)

internal class UsuarioDao(context: Context) {
    private companion object {
        val bloqueioBanco = Any()
    }

    private val appContext = context.applicationContext

    fun cadastrar(usuario: UsuarioData, hash: ByteArray, salt: ByteArray): Long =
        acessar { it.inserirUsuario(usuario, hash, salt) }

    fun buscarCredenciais(email: String): CredenciaisUsuario? =
        acessar { it.buscarCredenciais(email) }

    fun buscarPorId(id: Long): UsuarioData? =
        acessar { it.buscarUsuarioPorId(id) }

    fun alterar(usuario: UsuarioData): Int =
        acessar { it.atualizarUsuario(usuario) }

    fun alterarFoto(id: Long, caminho: String?): Int =
        acessar { it.atualizarFoto(id, caminho) }

    fun listar(): List<UsuarioData> = acessar { it.listarUsuarios() }

    fun excluir(id: Long): Int = acessar { it.excluirUsuario(id) }

    private fun <T> acessar(operacao: (DatabaseHelper) -> T): T = synchronized(bloqueioBanco) {
        DatabaseHelper(appContext).use(operacao)
    }
}
