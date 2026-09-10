package com.example.evogym.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.Alignment
import com.example.evogym.ui.theme.AzulEscuro
import com.example.evogym.ui.theme.BrancoBackground
import com.example.evogym.ui.theme.AzulTiffany


@Composable
fun ListaExerciciosScreen(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {}

) {
    var textoBusca by remember { mutableStateOf("") }
    val context = LocalContext.current
    var filtroSelecionado by remember { mutableStateOf<String?>(null) }  //

    val listaDeTreinos = listOf(
        Treino(nome = "Treino para Iniciantes", professor = "Prof. Joan Mendes", imagem = R1.drawable.mulher_barra, categoria = "Força"),
        Treino(nome = "Treino de Full Body", professor = "Prof. Pedro Lima", imagem = R1.drawable.homem_barra, categoria = "Força"),
        Treino(nome = "Alongamentos Dinâmicos", professor = "Prof. Ana Costa", imagem = R1.drawable.alongamento, categoria = "Mobilidade"),
        Treino(nome = "Yoga de Flexibilidade", professor = "Prof. Ana Costa", imagem = R1.drawable.yoga, categoria = "Resistência")
    )

    val listaFiltrada = listaDeTreinos.filter { treino ->
        val passaNoTexto = treino.nome.contains(textoBusca, ignoreCase = true)
        val passaNaCategoria = filtroSelecionado == null || treino.categoria == filtroSelecionado
        passaNoTexto && passaNaCategoria
    }

    Column(modifier = modifier
        .fillMaxWidth()
        .background(BrancoBackground)) {

        // Topbar roxa
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(46, 45, 77))
                .padding(start = 16.dp, end = 0.dp, top = 0.dp, bottom = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "<",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                        .padding(end = 20.dp)
                        .clickable {
                        onVoltarClick()
                    }

                )

                //Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lista Exercícios",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                )

            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { filtroSelecionado = if (filtroSelecionado == "Força") null else "Força" },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroSelecionado == "Força") AzulTiffany else Color.White.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (filtroSelecionado == "Força") Color.Transparent else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Text("Força")
                }

                Button(
                    onClick = { filtroSelecionado = if (filtroSelecionado == "Mobilidade") null else "Mobilidade" },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroSelecionado == "Mobilidade") AzulTiffany else Color.White.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (filtroSelecionado == "Mobilidade") Color.Transparent else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Text("Mobilidade")
                }

                Button(
                    onClick = { filtroSelecionado = if (filtroSelecionado == "Resistência") null else "Resistência" },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filtroSelecionado == "Resistência") AzulTiffany else Color.White.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (filtroSelecionado == "Resistência") Color.Transparent else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Text("Resistência")
                }
            }
        }

        // Barra de busca
        OutlinedTextField(
            value = textoBusca,
            onValueChange = { novoTexto -> textoBusca = novoTexto },
            placeholder = { Text("Buscar exercício...", color = Color.Gray) },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )

        // Lista de cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(listaFiltrada) { treino ->
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
            modifier = Modifier
                .background(color = Color.White) //background card
                .padding(12.dp),

            verticalAlignment = Alignment.CenterVertically,

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
                text = ">>",
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
    val imagem: Int,
    val categoria: String
)