package com.example.evogym.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evogym.R as R1
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun ListaExerciciosScreen(
    modifier: Modifier = Modifier
) {
    var textoBusca by remember { mutableStateOf("") }

    val listaDeTreinos = listOf(
        Treino(nome = "Treino para Iniciantes", professor = "Prof. Joan Mendes", imagem = R1.drawable.mulher_barra),
        Treino(nome = "Treino de Full Body", professor = "Prof. Pedro Lima", imagem = R1.drawable.homem_barra),
        Treino(nome = "Alongamentos Dinâmicos", professor = "Prof. Ana Costa", imagem = R1.drawable.alongamento),
        Treino(nome = "Yoga de Flexibilidade", professor = "Prof. Ana Costa", imagem = R1.drawable.yoga)
    )

    Column(modifier = modifier) {

        // Topbar roxa
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(46, 45, 77))
                .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Lista de Exercícios",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}, shape = RoundedCornerShape(16.dp)) {
                    Text("Força")
                }
                Button(onClick = {}, shape = RoundedCornerShape(16.dp)) {
                    Text("Mobilidade")
                }
                Button(onClick = {}, shape = RoundedCornerShape(16.dp)) {
                    Text("Resistência")
                }
            }
        }

        // Barra de busca
        TextField(
            value = textoBusca,
            onValueChange = { novoTexto -> textoBusca = novoTexto },
            placeholder = { Text("Buscar Exercício") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )

        // Lista de cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(listaDeTreinos) { treino ->
                CardTreino(treino = treino)
            }
        }
    }
}

@Composable
fun CardTreino(treino: Treino) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //trocar por Image depois
            Image(
                painter = painterResource(id = treino.imagem),
                contentDescription = "Foto do treino",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = treino.nome,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = treino.professor,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Text(
                text = ">",
                color = Color(0xFF2ECC71),
                fontSize = 20.sp,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Abrindo vídeo: ${treino.nome}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

data class Treino(
    val nome: String,
    val professor: String,
    val imagem: Int
)