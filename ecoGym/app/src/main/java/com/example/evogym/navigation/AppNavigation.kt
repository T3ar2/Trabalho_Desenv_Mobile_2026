package com.example.evogym.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.evogym.ui.screens.CadastroScreen
import com.example.evogym.ui.screens.HomeScreen
import com.example.evogym.ui.screens.ListaDeProfessores
import com.example.evogym.ui.screens.ListaExerciciosScreen
import com.example.evogym.ui.screens.LoginScreen
import com.example.evogym.ui.screens.PainelUsuarioScreen
import com.example.evogym.ui.screens.usuarioExemplo
import com.example.evogym.ui.viewmodel.ExerciciosViewModel

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
                onVoltarClick = { navController.popBackStack() },
                viewModel = viewModel<ExerciciosViewModel>()
            )
        }

        composable<PainelUsuario> {
            PainelUsuarioScreen(
                usuario = usuarioExemplo(),
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() },
                onDeslogarClick = {
                    navController.navigate(Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
