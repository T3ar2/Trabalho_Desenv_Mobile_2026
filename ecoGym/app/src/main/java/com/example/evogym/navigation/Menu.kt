package com.example.evogym.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    navController: NavHostController
){
    TopAppBar(
        title = {
            Text("ecoGym")
        },
        actions = {
            TextButton(onClick = {
                navController.navigate(HomeScreen)
            }) {
                Text("Home")
            }
            TextButton(onClick = {
                navController.navigate(ListaDeProfessores)
            }) {
                Text("Professores")
            }
            TextButton(onClick = {
                navController.navigate(ListaExercicios)
            }) {
                Text("Exercicios")
            }

            TextButton(onClick = {
                navController.navigate(PainelUsuario)
            }) {
                Text("Usuário")
            }
        }

    )
}
