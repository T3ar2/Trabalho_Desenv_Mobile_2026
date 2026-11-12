package com.example.evogym.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.auth.SessaoUsuario
import com.example.evogym.data.dal.UsuarioDao
import com.example.evogym.data.local.FotoPerfilStorage
import com.example.evogym.data.model.UsuarioData
import com.example.evogym.validation.CadastroValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val dao = UsuarioDao(context)
    val usuario: UsuarioData? get() = SessaoUsuario.usuario

    var carregando by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set
    var perfilSalvo by mutableStateOf(false)
        private set

    fun salvarPerfil(
        nome: String, idade: String, altura: String, peso: String, plano: String, professorId: Long?
    ) = executar("Erro ao atualizar perfil.") { atual ->
        val nomeLimpo = nome.trim()
        CadastroValidator.validarNome(nomeLimpo)
        val editado = atual.copy(
            nome = nomeLimpo,
            idade = CadastroValidator.validarIdade(idade),
            altura = CadastroValidator.validarAltura(altura),
            peso = CadastroValidator.validarPeso(peso),
            plano = CadastroValidator.validarPlano(plano),
            professorId = professorId
        )
        val linhas = withContext(Dispatchers.IO) { dao.alterar(editado) }
        require(linhas == 1) { "Erro ao atualizar perfil." }
        SessaoUsuario.atualizar(editado)
        perfilSalvo = true
        mensagem = "Perfil atualizado."
    }

    fun atualizarFoto(uri: Uri?) = executar("Não foi possível atualizar a foto.") { atual ->
        val caminho = withContext(Dispatchers.IO) {
            val arquivo = uri?.let {
                requireNotNull(FotoPerfilStorage.salvar(context, atual.id, it)) {
                    "Não foi possível salvar a foto."
                }
            }
            try {
                require(dao.alterarFoto(atual.id, arquivo?.path) == 1) {
                    "Não foi possível atualizar a foto."
                }
            } catch (erro: Exception) {
                arquivo?.delete()
                throw erro
            }
            FotoPerfilStorage.remover(context, atual.id, manter = arquivo)
            arquivo?.path
        }
        SessaoUsuario.atualizar(atual.copy(foto = caminho))
        mensagem = if (caminho == null) "Foto removida." else "Foto atualizada."
    }

    fun excluirConta() = executar("Erro ao excluir conta.") { atual ->
        withContext(Dispatchers.IO) {
            require(dao.excluir(atual.id) == 1) { "Erro ao excluir conta." }
            FotoPerfilStorage.remover(context, atual.id)
        }
        mensagem = "Conta excluída."
        SessaoUsuario.encerrar()
    }

    fun sair() {
        if (carregando) return
        consumirMensagem()
        consumirSalvamento()
        mensagem = "Sessão encerrada."
        SessaoUsuario.encerrar()
    }

    fun consumirSalvamento() {
        perfilSalvo = false
    }

    fun consumirMensagem() {
        mensagem = null
    }

    private fun executar(erroOperacao: String, operacao: suspend (UsuarioData) -> Unit) {
        val atual = usuario ?: return
        if (carregando) return
        consumirMensagem()
        consumirSalvamento()
        carregando = true
        viewModelScope.launch {
            try {
                operacao(atual)
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: SQLiteConstraintException) {
                mensagem = if (erro.message?.contains("FOREIGN KEY") == true) {
                    "O professor selecionado não está mais disponível."
                } else {
                    erroOperacao
                }
            } catch (erro: IllegalArgumentException) {
                mensagem = erro.message
            } catch (_: Exception) {
                mensagem = erroOperacao
            } finally {
                carregando = false
            }
        }
    }
}
