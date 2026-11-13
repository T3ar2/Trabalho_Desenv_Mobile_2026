package com.example.evogym.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.evogym.auth.SessaoUsuario
import com.example.evogym.data.model.UsuarioData
import com.example.evogym.ui.screens.CadastroScreen
import com.example.evogym.ui.screens.HomeScreen
import com.example.evogym.ui.screens.ListaDeProfessores
import com.example.evogym.ui.screens.ListaEquipamentosScreen
import com.example.evogym.ui.screens.ListaExerciciosScreen
import com.example.evogym.ui.screens.ListaTreinosScreen
import com.example.evogym.ui.screens.LoginScreen
import com.example.evogym.ui.screens.PainelUsuarioScreen
import com.example.evogym.ui.viewmodel.EquipamentosViewModel
import com.example.evogym.ui.viewmodel.ExerciciosViewModel
import com.example.evogym.ui.viewmodel.ProfessoresViewModel
import com.example.evogym.ui.viewmodel.TreinosViewModel
import com.example.evogym.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destino = entradaAtual?.destination
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val autenticado = usuarioViewModel.usuario != null
    val contexto = LocalContext.current
    val mensagem = usuarioViewModel.mensagem

    LaunchedEffect(destino, autenticado) {
        if (destino == null) return@LaunchedEffect
        val telaDeAutenticacao = destino.ehTelaDeAutenticacao()
        if (!autenticado && !telaDeAutenticacao) {
            navController.navigate(Login) {
                popUpTo(navController.graph.id)
                launchSingleTop = true
            }
        } else if (autenticado && telaDeAutenticacao) {
            navController.navigate(HomeScreen) {
                popUpTo(navController.graph.id)
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            Toast.makeText(contexto, mensagem, Toast.LENGTH_SHORT).show()
            usuarioViewModel.consumirMensagem()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Login,
        modifier = modifier
    ) {

        composable<Login> {
            LoginScreen(
                irCadastro = { navController.navigate(Cadastro) },
                viewModel = viewModel()
            )
        }

        composable<Cadastro> {
            CadastroScreen(
                irLogin = { navController.popBackStack() },
                authViewModel = viewModel()
            )
        }

        composableAutenticado<HomeScreen> { usuario ->
            HomeScreen(
                usuario = usuario,
                onExerciciosClick = { navController.navigate(ListaExercicios) },
                onProfessoresClick = { navController.navigate(ListaDeProfessores) },
                onPerfilClick = { navController.navigate(PainelUsuario) },
                onEquipamentoClick = { navController.navigate(ListaEquipamentos) },
                onTreinosClick = { navController.navigate(ListaTreinos) }
            )
        }

        composableAutenticado<ListaDeProfessores> {
            ListaDeProfessores(
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() },
                viewModel = viewModel<ProfessoresViewModel>()
            )
        }

        composableAutenticado<ListaExercicios> {
            ListaExerciciosScreen(
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() },
                viewModel = viewModel<ExerciciosViewModel>()
            )
        }

        composableAutenticado<ListaEquipamentos> {
            ListaEquipamentosScreen(
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() },
                viewModel = viewModel<EquipamentosViewModel>()
            )
        }

        composableAutenticado<ListaTreinos> {
            ListaTreinosScreen(
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() },
                viewModel = viewModel<TreinosViewModel>()
            )
        }

        composableAutenticado<PainelUsuario> {
            PainelUsuarioScreen(
                viewModel = usuarioViewModel,
                modifier = Modifier,
                onVoltarClick = { navController.popBackStack() }
            )
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.composableAutenticado(
    crossinline conteudo: @Composable (UsuarioData) -> Unit
) {
    composable<T> {
        val usuario = SessaoUsuario.usuario
        if (usuario != null) conteudo(usuario)
    }
}
