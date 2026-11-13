package com.example.evogym.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable


@Serializable
data object HomeScreen

@Serializable
data object ListaDeProfessores

@Serializable
data object ListaExercicios
@Serializable
data object ListaEquipamentos
@Serializable
data object ListaTreinos
@Serializable
data object PainelUsuario

@Serializable
object Login

@Serializable
object Cadastro

internal fun NavDestination.ehTelaDeAutenticacao(): Boolean =
    hasRoute<Login>() || hasRoute<Cadastro>()

internal fun NavDestination.deveExibirMenu(): Boolean =
    !ehTelaDeAutenticacao() && !hasRoute<HomeScreen>()
