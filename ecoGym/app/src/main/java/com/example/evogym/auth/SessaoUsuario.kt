package com.example.evogym.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.evogym.data.model.UsuarioData

internal object SessaoUsuario {
    var usuario by mutableStateOf<UsuarioData?>(null)
        private set

    fun iniciar(usuarioAutenticado: UsuarioData) {
        usuario = usuarioAutenticado
    }

    fun atualizar(usuarioAtualizado: UsuarioData) {
        if (usuario?.id == usuarioAtualizado.id) usuario = usuarioAtualizado
    }

    fun encerrar() {
        usuario = null
    }
}
