package com.example.evogym.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import com.example.evogym.data.model.Equipamento
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.theme.BrancoBackground
import com.example.evogym.ui.viewmodel.EquipamentosViewModel

private val CATEGORIAS = listOf("Cardio", "Musculação", "Funcional")
private val STATUS = listOf("Disponível", "Manutenção")

@Composable
fun ListaEquipamentosScreen(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    viewModel: EquipamentosViewModel
) {
    val context = LocalContext.current
    var textoBusca by remember { mutableStateOf("") }
    var formularioAberto by remember { mutableStateOf(false) }
    var idEmEdicao by remember { mutableStateOf(0L) }
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CATEGORIAS.first()) }
    var quantidade by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(STATUS.first()) }
    var equipamentoParaExcluir by remember { mutableStateOf<Equipamento?>(null) }

    LaunchedEffect(Unit) { viewModel.listar() }

    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) {
            formularioAberto = false
            viewModel.consumirSalvo()
            Toast.makeText(context, "Equipamento salvo com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    val listaFiltrada = viewModel.equipamentos.filter { equipamento ->
        equipamento.nome.contains(textoBusca, ignoreCase = true)
    }

    fun abrirFormulario(equipamento: Equipamento? = null) {
        idEmEdicao = equipamento?.id ?: 0L
        nome = equipamento?.nome ?: ""
        categoria = equipamento?.categoria ?: CATEGORIAS.first()
        quantidade = equipamento?.quantidade?.toString() ?: ""
        status = equipamento?.status ?: STATUS.first()
        formularioAberto = true
    }

    Column(modifier = modifier.fillMaxWidth().background(BrancoBackground)) {
        CabecalhoEquipamentos(
            onVoltarClick = onVoltarClick,
            onNovoClick = { abrirFormulario() }
        )

        if (formularioAberto) {
            FormularioEquipamento(
                idEmEdicao = idEmEdicao,
                nome = nome,
                categoria = categoria,
                quantidade = quantidade,
                status = status,
                onNomeChange = { nome = it },
                onCategoriaChange = { categoria = it },
                onQuantidadeChange = { novo -> if (novo.all { it.isDigit() }) quantidade = novo },
                onStatusChange = { status = it },
                onSalvar = { viewModel.salvar(idEmEdicao, nome, categoria, quantidade, status) },
                onCancelar = { formularioAberto = false }
            )
        }

        viewModel.mensagem?.let { mensagem ->
            Text(
                mensagem,
                color = if (mensagem.endsWith("excluído.")) Color.DarkGray else Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        OutlinedTextField(
            value = textoBusca,
            onValueChange = { textoBusca = it },
            placeholder = { Text("Buscar equipamento...", color = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray,
                focusedTextColor = AzulEscuro, unfocusedTextColor = AzulEscuro, cursorColor = AzulTiffany
            ),
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(listaFiltrada, key = { it.id }) { equipamento ->
                CardEquipamento(
                    equipamento = equipamento,
                    onEditar = { abrirFormulario(equipamento) },
                    onExcluir = { equipamentoParaExcluir = equipamento }
                )
            }
        }
    }

    equipamentoParaExcluir?.let { alvo ->
        AlertDialog(
            onDismissRequest = { equipamentoParaExcluir = null },
            title = { Text("Excluir equipamento") },
            text = { Text("Deseja realmente excluir \"${alvo.nome}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.excluir(alvo.id)
                        equipamentoParaExcluir = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Excluir", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { equipamentoParaExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CabecalhoEquipamentos(
    onVoltarClick: () -> Unit,
    onNovoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(AzulEscuro)
            .padding(start = 16.dp, end = 8.dp, bottom = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.ArrowBack, "Voltar", tint = Color.White,
                modifier = Modifier.size(26.dp).clickable { onVoltarClick() }
            )
            Text(
                "Equipamentos", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(start = 5.dp)
            )
            IconButton(onClick = onNovoClick) {
                Icon(Icons.Default.Add, "Cadastrar equipamento", tint = Color.White)
            }
        }
    }
}

@Composable
private fun FormularioEquipamento(
    idEmEdicao: Long,
    nome: String,
    categoria: String,
    quantidade: String,
    status: String,
    onNomeChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onQuantidadeChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                if (idEmEdicao == 0L) "Cadastrar equipamento" else "Alterar equipamento",
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                nome, onNomeChange,
                label = { Text("Nome do equipamento") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text("Categoria", fontSize = 12.sp, color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CATEGORIAS.forEach { opcao ->
                    FilterChip(
                        selected = categoria == opcao,
                        onClick = { onCategoriaChange(opcao) },
                        label = { Text(opcao) }
                    )
                }
            }
            OutlinedTextField(
                quantidade, onQuantidadeChange,
                label = { Text("Quantidade") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text("Status", fontSize = 12.sp, color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                STATUS.forEach { opcao ->
                    FilterChip(
                        selected = status == opcao,
                        onClick = { onStatusChange(opcao) },
                        label = { Text(opcao) }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancelar) { Text("Cancelar") }
                Button(onClick = onSalvar) { Text("Salvar") }
            }
        }
    }
}

@Composable
private fun CardEquipamento(equipamento: Equipamento, onEditar: () -> Unit, onExcluir: () -> Unit) {
    val disponivel = equipamento.status == "Disponível"
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.background(Color.White).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(equipamento.nome, fontWeight = FontWeight.Bold)
                Text(
                    "${equipamento.categoria} • ${equipamento.quantidade} un.",
                    color = AzulTiffany, fontSize = 12.sp
                )
                Text(
                    equipamento.status,
                    color = if (disponivel) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                    fontSize = 12.sp, fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, "Alterar equipamento", tint = AzulEscuro)
            }
            IconButton(onClick = onExcluir) {
                Icon(Icons.Default.Delete, "Excluir equipamento", tint = Color.Red)
            }
        }
    }
}
