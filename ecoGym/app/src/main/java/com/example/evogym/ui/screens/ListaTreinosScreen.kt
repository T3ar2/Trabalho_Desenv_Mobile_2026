package com.example.evogym.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.data.model.Exercicio
import com.example.evogym.data.model.Professor
import com.example.evogym.data.model.TreinoDetalhado
import com.example.evogym.data.model.UsuarioData
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.theme.BrancoBackground
import com.example.evogym.ui.viewmodel.TreinosViewModel

@Composable
fun ListaTreinosScreen(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    viewModel: TreinosViewModel
) {
    val context = LocalContext.current
    var textoBusca by remember { mutableStateOf("") }
    var formularioAberto by remember { mutableStateOf(false) }

    var idEmEdicao by remember { mutableStateOf(0L) }
    var exercicioIdSelecionado by remember { mutableStateOf<Long?>(null) }
    var professorIdSelecionado by remember { mutableStateOf<Long?>(null) }
    var usuarioIdSelecionado by remember { mutableStateOf<Long?>(null) }
    var series by remember { mutableStateOf("") }
    var repeticoes by remember { mutableStateOf("") }

    var treinoParaExcluir by remember { mutableStateOf<TreinoDetalhado?>(null) }

    LaunchedEffect(Unit) {
        viewModel.carregarDados()
    }

    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) {
            formularioAberto = false
            viewModel.consumirSalvo()
            Toast.makeText(context, "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    fun abrirFormulario(treino: TreinoDetalhado? = null) {
        idEmEdicao = treino?.id ?: 0L
        exercicioIdSelecionado = treino?.exercicioId
        professorIdSelecionado = treino?.professorId
        usuarioIdSelecionado = treino?.usuarioId
        series = treino?.series?.toString() ?: ""
        repeticoes = treino?.repeticoes?.toString() ?: ""
        formularioAberto = true
    }

    val treinosFiltrados = viewModel.treinos.filter { treino ->
        treino.exercicioNome.contains(textoBusca, ignoreCase = true) ||
            treino.professorNome.contains(textoBusca, ignoreCase = true) ||
            (treino.usuarioNome?.contains(textoBusca, ignoreCase = true) == true)
    }

    Column(modifier = modifier.fillMaxWidth().background(BrancoBackground)) {
        CabecalhoTreinos(
            onVoltarClick = onVoltarClick,
            onNovoClick = { abrirFormulario() }
        )

        if (formularioAberto) {
            FormularioTreino(
                idEmEdicao = idEmEdicao,
                exercicios = viewModel.exercicios,
                professores = viewModel.professores,
                alunos = viewModel.alunos,
                exercicioId = exercicioIdSelecionado,
                professorId = professorIdSelecionado,
                usuarioId = usuarioIdSelecionado,
                series = series,
                repeticoes = repeticoes,
                onExercicioSelecionado = { exercicioIdSelecionado = it },
                onProfessorSelecionado = { professorIdSelecionado = it },
                onUsuarioSelecionado = { usuarioIdSelecionado = it },
                onSeriesChange = { if (it.all { char -> char.isDigit() }) series = it },
                onRepeticoesChange = { if (it.all { char -> char.isDigit() }) repeticoes = it },
                onSalvar = {
                    viewModel.salvar(
                        id = idEmEdicao,
                        exercicioId = exercicioIdSelecionado,
                        professorId = professorIdSelecionado,
                        usuarioId = usuarioIdSelecionado,
                        seriesTexto = series,
                        repeticoesTexto = repeticoes
                    )
                },
                onCancelar = { formularioAberto = false }
            )
        }

        viewModel.mensagem?.let { mensagem ->
            Text(
                text = mensagem,
                color = if (mensagem.endsWith("excluído.")) Color.DarkGray else Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        OutlinedTextField(
            value = textoBusca,
            onValueChange = { textoBusca = it },
            placeholder = { Text("Buscar por exercício, professor ou aluno...", color = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = AzulEscuro,
                unfocusedTextColor = AzulEscuro,
                cursorColor = AzulTiffany
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(treinosFiltrados, key = { it.id }) { treino ->
                CardTreino(
                    treino = treino,
                    onEditar = { abrirFormulario(treino) },
                    onExcluir = { treinoParaExcluir = treino }
                )
            }
        }
    }

    treinoParaExcluir?.let { alvo ->
        AlertDialog(
            onDismissRequest = { treinoParaExcluir = null },
            title = { Text("Excluir treino") },
            text = { Text("Deseja realmente excluir o treino de \"${alvo.exercicioNome}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.excluir(alvo.id)
                        treinoParaExcluir = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { treinoParaExcluir = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CabecalhoTreinos(
    onVoltarClick: () -> Unit,
    onNovoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AzulEscuro)
            .padding(start = 16.dp, end = 8.dp, bottom = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onVoltarClick() }
            )
            Text(
                text = "Treinos",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            IconButton(onClick = onNovoClick) {
                Icon(Icons.Default.Add, "Cadastrar treino", tint = Color.White)
            }
        }
    }
}

@Composable
private fun FormularioTreino(
    idEmEdicao: Long,
    exercicios: List<Exercicio>,
    professores: List<Professor>,
    alunos: List<UsuarioData>,
    exercicioId: Long?,
    professorId: Long?,
    usuarioId: Long?,
    series: String,
    repeticoes: String,
    onExercicioSelecionado: (Long) -> Unit,
    onProfessorSelecionado: (Long) -> Unit,
    onUsuarioSelecionado: (Long?) -> Unit,
    onSeriesChange: (String) -> Unit,
    onRepeticoesChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (idEmEdicao == 0L) "Cadastrar Treino" else "Alterar Treino",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AzulEscuro
            )

            CaixaSelecao(
                label = "Exercício",
                textoSelecionado = exercicios.firstOrNull { it.id == exercicioId }?.nome ?: "Selecione o exercício",
                itens = exercicios.map { it.id to it.nome },
                onItemSelecionado = onExercicioSelecionado
            )

            CaixaSelecao(
                label = "Professor Instrutor",
                textoSelecionado = professores.firstOrNull { it.id == professorId }?.nome ?: "Selecione o professor",
                itens = professores.map { it.id to it.nome },
                onItemSelecionado = onProfessorSelecionado
            )

            CaixaSelecaoOpcional(
                label = "Aluno",
                textoSelecionado = alunos.firstOrNull { it.id == usuarioId }?.nome ?: "Sem aluno vinculado",
                itens = alunos.map { it.id to it.nome },
                onItemSelecionado = onUsuarioSelecionado
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = series,
                    onValueChange = onSeriesChange,
                    label = { Text("Séries") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = repeticoes,
                    onValueChange = onRepeticoesChange,
                    label = { Text("Repetições") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancelar) {
                    Text("Cancelar")
                }
                Button(
                    onClick = onSalvar,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulTiffany)
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}

@Composable
private fun CaixaSelecao(
    label: String,
    textoSelecionado: String,
    itens: List<Pair<Long, String>>,
    onItemSelecionado: (Long) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = textoSelecionado,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expandir $label"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = true }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expandido = true }
            )
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                itens.forEach { (id, nome) ->
                    DropdownMenuItem(
                        text = { Text(nome) },
                        onClick = {
                            onItemSelecionado(id)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CaixaSelecaoOpcional(
    label: String,
    textoSelecionado: String,
    itens: List<Pair<Long, String>>,
    onItemSelecionado: (Long?) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = textoSelecionado,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expandir $label"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = true }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expandido = true }
            )
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                DropdownMenuItem(
                    text = { Text("Sem aluno vinculado", color = Color.Gray) },
                    onClick = {
                        onItemSelecionado(null)
                        expandido = false
                    }
                )
                itens.forEach { (id, nome) ->
                    DropdownMenuItem(
                        text = { Text(nome) },
                        onClick = {
                            onItemSelecionado(id)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardTreino(
    treino: TreinoDetalhado,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = treino.exercicioNome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AzulEscuro
                )
                Text(
                    text = "Prof. ${treino.professorNome}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${treino.series} séries × ${treino.repeticoes} repetições",
                    color = AzulTiffany,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (!treino.usuarioNome.isNullOrBlank()) {
                    Text(
                        text = "Aluno: ${treino.usuarioNome}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, "Alterar treino", tint = AzulEscuro)
            }
            IconButton(onClick = onExcluir) {
                Icon(Icons.Default.Delete, "Excluir treino", tint = Color.Red)
            }
        }
    }
}
