package com.example.evogym.data.local

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Guarda a foto de perfil de cada usuário no armazenamento interno do app.
 * Copiar o arquivo (em vez de guardar só a Uri da galeria) garante que a foto
 * continue disponível depois de fechar o app ou reiniciar o celular.
 */
object FotoPerfilStorage {

    private fun prefixo(usuarioId: Long) = "perfil_${usuarioId}_"

    /** Copia a imagem escolhida para o app. Retorna o arquivo salvo ou null em caso de erro. */
    fun salvar(context: Context, usuarioId: Long, uri: Uri): File? {
        // Nome novo a cada troca: evita que o Coil mostre a foto antiga do cache.
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

    /** Apaga as fotos antigas do usuário, mantendo só o arquivo informado (se houver). */
    fun remover(context: Context, usuarioId: Long, manter: File? = null) {
        context.filesDir
            .listFiles { arquivo -> arquivo.name.startsWith(prefixo(usuarioId)) && arquivo != manter }
            ?.forEach { it.delete() }
    }
}
