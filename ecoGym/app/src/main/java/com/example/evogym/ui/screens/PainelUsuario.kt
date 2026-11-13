package com.example.evogym.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.theme.BrancoBackground
import com.example.evogym.ui.components.AvatarUsuario
import com.example.evogym.ui.components.SeletorProfessor
import com.example.evogym.ui.viewmodel.ProfessoresViewModel
import com.example.evogym.ui.viewmodel.UsuarioViewModel

@Composable
fun PainelUsuarioScreen(
    viewModel: UsuarioViewModel,
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    professoresViewModel: ProfessoresViewModel = viewModel()
) {
    val usuario = viewModel.usuario ?: return
    val professores = professoresViewModel.professores
    val professorVinculado = professores.firstOrNull { it.id == usuario.professorId }

    var exibirDialogoEditar by remember { mutableStateOf(false) }
    var exibirDialogoExcluir by remember { mutableStateOf(false) }
    var exibirOpcoesFoto by remember { mutableStateOf(false) }

    LaunchedEffect(professoresViewModel) { professoresViewModel.listar() }

    LaunchedEffect(viewModel.perfilSalvo) {
        if (viewModel.perfilSalvo) {
            exibirDialogoEditar = false
            viewModel.consumirSalvamento()
        }
    }

    val seletorFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.atualizarFoto(uri)
    }
    val seletorArquivo = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) viewModel.atualizarFoto(uri)
    }

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
                Box(contentAlignment = Alignment.BottomEnd) {
                    AvatarUsuario(
                        nome = usuario.nome,
                        foto = usuario.foto,
                        tamanho = 96.dp,
                        onClick = { if (!viewModel.carregando) exibirOpcoesFoto = true }
                    )
                    Surface(
                        shape = CircleShape,
                        color = AzulEscuro,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Alterar foto",
                            tint = Color.White,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
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
            InfoRow("Professor Vinculado", professorVinculado?.nome ?: "Sem professor")
            InfoRow("CEP", usuario.cep.ifBlank { "Não informado" })
            InfoRow("Endereço", usuario.endereco.ifBlank { "Não informado" })

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { exibirDialogoEditar = true },
                enabled = !viewModel.carregando && !professoresViewModel.carregando,
                colors = ButtonDefaults.buttonColors(containerColor = AzulTiffany),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar Perfil", color = Color.White)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { exibirDialogoExcluir = true },
                enabled = !viewModel.carregando,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Excluir Conta")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = viewModel::sair,
                enabled = !viewModel.carregando
            ) {
                Text("Sair da Conta", color = Color.Gray)
            }
        }
    }

    if (exibirOpcoesFoto) {
        AlertDialog(
            onDismissRequest = { exibirOpcoesFoto = false },
            title = { Text("Foto de perfil") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            exibirOpcoesFoto = false
                            seletorFoto.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text("Escolher da galeria", color = AzulEscuro)
                    }
                    if (usuario.foto != null) {
                        TextButton(
                            onClick = {
                                exibirOpcoesFoto = false
                                viewModel.atualizarFoto(null)
                            }
                        ) {
                            Text("Remover foto", color = Color(0xFFD32F2F))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { exibirOpcoesFoto = false }) { Text("Cancelar") }
            }
        )
    }

    if (exibirDialogoEditar) {
        var editNome by remember { mutableStateOf(usuario.nome) }
        var editIdade by remember { mutableStateOf(usuario.idade.toString()) }
        var editAltura by remember { mutableStateOf(usuario.altura.trim().removeSuffix("m").trim()) }
        var editPeso by remember { mutableStateOf(usuario.peso.trim().removeSuffix("kg").trim()) }
        var editPlano by remember { mutableStateOf(usuario.plano) }
        var editProfessorId by remember {
            mutableStateOf(
                if (professoresViewModel.mensagem == null) professorVinculado?.id else usuario.professorId
            )
        }

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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAltura,
                        onValueChange = { editAltura = it },
                        label = { Text("Altura (m)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPeso,
                        onValueChange = { editPeso = it },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPlano,
                        onValueChange = { editPlano = it },
                        label = { Text("Plano") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SeletorProfessor(
                        professores = professores,
                        selecionadoId = editProfessorId,
                        selecionar = { editProfessorId = it },
                        habilitado = !viewModel.carregando && professoresViewModel.mensagem == null
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.salvarPerfil(
                            nome = editNome,
                            idade = editIdade,
                            altura = editAltura,
                            peso = editPeso,
                            plano = editPlano,
                            professorId = editProfessorId
                        )
                    },
                    enabled = !viewModel.carregando,
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
                    onClick = viewModel::excluirConta,
                    enabled = !viewModel.carregando,
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, color = Color.Gray)
        Text(
            text = valor,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}
