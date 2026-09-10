package com.example.evogym

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Professor(
    val nome: String,
    val especialidade: String,
    val imagemId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDeProfessores(
    modifier: Modifier,
    onVoltarClick: () -> Unit = {}
) {
    val listaProfessores = listOf(

        Professor(
            nome = "Joan Mendes",
            especialidade = "Crossfit e Força",
            imagemId = R.drawable.joan_mendes
        ),

        Professor(
            nome = "Ana Costa",
            especialidade = "Ginástica & Mobilidade",
            imagemId = R.drawable.ana_costa
        ),

        Professor(
            nome = "Pedro Lima",
            especialidade = "Cardio e Ritmo",
            imagemId = R.drawable.pedro_lima
        ),


        Professor(
            nome = "Joana Mendes",
            especialidade = "Flexibilidade e Prevenção",
            imagemId = R.drawable.joan_mendes2
        )
    )

    var textoBusca by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
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
                        modifier = modifier
                            .clickable { onVoltarClick() }

                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Lista de Professores",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Todos")
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40405E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Crossfit")
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40405E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mobilidade")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F9))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = textoBusca,
                onValueChange = { textoBusca = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar professor...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ícone Busca",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaProfessores) { professor ->
                    CardProfessor(professor)
                }
            }
        }
    }
}

@Composable
fun CardProfessor(professor: Professor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = professor.imagemId),
                contentDescription = "Foto",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
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
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalhes",
                tint = Color.LightGray
            )
        }
    }
}