package com.example.evogym.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.data.model.Endereco
import com.example.evogym.data.remote.ViaCepApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CepViewModel : ViewModel() {
    var cep by mutableStateOf("")
        private set
    var endereco by mutableStateOf<Endereco?>(null)
        private set
    var carregando by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    fun mudarCep(valor: String) {
        if (carregando || valor.length > 9) return
        if (!valor.all { it.isDigit() || it == '-' }) return
        cep = valor
        endereco = null
        mensagem = null
    }

    fun consultar() {
        if (carregando) return
        endereco = null
        mensagem = null

        if (!Regex("[0-9]{5}-?[0-9]{3}").matches(cep)) {
            mensagem = "Informe um CEP válido"
            return
        }
        val cepConsulta = cep.replace("-", "")
        carregando = true
        viewModelScope.launch {
            try {
                val resposta = ViaCepApi.service.buscar(cepConsulta)
                when {
                    resposta.erro == true -> mensagem = "CEP não encontrado"
                    resposta.cep.isNullOrBlank() -> mensagem = "Resposta inválida"
                    else -> endereco = resposta
                }
            } catch (erro: CancellationException) {
                throw erro
            } catch (_: Exception) {
                mensagem = "Não foi possível consultar o CEP"
            } finally {
                carregando = false
            }
        }
    }
}