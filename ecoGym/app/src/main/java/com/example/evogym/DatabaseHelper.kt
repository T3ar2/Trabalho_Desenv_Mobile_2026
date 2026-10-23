package com.example.evogym.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class UsuarioData(
    val id: Long = 0,
    val nome: String,
    val email: String,
    val senha: String,
    val idade: Int = 25,
    val altura: String = "1.70 m",
    val peso: String = "70 kg",
    val plano: String = "Mensal",
    val professor: String = "Joan Mendes",
    val treinos: Int = 0,
    val seguidores: Int = 0,
    val diasSeguidos: Int = 0
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecogym.db"
        private const val DATABASE_VERSION = 1

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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
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
}
