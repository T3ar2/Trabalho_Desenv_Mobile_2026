package com.example.evogym.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.evogym.data.model.Exercicio
import com.example.evogym.data.model.UsuarioData

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecogym.db"
        // A versão 3 força a criação da tabela em instalações que já tinham o banco na versão 2.
        private const val DATABASE_VERSION = 3

        const val TABLE_USUARIOS = "usuarios"
        const val COL_ID = "id"
        const val COL_NOME = "nome"
        const val COL_EMAIL = "email"
        const val COL_SENHA = "senha"
        const val COL_IDADE = "idade"
        const val COL_ALTURA = "altura"
        const val COL_PESO = "peso"
        const val COL_PLANO = "plano"
        const val COL_PROFESSOR = "professor"
        const val COL_TREINOS = "treinos"
        const val COL_SEGUIDORES = "seguidores"
        const val COL_DIAS_SEGUIDOS = "dias_seguidos"

        const val TABLE_EXERCICIOS = "exercicios"
        const val COL_EXERCICIO_ID = "id"
        const val COL_EXERCICIO_NOME = "nome"
        const val COL_EXERCICIO_CATEGORIA = "categoria"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOME TEXT NOT NULL,
                $COL_EMAIL TEXT UNIQUE NOT NULL,
                $COL_SENHA TEXT NOT NULL,
                $COL_IDADE INTEGER DEFAULT 25,
                $COL_ALTURA TEXT DEFAULT '1.70 m',
                $COL_PESO TEXT DEFAULT '70 kg',
                $COL_PLANO TEXT DEFAULT 'Mensal',
                $COL_PROFESSOR TEXT DEFAULT 'Joan Mendes',
                $COL_TREINOS INTEGER DEFAULT 0,
                $COL_SEGUIDORES INTEGER DEFAULT 0,
                $COL_DIAS_SEGUIDOS INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
        criarTabelaExercicios(db)
        inserirExerciciosIniciais(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Não apagamos os usuários durante uma atualização de banco.
        // IF NOT EXISTS permite executar a migração mesmo se a tabela já existir.
        if (oldVersion < 3) {
            criarTabelaExercicios(db)
            inserirExerciciosIniciais(db)
        }
    }

    fun inserirUsuario(nome: String, email: String, senha: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, nome)
            put(COL_EMAIL, email)
            put(COL_SENHA, senha)
            put(COL_IDADE, 25)
            put(COL_ALTURA, "1.70 m")
            put(COL_PESO, "70 kg")
            put(COL_PLANO, "Mensal")
            put(COL_PROFESSOR, "Joan Mendes")
            put(COL_TREINOS, 1)
            put(COL_SEGUIDORES, 0)
            put(COL_DIAS_SEGUIDOS, 1)
        }
        return db.insert(TABLE_USUARIOS, null, values)
    }

    fun autenticar(email: String, senha: String): UsuarioData? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "$COL_EMAIL = ? AND $COL_SENHA = ?",
            arrayOf(email, senha),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                extrairUsuario(it)
            } else {
                null
            }
        }
    }


    fun buscarPorEmail(email: String): UsuarioData? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "$COL_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                extrairUsuario(it)
            } else {
                null
            }
        }
    }


    fun atualizarUsuario(
        emailOriginal: String,
        novoNome: String,
        novaIdade: Int,
        novaAltura: String,
        novoPeso: String,
        novoPlano: String,
        novoProfessor: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, novoNome)
            put(COL_IDADE, novaIdade)
            put(COL_ALTURA, novaAltura)
            put(COL_PESO, novoPeso)
            put(COL_PLANO, novoPlano)
            put(COL_PROFESSOR, novoProfessor)
        }
        return db.update(TABLE_USUARIOS, values, "$COL_EMAIL = ?", arrayOf(emailOriginal))
    }

    fun excluirUsuario(email: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_USUARIOS, "$COL_EMAIL = ?", arrayOf(email))
    }

    fun inserirExercicio(nome: String, categoria: String): Long {
        val values = ContentValues().apply {
            put(COL_EXERCICIO_NOME, nome)
            put(COL_EXERCICIO_CATEGORIA, categoria)
        }
        return writableDatabase.insert(TABLE_EXERCICIOS, null, values)
    }

    fun listarExercicios(): List<Exercicio> {
        val exercicios = mutableListOf<Exercicio>()
        val cursor = readableDatabase.query(
            TABLE_EXERCICIOS,
            null,
            null,
            null,
            null,
            null,
            "$COL_EXERCICIO_NOME ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                exercicios.add(extrairExercicio(it))
            }
        }
        return exercicios
    }

    fun atualizarExercicio(exercicio: Exercicio): Int {
        val values = ContentValues().apply {
            put(COL_EXERCICIO_NOME, exercicio.nome)
            put(COL_EXERCICIO_CATEGORIA, exercicio.categoria)
        }
        return writableDatabase.update(
            TABLE_EXERCICIOS,
            values,
            "$COL_EXERCICIO_ID = ?",
            arrayOf(exercicio.id.toString())
        )
    }

    fun excluirExercicio(id: Long): Int = writableDatabase.delete(
        TABLE_EXERCICIOS,
        "$COL_EXERCICIO_ID = ?",
        arrayOf(id.toString())
    )

    private fun criarTabelaExercicios(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_EXERCICIOS (
                $COL_EXERCICIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EXERCICIO_NOME TEXT NOT NULL,
                $COL_EXERCICIO_CATEGORIA TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    private fun inserirExerciciosIniciais(db: SQLiteDatabase) {
        val quantidade = db.rawQuery("SELECT COUNT(*) FROM $TABLE_EXERCICIOS", null).use {
            it.moveToFirst()
            it.getInt(0)
        }
        if (quantidade > 0) return

        listOf(
            Triple("Treino para Iniciantes", "Prof. Joan Mendes", "Força"),
            Triple("Treino de Full Body", "Prof. Pedro Lima", "Força"),
            Triple("Alongamentos Dinâmicos", "Prof. Ana Costa", "Mobilidade"),
            Triple("Yoga de Flexibilidade", "Prof. Ana Costa", "Resistência")
        ).forEach { (nome, professor, categoria) ->
            val values = ContentValues().apply {
                put(COL_EXERCICIO_NOME, nome)
                put(COL_EXERCICIO_CATEGORIA, categoria)
            }
            db.insert(TABLE_EXERCICIOS, null, values)
        }
    }

    private fun extrairUsuario(cursor: android.database.Cursor): UsuarioData {
        return UsuarioData(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
            nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
            senha = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENHA)),
            idade = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IDADE)),
            altura = cursor.getString(cursor.getColumnIndexOrThrow(COL_ALTURA)),
            peso = cursor.getString(cursor.getColumnIndexOrThrow(COL_PESO)),
            plano = cursor.getString(cursor.getColumnIndexOrThrow(COL_PLANO)),
            professor = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFESSOR)),
            treinos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TREINOS)),
            seguidores = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEGUIDORES)),
            diasSeguidos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DIAS_SEGUIDOS))
        )
    }

    private fun extrairExercicio(cursor: android.database.Cursor): Exercicio = Exercicio(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXERCICIO_ID)),
        nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCICIO_NOME)),
        categoria = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCICIO_CATEGORIA))
    )
}
