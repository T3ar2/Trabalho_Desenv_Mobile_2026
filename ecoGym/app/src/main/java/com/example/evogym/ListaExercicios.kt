package com.example.evogym.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListaExerciciosScreen(
    modifier: Modifier = Modifier
) {
    //barra de pesquisa
    var textoBusca by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(0.dp, 30.dp)
    )
    {
        Text(text = "Lista de Exercicios")

        //adicionar arrowback
        Row (horizontalArrangement = Arrangement.spacedBy(16.dp))
        {
            Button(onClick = {}, modifier = modifier.width(100.dp)) {
                Text("Força")
            }
            Button(onClick = {}, modifier = modifier.width(100.dp)) {
                Text("Mobilidade")
            }
            Button(onClick = {}, modifier = modifier.width(100.dp)) {
                Text("Resistência")
            }

        }
        TextField(
            value = textoBusca,
            onValueChange = {novoTexto -> textoBusca = novoTexto},
            placeholder = {Text("Buscar Exercício")},
            modifier = Modifier.fillMaxWidth()
        )

    }


}