package com.example.evogym.data.local

import android.content.Context
import android.net.Uri
import java.io.File

object FotoPerfilStorage {

    private fun prefixo(usuarioId: Long) = "perfil_${usuarioId}_"

    fun salvar(context: Context, usuarioId: Long, uri: Uri): File? {
        val destino = File(context.filesDir, "${prefixo(usuarioId)}${System.currentTimeMillis()}.img")
        return try {
            val entrada = context.contentResolver.openInputStream(uri)
            if (entrada == null) {
                null
            } else {
                entrada.use { origem -> destino.outputStream().use { origem.copyTo(it) } }
                destino
            }
        } catch (e: Exception) {
            destino.delete()
            null
        }
    }

    fun remover(context: Context, usuarioId: Long, manter: File? = null) {
        context.filesDir
            .listFiles { arquivo -> arquivo.name.startsWith(prefixo(usuarioId)) && arquivo != manter }
            ?.forEach { it.delete() }
    }
}