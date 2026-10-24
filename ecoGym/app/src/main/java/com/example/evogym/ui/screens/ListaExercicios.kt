package com.example.evogym.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.data.model.Exercicio
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.theme.BrancoBackground
import com.example.evogym.ui.viewmodel.ExerciciosViewModel

@Composable
fun ListaExerciciosScreen(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    viewModel: ExerciciosViewModel
) {
    val context = LocalContext.current
    var textoBusca by remember { mutableStateOf("") }
    var filtroSelecionado by remember { mutableStateOf<String?>(null) }
    var formularioAberto by remember { mutableStateOf(false) }
    var idEmEdicao by remember { mutableStateOf(0L) }
    var nome by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Força") }

    LaunchedEffect(Unit) { viewModel.listar() }

    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) {
            formularioAberto = false
            viewModel.consumirSalvo()
            Toast.makeText(context, "Exercício salvo com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    val listaFiltrada = viewModel.exercicios.filter { exercicio ->
        exercicio.nome.contains(textoBusca, ignoreCase = true) &&
            (filtroSelecionado == null || exercicio.categoria == filtroSelecionado)
    }

    fun abrirFormulario(exercicio: Exercicio? = null) {
        idEmEdicao = exercicio?.id ?: 0L
        nome = exercicio?.nome ?: ""
        categoria = exercicio?.categoria ?: "Força"
        formularioAberto = true
    }

    Column(modifier = modifier.fillMaxWidth().background(BrancoBackground)) {
        CabecalhoExercicios(
            filtroSelecionado = filtroSelecionado,
            onFiltroClick = { filtro -> filtroSelecionado = if (filtroSelecionado == filtro) null else filtro },
            onVoltarClick = onVoltarClick,
            onNovoClick = { abrirFormulario() }
        )

        if (formularioAberto) {
            FormularioExercicio(
                idEmEdicao, nome, professor, categoria,
                onNomeChange = { nome = it },
                onProfessorChange = { professor = it },
                onCategoriaChange = { categoria = it },
                onSalvar = {
                    viewModel.salvar(idEmEdicao, nome, categoria)
                },
                onCancelar = { formularioAberto = false }
            )
        }

        viewModel.mensagem?.let { mensagem ->
            Text(mensagem, color = Color.Red, modifier = Modifier.padding(horizontal = 20.dp))
        }

        OutlinedTextField(
            value = textoBusca,
            onValueChange = { textoBusca = it },
            placeholder = { Text("Buscar exercício...", color = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray,
                focusedTextColor = AzulEscuro, unfocusedTextColor = AzulEscuro, cursorColor = AzulTiffany
            ),
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(listaFiltrada, key = { it.id }) { exercicio ->
                CardExercicio(
                    exercicio = exercicio,
                    onEditar = { abrirFormulario(exercicio) },
                    onExcluir = { viewModel.excluir(exercicio.id) }
                )
            }
        }
    }
}

@Composable
private fun CabecalhoExercicios(
    filtroSelecionado: String?, onFiltroClick: (String) -> Unit,
    onVoltarClick: () -> Unit, onNovoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(46, 45, 77))
            .padding(start = 16.dp, end = 8.dp, bottom = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack, "Voltar", tint = Color.White,
                modifier = Modifier.size(26.dp).clickable { onVoltarClick() }
            )
            Text(
                "Lista Exercícios", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(start = 5.dp)
            )
            IconButton(onClick = onNovoClick) { Icon(Icons.Default.Add, "Cadastrar exercício", tint = Color.White) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Força", "Mobilidade", "Resistência").forEach { filtro ->
                Button(
                    onClick = { onFiltroClick(filtro) }, shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroSelecionado == filtro) AzulTiffany else Color.White.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(1.dp, if (filtroSelecionado == filtro) Color.Transparent else Color.White.copy(alpha = 0.1f))
                ) { Text(filtro) }
            }
        }
    }
}

@Composable
private fun FormularioExercicio(
    idEmEdicao: Long, nome: String, professor: String, categoria: String,
    onNomeChange: (String) -> Unit, onProfessorChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit, onSalvar: () -> Unit, onCancelar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(if (idEmEdicao == 0L) "Cadastrar exercício" else "Alterar exercício", fontWeight = FontWeight.Bold)
            OutlinedTextField(nome, onNomeChange, label = { Text("Nome do exercício") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(categoria, onCategoriaChange, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancelar) { Text("Cancelar") }
                Button(onClick = onSalvar) { Text("Salvar") }
            }
        }
    }
}

@Composable
private fun CardExercicio(exercicio: Exercicio, onEditar: () -> Unit, onExcluir: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.background(Color.White).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercicio.nome, fontWeight = FontWeight.Bold)
                Text(exercicio.categoria, color = AzulTiffany, fontSize = 12.sp)
            }
            IconButton(onClick = {
                Toast.makeText(context, "Abrindo vídeo: ${exercicio.nome}", Toast.LENGTH_SHORT).show()
            }) { Icon(Icons.Default.PlayArrow, "Tocar exercício", tint = AzulTiffany) }
            IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, "Alterar exercício", tint = AzulEscuro) }
            IconButton(onClick = onExcluir) { Icon(Icons.Default.Delete, "Excluir exercício", tint = Color.Red) }
        }
    }
}
