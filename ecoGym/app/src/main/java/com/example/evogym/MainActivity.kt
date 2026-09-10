package com.example.evogym
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.evogym.ui.ListaExerciciosScreen
import com.example.evogym.ui.PainelUsuarioScreen
import com.example.evogym.ui.theme.EvoGymTheme
import com.example.evogym.ui.usuarioExemplo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvoGymTheme {
                var mostrarListaExercicios by rememberSaveable {
                    mutableStateOf(false)
                }
                var mostrarListaProfessores by rememberSaveable { mutableStateOf(false) }
                var mostrarPainelUsuario by rememberSaveable { mutableStateOf(false) }

                BackHandler(enabled = mostrarListaExercicios) {
                    mostrarListaExercicios = false
                }

                BackHandler(enabled = mostrarListaProfessores) {
                    mostrarListaProfessores = false
                }
                BackHandler(enabled = mostrarPainelUsuario) {
                    mostrarPainelUsuario = false
                }


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (mostrarListaExercicios) {
                        ListaExerciciosScreen(
                            modifier = Modifier.padding(innerPadding),
                            onVoltarClick = {
                                mostrarListaExercicios = false
                            }
                        )
                    }
                    else if (mostrarListaProfessores) {
                        ListaDeProfessores(
                            modifier = Modifier.padding(innerPadding),
                            onVoltarClick = {
                                mostrarListaProfessores = false
                            }
                        )
                    }  else if (mostrarPainelUsuario) {
                        PainelUsuarioScreen(
                            usuario = usuarioExemplo(),
                            modifier = Modifier.padding(innerPadding),
                            onVoltarClick = {
                                mostrarPainelUsuario = false
                            }
                        )
                    }

                    else {
                        HomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onExerciciosClick = {
                                mostrarListaExercicios = true
                            },
                            onProfessoresClick = {
                                mostrarListaProfessores = true
                            },
                            onPerfilClick = {
                                mostrarPainelUsuario = true
                            }

                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    EvoGymTheme {
        HomeScreen()
    }
}
