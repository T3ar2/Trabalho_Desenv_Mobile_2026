package com.example.evogym.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.theme.BrancoBackground
import com.example.evogym.auth.AuthManager

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
    onVoltarClick: () -> Unit = {},
    onDeslogarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val usuarioSessao = AuthManager.usuarioLogado

    val nomeAtual = usuarioSessao?.nome ?: usuario.nome
    val idadeAtual = usuarioSessao?.idade ?: usuario.idade
    val alturaAtual = usuarioSessao?.altura ?: usuario.altura
    val planoAtual = usuarioSessao?.plano ?: usuario.plano
    val pesoAtual = usuarioSessao?.peso ?: usuario.peso
    val professorAtual = usuarioSessao?.professor ?: usuario.professor
    val treinosAtual = usuarioSessao?.treinos ?: usuario.treinos
    val seguidoresAtual = usuarioSessao?.seguidores ?: usuario.seguidores
    val diasSeguidosAtual = usuarioSessao?.diasSeguidos ?: usuario.diasSeguidos

    var exibirDialogoEditar by remember { mutableStateOf(false) }
    var exibirDialogoExcluir by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
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
                        text = nomeAtual.take(1).ifEmpty { "U" },
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(nomeAtual, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge("$treinosAtual Treinos")
                Badge("$seguidoresAtual Seguidores")
                Badge("$diasSeguidosAtual Dias Seguidos")
            }

            Spacer(Modifier.height(24.dp))

            InfoRow("Nome", nomeAtual)
            InfoRow("Idade", "$idadeAtual anos")
            InfoRow("Altura", alturaAtual)
            InfoRow("Plano", planoAtual)
            InfoRow("Peso", pesoAtual)
            InfoRow("Professor Vinculado", professorAtual)

            Spacer(Modifier.height(32.dp))

            // crud buttons
            Button(
                onClick = { exibirDialogoEditar = true },
                colors = ButtonDefaults.buttonColors(containerColor = AzulTiffany),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar Perfil", color = Color.White)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { exibirDialogoExcluir = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Excluir Conta")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = {
                    AuthManager.logout()
                    Toast.makeText(context, "Sessão encerrada", Toast.LENGTH_SHORT).show()
                    onDeslogarClick()
                }
            ) {
                Text("Sair da Conta", color = Color.Gray)
            }
        }
    }

    if (exibirDialogoEditar) {
        var editNome by remember { mutableStateOf(nomeAtual) }
        var editIdade by remember { mutableStateOf(idadeAtual.toString()) }
        var editAltura by remember { mutableStateOf(alturaAtual) }
        var editPeso by remember { mutableStateOf(pesoAtual) }
        var editPlano by remember { mutableStateOf(planoAtual) }
        var editProfessor by remember { mutableStateOf(professorAtual) }

        AlertDialog(
            onDismissRequest = { exibirDialogoEditar = false },
            title = { Text("Editar Perfil") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editNome,
                        onValueChange = { editNome = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editIdade,
                        onValueChange = { editIdade = it },
                        label = { Text("Idade") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAltura,
                        onValueChange = { editAltura = it },
                        label = { Text("Altura") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPeso,
                        onValueChange = { editPeso = it },
                        label = { Text("Peso") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPlano,
                        onValueChange = { editPlano = it },
                        label = { Text("Plano") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProfessor,
                        onValueChange = { editProfessor = it },
                        label = { Text("Professor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idadeInt = editIdade.toIntOrNull() ?: idadeAtual
                        val sucesso = AuthManager.atualizarPerfil(
                            context = context,
                            nome = editNome,
                            idade = idadeInt,
                            altura = editAltura,
                            peso = editPeso,
                            plano = editPlano,
                            professor = editProfessor
                        )
                        if (sucesso) {
                            Toast.makeText(context, "Perfil atualizado.", Toast.LENGTH_SHORT).show()
                            exibirDialogoEditar = false
                        } else {
                            Toast.makeText(context, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulTiffany)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { exibirDialogoEditar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (exibirDialogoExcluir) {
        AlertDialog(
            onDismissRequest = { exibirDialogoExcluir = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir sua conta permanentemente do banco de dados?") },
            confirmButton = {
                Button(
                    onClick = {
                        val sucesso = AuthManager.excluirConta(context)
                        if (sucesso) {
                            Toast.makeText(context, "Conta excluída do SQLite com sucesso!", Toast.LENGTH_LONG).show()
                            exibirDialogoExcluir = false
                            onDeslogarClick()
                        } else {
                            Toast.makeText(context, "Erro ao excluir conta", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Sim, excluir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { exibirDialogoExcluir = false }) {
                    Text("Cancelar")
                }
            }
        )
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
