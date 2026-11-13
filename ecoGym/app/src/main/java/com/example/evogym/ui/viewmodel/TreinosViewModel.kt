package com.example.evogym.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evogym.data.dal.ExercicioDao
import com.example.evogym.data.dal.ProfessorDao
import com.example.evogym.data.dal.TreinoDao
import com.example.evogym.data.dal.UsuarioDao
import com.example.evogym.data.model.Exercicio
import com.example.evogym.data.model.Professor
import com.example.evogym.data.model.Treino
import com.example.evogym.data.model.TreinoDetalhado
import com.example.evogym.data.model.UsuarioData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreinosViewModel(application: Application) : AndroidViewModel(application) {

    private val treinoDao = TreinoDao(application)
    private val exercicioDao = ExercicioDao(application)
    private val professorDao = ProfessorDao(application)
    private val usuarioDao = UsuarioDao(application)

    var treinos by mutableStateOf<List<TreinoDetalhado>>(emptyList())
        private set
    var exercicios by mutableStateOf<List<Exercicio>>(emptyList())
        private set
    var professores by mutableStateOf<List<Professor>>(emptyList())
        private set
    var alunos by mutableStateOf<List<UsuarioData>>(emptyList())
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
                mensagem = "Não foi possivel carregar ou salvar os treinos."
            } finally {
                carregando = false
            }
        }
    }

    private fun mapearParaDetalhados(
        listaTreinos: List<Treino>,
        listaExercicios: List<Exercicio>,
        listaProfessores: List<Professor>,
        listaAlunos: List<UsuarioData>
    ): List<TreinoDetalhado> {
        val exerciciosMap = listaExercicios.associateBy { it.id }
        val professoresMap = listaProfessores.associateBy { it.id }
        val alunosMap = listaAlunos.associateBy { it.id }
        return listaTreinos.map { treino ->
            TreinoDetalhado(
                id = treino.id,
                exercicioId = treino.exercicioId,
                professorId = treino.professorId,
                usuarioId = treino.usuarioId,
                exercicioNome = exerciciosMap[treino.exercicioId]?.nome ?: "",
                professorNome = professoresMap[treino.professorId]?.nome ?: "",
                usuarioNome = treino.usuarioId?.let { alunosMap[it]?.nome },
                series = treino.series,
                repeticoes = treino.repeticoes
            )
        }
    }

    fun carregarDados() = executar {
        withContext(Dispatchers.IO) {
            val listaTreinos = treinoDao.listar()
            val listaExercicios = exercicioDao.listar()
            val listaProfessores = professorDao.listar()
            val listaAlunos = usuarioDao.listar()
            val detalhados = mapearParaDetalhados(listaTreinos, listaExercicios, listaProfessores, listaAlunos)

            withContext(Dispatchers.Main) {
                treinos = detalhados
                exercicios = listaExercicios
                professores = listaProfessores
                alunos = listaAlunos
            }
        }
    }

    fun salvar(
        id: Long,
        exercicioId: Long?,
        professorId: Long?,
        usuarioId: Long?,
        seriesTexto: String,
        repeticoesTexto: String
    ) = executar {
        require(exercicioId != null && exercicioId > 0) {
            "Selecione um exercício."
        }
        require(professorId != null && professorId > 0) {
            "Selecione um professor."
        }
        val series = seriesTexto.trim().toIntOrNull()
        require(series != null && series > 0) {
            "Informe uma quantidade válida de séries."
        }
        val repeticoes = repeticoesTexto.trim().toIntOrNull()
        require(repeticoes != null && repeticoes > 0) {
            "Informe uma quantidade válida de repetições."
        }

        val treino = Treino(
            id = id,
            exercicioId = exercicioId,
            professorId = professorId,
            usuarioId = usuarioId,
            series = series,
            repeticoes = repeticoes
        )

        val resultado = withContext(Dispatchers.IO) {
            if (id == 0L) {
                treinoDao.cadastrar(treino)
            } else {
                treinoDao.alterar(treino).toLong()
            }
        }

        require(resultado > 0) { "Não foi possível salvar o treino." }

        val treinosAtualizados = withContext(Dispatchers.IO) {
            val listaTreinos = treinoDao.listar()
            mapearParaDetalhados(listaTreinos, exercicios, professores, alunos)
        }
        treinos = treinosAtualizados
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val excluidos = withContext(Dispatchers.IO) { treinoDao.excluir(id) }
        require(excluidos == 1) { "Treino não encontrado." }
        treinos = treinos.filterNot { it.id == id }
        mensagem = "Treino excluido."
    }

    fun consumirSalvo() {
        salvo = false
    }

    fun consumirMensagem() {
        mensagem = null
    }
}
