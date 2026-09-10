package com.example.evogym.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.theme.BrancoBackground

data class Usuario(
    val nome: String,
    val idade: Int,
    val altura: String,
    val plano: String,
    val peso: String,
    val professor: String,
    val treinos: Int,
    val seguidores: Int,
    val diasSeguidos: Int
)

fun usuarioExemplo() = Usuario(
    nome = "Susana K.",
    idade = 27,
    altura = "1.65 m",
    plano = "Mensal",
    peso = "61 kg",
    professor = "Joan Mendes",
    treinos = 12,
    seguidores = 1,
    diasSeguidos = 4
)

@Composable
fun PainelUsuarioScreen(
    usuario: Usuario = usuarioExemplo(),
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrancoBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulEscuro)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .padding(end = 8.dp)
                        .clickable { onVoltarClick() }
                )
                Text(
                    text = "Painel do Usuário",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(50),
                color = AzulTiffany
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = usuario.nome.take(1),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(usuario.nome, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge("${usuario.treinos} Treinos")
                Badge("${usuario.seguidores} Seguidores")
                Badge("${usuario.diasSeguidos} Dias Seguidos")
            }

            Spacer(Modifier.height(24.dp))

            InfoRow("Nome", usuario.nome)
            InfoRow("Idade", "${usuario.idade} anos")
            InfoRow("Altura", usuario.altura)
            InfoRow("Plano", usuario.plano)
            InfoRow("Peso", usuario.peso)
            InfoRow("Professor Vinculado", usuario.professor)
        }
    }
}

@Composable
fun Badge(texto: String) {
    Surface(shape = RoundedCornerShape(50), color = AzulTiffany.copy(alpha = 0.15f)) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
            color = AzulEscuro
        )
    }
}

@Composable
fun InfoRow(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(valor, fontWeight = FontWeight.Medium)
    }
}