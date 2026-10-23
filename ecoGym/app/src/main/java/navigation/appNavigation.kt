package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.evogym.CadastroScreen
import com.example.evogym.HomeScreen
import com.example.evogym.ListaDeProfessores
import com.example.evogym.ui.ListaExerciciosScreen
import com.example.evogym.ui.PainelUsuarioScreen
import com.example.evogym.ui.usuarioExemplo
import com.example.myapplication.ui.screens.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Login,
        modifier = modifier
    ) {

        composable<Login> {
            LoginScreen(
                loginSucesso = {
                    navController.navigate(HomeScreen) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                irCadastro = {
                    navController.navigate(Cadastro)
                }
            )
        }

        composable<Cadastro> {
            CadastroScreen(
                irLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<HomeScreen> {
            HomeScreen(
                onExerciciosClick = { navController.navigate(ListaExercicios) },
                onProfessoresClick = { navController.navigate(ListaDeProfessores) },
                onPerfilClick = { navController.navigate(PainelUsuario) }
            )
        }

        composable<ListaDeProfessores> {
            ListaDeProfessores(
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() }
            )
        }

        composable<ListaExercicios> {
            ListaExerciciosScreen(
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() }
            )
        }

        composable<PainelUsuario> {
            PainelUsuarioScreen(
                usuario = usuarioExemplo(),
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() }
            )
        }
    }
}