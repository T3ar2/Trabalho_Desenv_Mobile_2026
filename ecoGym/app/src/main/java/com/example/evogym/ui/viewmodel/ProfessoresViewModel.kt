package com.example.evogym.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.data.dal.ProfessorDao
import com.example.evogym.data.model.Professor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfessoresViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ProfessorDao(application)

    var professores by mutableStateOf<List<Professor>>(emptyList())
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
                mensagem = "Não foi possível acessar os professores."
            } finally {
                carregando = false
            }
        }
    }

    fun listar() = executar {
        professores = withContext(Dispatchers.IO) { dao.listar() }
    }

    fun salvar(id: Long, nome: String, especialidade: String) = executar {
        require(nome.isNotBlank() && especialidade.isNotBlank()) {
            "Preencha todos os campos."
        }
        val resultado = withContext(Dispatchers.IO) {
            if (id == 0L) dao.cadastrar(nome.trim(), especialidade.trim())
            else dao.alterar(Professor(id, nome.trim(), especialidade.trim())).toLong()
        }
        require(resultado > 0) { "Não foi possível salvar o professor." }
        professores = withContext(Dispatchers.IO) { dao.listar() }
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val excluidos = withContext(Dispatchers.IO) { dao.excluir(id) }
        require(excluidos == 1) { "Professor não encontrado." }
        professores = professores.filterNot { it.id == id }
        mensagem = "Professor excluído."
    }

    fun consumirSalvo() {
        salvo = false
    }

    fun consumirMensagem() {
        mensagem = null
    }
}