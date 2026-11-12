package com.example.evogym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.evogym.auth.SessaoUsuario
import com.example.evogym.navigation.AppNavigation
import com.example.evogym.navigation.Menu
import com.example.evogym.navigation.ehTelaDeAutenticacao
import com.example.evogym.ui.theme.EvoGymTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            EvoGymTheme {
                val navController = rememberNavController()
                val entradaAtual by navController.currentBackStackEntryAsState()
                val destino = entradaAtual?.destination
                val mostrarMenu = SessaoUsuario.usuario != null &&
                        destino?.ehTelaDeAutenticacao() == false

                Scaffold(
                    topBar = {
                        if (mostrarMenu) Menu(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
