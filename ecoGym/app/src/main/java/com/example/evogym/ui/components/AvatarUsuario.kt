
package com.example.evogym.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.ui.theme.AzulTiffany
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AvatarUsuario(
    nome: String,
    foto: String?,
    modifier: Modifier = Modifier,
    tamanho: Dp = 72.dp,
    onClick: (() -> Unit)? = null
) {
    val tamanhoPx = with(LocalDensity.current) { tamanho.roundToPx().coerceAtLeast(1) }
    val imagem by key(foto, tamanhoPx) {
        produceState<ImageBitmap?>(initialValue = null) {
            if (foto != null) {
                value = withContext(Dispatchers.IO) {
                    carregarFoto(foto, tamanhoPx)?.asImageBitmap()
                }
            }
        }
    }

    Surface(
        modifier = modifier
            .size(tamanho)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = CircleShape,
        color = AzulTiffany
    ) {
        val fotoCarregada = imagem
        if (fotoCarregada != null) {
            Image(
                bitmap = fotoCarregada,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = nome.take(1).uppercase().ifEmpty { "U" },
                    color = Color.White,
                    fontSize = (tamanho.value * 0.39f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun carregarFoto(caminho: String, tamanhoPx: Int): Bitmap? {
    val opcoes = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(caminho, opcoes)
    if (opcoes.outWidth <= 0 || opcoes.outHeight <= 0) return null

    val maiorLado = maxOf(opcoes.outWidth, opcoes.outHeight)
    opcoes.inSampleSize = 1
    while (maiorLado / opcoes.inSampleSize / 2 >= tamanhoPx) {
        opcoes.inSampleSize *= 2
    }
    opcoes.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeFile(caminho, opcoes) ?: return null

    val orientacao = try {
        ExifInterface(caminho).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    } catch (_: IOException) {
        ExifInterface.ORIENTATION_NORMAL
    }
    val transformacao = Matrix()
    when (orientacao) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> transformacao.setScale(-1f, 1f)
        ExifInterface.ORIENTATION_ROTATE_180 -> transformacao.setRotate(180f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> transformacao.setScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            transformacao.setRotate(90f)
            transformacao.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_ROTATE_90 -> transformacao.setRotate(90f)
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            transformacao.setRotate(-90f)
            transformacao.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_ROTATE_270 -> transformacao.setRotate(-90f)
    }
    if (transformacao.isIdentity) return bitmap

    val ajustado = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, transformacao, true)
    if (ajustado !== bitmap) bitmap.recycle()
    return ajustado
}
