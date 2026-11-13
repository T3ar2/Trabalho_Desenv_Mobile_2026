package com.example.evogym.navigation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(navController: NavHostController) {
    TopAppBar(
        title = {},
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ItemMenu("Home") { navController.navigate(HomeScreen) }
                ItemMenu("Professores") { navController.navigate(ListaDeProfessores) }
                ItemMenu("Exercícios") { navController.navigate(ListaExercicios) }
                ItemMenu("Treinos") { navController.navigate(ListaTreinos) }
                ItemMenu("Usuário") { navController.navigate(PainelUsuario) }
            }
        }
    )
}

@Composable
private fun ItemMenu(rotulo: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp)) {
        Text(rotulo, maxLines = 1)
    }
}
