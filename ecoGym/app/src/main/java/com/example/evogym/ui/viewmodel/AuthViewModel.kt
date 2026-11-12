package com.example.evogym.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.auth.AuthManager
import com.example.evogym.auth.SessaoUsuario
import com.example.evogym.data.dal.UsuarioDao
import com.example.evogym.data.model.UsuarioData
import com.example.evogym.validation.CadastroValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = UsuarioDao(application)
    var carregando by mutableStateOf(false)
        private set
    var cadastroConcluido by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        idade: String,
        altura: String,
        peso: String,
        plano: String,
        professorId: Long?,
        cep: String,
        endereco: String
    ) = executar {
        val nomeLimpo = nome.trim()
        val emailLimpo = email.trim()
        CadastroValidator.validar(nomeLimpo, emailLimpo, senha)

        require(cep.isBlank() || CadastroValidator.cepValido(cep)) { "Informe um CEP válido." }

        val usuario = UsuarioData(
            nome = nomeLimpo,
            email = emailLimpo,
            idade = CadastroValidator.validarIdade(idade),
            altura = CadastroValidator.validarAltura(altura),
            peso = CadastroValidator.validarPeso(peso),
            plano = CadastroValidator.validarPlano(plano),
            professorId = professorId,
            cep = cep,
            endereco = endereco
        )

        withContext(Dispatchers.IO) {
            val salt = AuthManager.criarSalt()
            val hash = AuthManager.gerarHash(senha, salt)
            try {
                dao.cadastrar(usuario, hash, salt)
            } finally {
                hash.fill(0)
            }
        }
        cadastroConcluido = true
    }

    fun login(email: String, senha: String) = executar {
        require(email.isNotBlank() && senha.isNotBlank()) { "Preencha todos os campos." }

        val usuario = withContext(Dispatchers.IO) {
            val credenciais = dao.buscarCredenciais(email.trim())
            val hash = credenciais?.hash ?: ByteArray(32)
            val salt = credenciais?.salt ?: ByteArray(16)
            try {
                val senhaValida = AuthManager.validarSenha(senha, salt, hash)
                if (senhaValida && credenciais != null) dao.buscarPorId(credenciais.id) else null
            } finally {
                hash.fill(0)
            }
        }
        if (usuario == null) {
            mensagem = "E-mail ou senha inválidos."
        } else {
            SessaoUsuario.iniciar(usuario)
        }
    }

    private fun executar(operacao: suspend () -> Unit) {
        if (carregando || cadastroConcluido) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                operacao()
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: SQLiteConstraintException) {
                mensagem = if (erro.message?.contains("FOREIGN KEY") == true) {
                    "O professor selecionado não está mais disponível."
                } else {
                    "E-mail já cadastrado."
                }
            } catch (erro: IllegalArgumentException) {
                mensagem = erro.message
            } catch (_: Exception) {
                mensagem = "Não foi possível acessar a conta."
            } finally {
                carregando = false
            }
        }
    }
}
