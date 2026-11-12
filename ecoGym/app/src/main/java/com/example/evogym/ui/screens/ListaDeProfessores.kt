package com.example.evogym.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.data.model.Professor
import com.example.evogym.ui.viewmodel.ProfessoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDeProfessores(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    viewModel: ProfessoresViewModel
) {
    val context = LocalContext.current
    var textoBusca by remember { mutableStateOf("") }
    var filtroSelecionado by remember { mutableStateOf("Todos") }
    var formularioAberto by remember { mutableStateOf(false) }
    var idEmEdicao by remember { mutableStateOf(0L) }
    var nome by remember { mutableStateOf("") }
    var especialidade by remember { mutableStateOf("") }
    var paraExcluir by remember { mutableStateOf<Professor?>(null) }

    LaunchedEffect(Unit) { viewModel.listar() }

    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) {
            formularioAberto = false
            viewModel.consumirSalvo()
            Toast.makeText(context, "Professor salvo com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(viewModel.mensagem) {
        viewModel.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumirMensagem()
        }
    }

    val listaFiltrada = viewModel.professores.filter { professor ->
        professor.nome.contains(textoBusca, ignoreCase = true) &&
            (filtroSelecionado == "Todos" ||
                professor.especialidade.contains(filtroSelecionado, ignoreCase = true))
    }

    fun abrirFormulario(professor: Professor? = null) {
        idEmEdicao = professor?.id ?: 0L
        nome = professor?.nome ?: ""
        especialidade = professor?.especialidade ?: ""
        formularioAberto = true
    }

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F7))
        ) {
            // ---------- Top bar ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2B2B44))
                    .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.clickable { onVoltarClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Lista de Professores",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { abrirFormulario() }) {
                        Icon(Icons.Default.Add, "Cadastrar professor", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = textoBusca,
                    onValueChange = { textoBusca = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar professor", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Todos", "Crossfit", "Cardio", "Mobilidade").forEach { filtro ->
                        Button(
                            onClick = { filtroSelecionado = filtro },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (filtroSelecionado == filtro) Color(0xFF26A69A)
                                else Color(0xFF40405E)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(filtro)
                        }
                    }
                }
            }

            // ---------- Formulário ----------
            if (formularioAberto) {
                FormularioProfessor(
                    idEmEdicao = idEmEdicao,
                    nome = nome,
                    especialidade = especialidade,
                    onNomeChange = { nome = it },
                    onEspecialidadeChange = { especialidade = it },
                    onSalvar = { viewModel.salvar(idEmEdicao, nome, especialidade) },
                    onCancelar = { formularioAberto = false }
                )
            }

            // ---------- Lista ----------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp, vertical = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaFiltrada) { professor ->
                    CardProfessor(
                        professor = professor,
                        onEditar = { abrirFormulario(professor) },
                        onExcluir = { paraExcluir = professor }
                    )
                }
            }
        }
    }

    // ---------- Confirmação de exclusão ----------
    paraExcluir?.let { professor ->
        AlertDialog(
            onDismissRequest = { paraExcluir = null },
            title = { Text("Excluir professor") },
            text = { Text("Deseja realmente excluir ${professor.nome}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.excluir(professor.id)
                    paraExcluir = null
                }) { Text("Excluir", color = Color(0xFFC62828)) }
            },
            dismissButton = {
                TextButton(onClick = { paraExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FormularioProfessor(
    idEmEdicao: Long,
    nome: String,
    especialidade: String,
    onNomeChange: (String) -> Unit,
    onEspecialidadeChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                if (idEmEdicao == 0L) "Cadastrar professor" else "Alterar professor",
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                nome, onNomeChange,
                label = { Text("Nome do professor") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                especialidade, onEspecialidadeChange,
                label = { Text("Especialidade") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancelar) { Text("Cancelar") }
                Button(onClick = onSalvar) { Text("Salvar") }
            }
        }
    }
}

@Composable
private fun CardProfessor(
    professor: Professor,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    val context = LocalContext.current

    // Resolve o drawable pelo nome guardado no banco ("joan_mendes", etc.).
    val fotoId = remember(professor.foto) {
        if (professor.foto.isBlank()) 0
        else context.resources.getIdentifier(
            professor.foto, "drawable", context.packageName
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fotoId != 0) {
                Image(
                    painter = painterResource(id = fotoId),
                    contentDescription = "Foto",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = professor.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E1E38)
                )
                Text(
                    text = professor.especialidade,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = Color(0xFF26A69A),
                modifier = Modifier
                    .clickable { onEditar() }
                    .padding(4.dp)
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Excluir",
                tint = Color(0xFFC62828),
                modifier = Modifier
                    .clickable { onExcluir() }
                    .padding(4.dp)
            )
        }
    }
}