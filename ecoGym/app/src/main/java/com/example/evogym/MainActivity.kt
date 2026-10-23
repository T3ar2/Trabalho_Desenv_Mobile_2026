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
import com.example.evogym.ui.theme.EvoGymTheme
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.navigation.Menu

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            EvoGymTheme {


                val navController = rememberNavController()



                val backStackEntry by
                navController.currentBackStackEntryAsState()

                val rotaAtual = backStackEntry?.destination?.route

                val mostrarMenu = rotaAtual != null &&
                        !rotaAtual.contains("Login") &&
                        !rotaAtual.contains("Cadastro")

                Scaffold(

                        topBar = {
                            if (mostrarMenu){Menu(navController = navController) }
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