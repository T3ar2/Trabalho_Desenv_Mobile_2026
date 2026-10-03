package com.example.evogym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.evogym.navigation.AppNavigation
import com.example.evogym.ui.theme.EvoGymTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            EvoGymTheme {

                // Controlador de navegação
                val navController = rememberNavController()

                // Observa qual tela está sendo exibida
                val backStackEntry by
                navController.currentBackStackEntryAsState()

                val rotaAtual = backStackEntry?.destination?.route

                // Se futuramente existirem Login e Cadastro,
                // podemos esconder elementos específicos nessas telas.
                val mostrarMenu = rotaAtual != null &&
                        !rotaAtual.contains("Login") &&
                        !rotaAtual.contains("Cadastro")

                Scaffold { innerPadding ->

                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}