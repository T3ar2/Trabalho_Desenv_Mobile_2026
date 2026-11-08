package com.example.evogym.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.data.dal.EquipamentoDao
import com.example.evogym.data.model.Equipamento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EquipamentosViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = EquipamentoDao(application)

    var equipamentos by mutableStateOf<List<Equipamento>>(emptyList())
        private set

    var carregando by mutableStateOf(false)
        private set

    var salvo by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    private fun executar (operacao : suspend () -> Unit){
        if (carregando) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                operacao()
            } catch ( erro : IllegalArgumentException){
                mensagem = erro.message
            }catch (erro : Exception){
                mensagem = "Não foi possível acessar os equipamentos."
            } finally {
                carregando = false
            }

        }
    }

    fun listar() = executar {
        equipamentos = withContext(Dispatchers.IO) {
         dao.listar()
        }
    }

    fun salvar (id : Long, nome : String, categoria : String, quantidadeTexto :  String, status:  String) = executar{
        require(nome.isNotBlank() && categoria.isNotBlank() && quantidadeTexto.isNotBlank()){
            "Preencha todos os campos"
        }

        val quantidade = quantidadeTexto.trim().toIntOrNull()
        require(quantidade != null && quantidade >= 0){
            "Quantidade inválida"
        }

        val resultado = withContext(Dispatchers.IO){
          if (id == 0L)  dao.cadastrar(nome.trim(), categoria.trim(), quantidade, status)
            else dao.alterar(Equipamento(id, nome.trim(), categoria.trim(), quantidade,status)).toLong()
        }
        require(resultado > 0){
            "Não foi possível salvar o equipamento."
        }

        equipamentos = withContext(Dispatchers.IO){dao.listar()}
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val excluidos = withContext(Dispatchers.IO){ dao.excluir(id)}

        require(excluidos == 1) {
            "Equipamento não encontrado."
        }

        equipamentos = equipamentos.filterNot { it.id == id }

        mensagem = "Equipamento excluído."
    }

    fun consumirSalvo(){
        salvo = false
    }
}