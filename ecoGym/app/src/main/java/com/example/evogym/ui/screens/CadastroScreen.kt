package com.example.evogym.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evogym.ui.components.SeletorProfessor
import com.example.evogym.ui.theme.AzulTiffany
import com.example.evogym.ui.viewmodel.AuthViewModel
import com.example.evogym.ui.viewmodel.CepViewModel
import com.example.evogym.ui.viewmodel.ProfessoresViewModel
import kotlinx.coroutines.delay

@Composable
fun CadastroScreen(
    irLogin: () -> Unit,
    authViewModel: AuthViewModel,
    cepViewModel: CepViewModel = viewModel(),
    professoresViewModel: ProfessoresViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var idade by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var plano by remember { mutableStateOf("") }
    var professorId by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    val endereco = cepViewModel.endereco
    val podeEditar = !authViewModel.carregando && !authViewModel.cadastroConcluido

    LaunchedEffect(professoresViewModel) { professoresViewModel.listar() }

    LaunchedEffect(authViewModel.cadastroConcluido) {
        if (authViewModel.cadastroConcluido) {
            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            delay(1500)
            irLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cadastro:", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            enabled = podeEditar,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            enabled = podeEditar,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            enabled = podeEditar,
            visualTransformation = if (senhaVisivel) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                val imagem = if (senhaVisivel) Icons.Default.Visibility
                else Icons.Default.VisibilityOff
                val descricao = if (senhaVisivel) "Esconder Senha"
                else "Mostrar Senha"

                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = imagem, contentDescription = descricao)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CampoPerfil("Idade", idade, { idade = it }, podeEditar, KeyboardType.Number)
            CampoPerfil("Altura (m)", altura, { altura = it }, podeEditar, KeyboardType.Decimal)
            CampoPerfil("Peso (kg)", peso, { peso = it }, podeEditar, KeyboardType.Decimal)
            CampoPerfil("Plano", plano, { plano = it }, podeEditar)
            SeletorProfessor(
                professores = professoresViewModel.professores,
                selecionadoId = professorId,
                selecionar = { professorId = it },
                habilitado = podeEditar && !professoresViewModel.carregando &&
                    professoresViewModel.mensagem == null
            )
            professoresViewModel.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cepViewModel.cep,
            onValueChange = cepViewModel::mudarCep,
            label = { Text("CEP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = podeEditar && !cepViewModel.carregando,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = cepViewModel::consultar,
            enabled = podeEditar && !cepViewModel.carregando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar CEP")
        }

        if (cepViewModel.carregando) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        cepViewModel.mensagem?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        endereco?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Rua: ${it.rua?.ifBlank { "Não informado" } ?: "Não informado"}")
                Text("Bairro: ${it.bairro?.ifBlank { "Não informado" } ?: "Não informado"}")
                Text("Cidade: ${it.cidade.orEmpty()} / ${it.uf.orEmpty()}")
            }
        }

        authViewModel.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (authViewModel.carregando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                authViewModel.cadastrar(
                    nome = nome,
                    email = email,
                    senha = senha,
                    idade = idade,
                    altura = altura,
                    peso = peso,
                    plano = plano,
                    professorId = professorId,
                    cep = endereco?.cep ?: cepViewModel.cep,
                    endereco = endereco?.formatado().orEmpty()
                )
                senha = ""
            },
            enabled = podeEditar && !cepViewModel.carregando,
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulTiffany,
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }

        TextButton(onClick = irLogin, enabled = podeEditar) {
            Text("Já tenho cadastro.")
        }
    }
}

@Composable
private fun CampoPerfil(
    rotulo: String,
    valor: String,
    mudarValor: (String) -> Unit,
    habilitado: Boolean,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = mudarValor,
        label = { Text(rotulo) },
        enabled = habilitado,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = Modifier.fillMaxWidth()
    )
}
