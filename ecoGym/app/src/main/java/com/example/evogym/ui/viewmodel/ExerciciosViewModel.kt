package com.example.evogym.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.data.dal.ExercicioDao
import com.example.evogym.data.model.Exercicio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciciosViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ExercicioDao(application)

    var exercicios by mutableStateOf<List<Exercicio>>(emptyList())
        private set
    var carregando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    private fun executar(operacao: suspend () -> Unit) {
        if (carregando) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                operacao()
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: IllegalArgumentException) {
                mensagem = erro.message
            } catch (erro: Exception) {
                mensagem = "Não foi possível acessar os exercícios."
            } finally {
                carregando = false
            }
        }
    }

    fun listar() = executar {
        exercicios = withContext(Dispatchers.IO) { dao.listar() }
    }

    fun salvar(id: Long, nome: String, categoria: String) = executar {
        require(nome.isNotBlank() && categoria.isNotBlank()) {
            "Preencha todos os campos."
        }
        val resultado = withContext(Dispatchers.IO) {
            if (id == 0L) dao.cadastrar(nome.trim(),  categoria.trim())
            else dao.alterar(Exercicio(id, nome.trim(), categoria.trim())).toLong()
        }
        require(resultado > 0) { "Não foi possível salvar o exercício." }
        exercicios = withContext(Dispatchers.IO) { dao.listar() }
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val excluidos = withContext(Dispatchers.IO) { dao.excluir(id) }
        require(excluidos == 1) { "Exercício não encontrado." }
        exercicios = exercicios.filterNot { it.id == id }
        mensagem = "Exercício excluído."
    }

    fun consumirSalvo() {
        salvo = false
    }
}
