package com.example.evogym.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.evogym.data.model.Professor

@Composable
internal fun SeletorProfessor(
    professores: List<Professor>,
    selecionadoId: Long?,
    selecionar: (Long?) -> Unit,
    habilitado: Boolean
) {
    var aberto by remember { mutableStateOf(false) }
    val nome = professores.firstOrNull { it.id == selecionadoId }?.nome

    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { aberto = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(nome ?: "Selecionar professor (opcional)")
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            DropdownMenuItem(
                text = { Text("Sem professor") },
                onClick = {
                    selecionar(null)
                    aberto = false
                }
            )
            professores.forEach { professor ->
                DropdownMenuItem(
                    text = { Text(professor.nome) },
                    onClick = {
                        selecionar(professor.id)
                        aberto = false
                    }
                )
            }
        }
    }
}
