package com.example.evogym.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.evogym.ui.theme.AzulTiffany
import java.io.File

/**
 * Avatar circular do usuário: mostra a foto de perfil e, se não houver foto
 * (ou o arquivo não existir mais), a inicial do nome.
 */
@Composable
fun AvatarUsuario(
    nome: String,
    foto: String?,
    modifier: Modifier = Modifier,
    tamanho: Dp = 72.dp,
    onClick: (() -> Unit)? = null
) {
    val arquivo = remember(foto) { foto?.let { File(it) }?.takeIf { it.exists() } }

    Surface(
        modifier = modifier
            .size(tamanho)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = CircleShape,
        color = AzulTiffany
    ) {
        if (arquivo != null) {
            AsyncImage(
                model = arquivo,
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
