package com.example.evogym.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.evogym.auth.AuthManager
import com.example.evogym.data.dal.CredenciaisUsuario
import com.example.evogym.data.model.Equipamento
import com.example.evogym.data.model.Exercicio
import com.example.evogym.data.model.Professor
import com.example.evogym.data.model.UsuarioData

internal class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecogym.db"
        private const val DATABASE_VERSION = 8

        const val TABLE_USUARIOS = "usuarios"
        const val COL_ID = "id"
        const val COL_NOME = "nome"
        const val COL_EMAIL = "email"
        const val COL_SENHA = "senha"
        const val COL_SALT = "salt"
        const val COL_IDADE = "idade"
        const val COL_ALTURA = "altura"
        const val COL_PESO = "peso"
        const val COL_PLANO = "plano"
        const val COL_PROFESSOR_ID = "professor_id"
        const val COL_TREINOS = "treinos"
        const val COL_SEGUIDORES = "seguidores"
        const val COL_DIAS_SEGUIDOS = "dias_seguidos"
        const val COL_CEP = "cep"
        const val COL_ENDERECO = "endereco"
        const val COL_FOTO = "foto"

        const val TABLE_EXERCICIOS = "exercicios"
        const val COL_EXERCICIO_ID = "id"
        const val COL_EXERCICIO_NOME = "nome"
        const val COL_EXERCICIO_CATEGORIA = "categoria"

        const val TABLE_EQUIPAMENTOS = "equipamentos"
        const val COL_EQUIP_ID = "id"
        const val COL_EQUIP_NOME = "nome"
        const val COL_EQUIP_CATEGORIA = "categoria"
        const val COL_EQUIP_QUANTIDADE = "quantidade"
        const val COL_EQUIP_STATUS = "status"

        const val TABLE_PROFESSORES = "professores"
        const val COL_PROF_ID = "id"
        const val COL_PROF_NOME = "nome"
        const val COL_PROF_ESPECIALIDADE = "especialidade"
        const val COL_PROF_FOTO = "foto"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        criarTabelaProfessores(db)
        val createTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOME TEXT NOT NULL,
                $COL_EMAIL TEXT UNIQUE NOT NULL,
                $COL_SENHA BLOB NOT NULL,
                $COL_SALT BLOB NOT NULL,
                $COL_IDADE INTEGER DEFAULT 0,
                $COL_ALTURA TEXT DEFAULT '1.70 m', 
                $COL_PESO TEXT DEFAULT '70 kg',
                $COL_PLANO TEXT DEFAULT 'Mensal',
                $COL_PROFESSOR_ID INTEGER REFERENCES $TABLE_PROFESSORES($COL_PROF_ID) ON DELETE SET NULL,
                $COL_TREINOS INTEGER DEFAULT 0,
                $COL_SEGUIDORES INTEGER DEFAULT 0,
                $COL_DIAS_SEGUIDOS INTEGER DEFAULT 0,
                $COL_CEP TEXT DEFAULT '',
                $COL_ENDERECO TEXT DEFAULT '',
                $COL_FOTO TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
        criarTabelaExercicios(db)
        inserirExerciciosIniciais(db)
        criarTabelaEquipamentos(db)
        inserirEquipamentosIniciais(db)
        inserirProfessoresIniciais(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            criarTabelaExercicios(db)
            inserirExerciciosIniciais(db)
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_CEP TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_ENDERECO TEXT DEFAULT ''")
            criarTabelaEquipamentos(db)
            inserirEquipamentosIniciais(db)
        }
        if (oldVersion < 7) {
            val colunasUsuario = db.rawQuery("SELECT * FROM $TABLE_USUARIOS LIMIT 0", null)
                .use { it.columnNames }
            if (COL_SALT !in colunasUsuario) {
                migrarSenhas(db)
            }
            if (COL_FOTO !in colunasUsuario) {
                db.execSQL("ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_FOTO TEXT")
            }
            val temTabelaProfessores = db.rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?",
                arrayOf(TABLE_PROFESSORES)
            ).use { it.moveToFirst() }
            if (!temTabelaProfessores) {
                criarTabelaProfessores(db)
                inserirProfessoresIniciais(db)
            }
        }
        if (oldVersion < 8) {
            db.execSQL(
                "ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_PROFESSOR_ID INTEGER " +
                    "REFERENCES $TABLE_PROFESSORES($COL_PROF_ID) ON DELETE SET NULL"
            )
            db.execSQL("""
                UPDATE $TABLE_USUARIOS
                SET $COL_PROFESSOR_ID = (
                    SELECT CASE WHEN COUNT(*) = 1 THEN MIN(p.$COL_PROF_ID) END
                    FROM $TABLE_PROFESSORES p
                    WHERE TRIM(p.$COL_PROF_NOME) = TRIM($TABLE_USUARIOS.professor) COLLATE NOCASE
                )
            """.trimIndent())
        }
    }

    private fun migrarSenhas(db: SQLiteDatabase) {
        db.execSQL("ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_SALT BLOB")
        var ultimoId = 0L
        while (true) {
            val conta = db.query(
                TABLE_USUARIOS, arrayOf(COL_ID, COL_SENHA), "$COL_ID > ?",
                arrayOf(ultimoId.toString()), null, null, "$COL_ID ASC", "1"
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) to cursor.getString(1) else null
            } ?: break

            val (id, senha) = conta
            val salt = AuthManager.criarSalt()
            val hash = AuthManager.gerarHash(senha, salt)
            try {
                val valores = ContentValues().apply {
                    put(COL_SENHA, hash)
                    put(COL_SALT, salt)
                }
                db.update(TABLE_USUARIOS, valores, "$COL_ID = ?", arrayOf(id.toString()))
            } finally {
                hash.fill(0)
            }
            ultimoId = id
        }
    }

    fun inserirUsuario(usuario: UsuarioData, hash: ByteArray, salt: ByteArray): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, usuario.nome)
            put(COL_EMAIL, usuario.email)
            put(COL_SENHA, hash)
            put(COL_SALT, salt)
            put(COL_IDADE, usuario.idade)
            put(COL_ALTURA, usuario.altura)
            put(COL_PESO, usuario.peso)
            put(COL_PLANO, usuario.plano)
            put(COL_PROFESSOR_ID, usuario.professorId)
            put(COL_TREINOS, usuario.treinos)
            put(COL_SEGUIDORES, usuario.seguidores)
            put(COL_DIAS_SEGUIDOS, usuario.diasSeguidos)
            put(COL_CEP, usuario.cep)
            put(COL_ENDERECO, usuario.endereco)
        }
        return db.insertOrThrow(TABLE_USUARIOS, null, values)
    }

    fun buscarCredenciais(email: String): CredenciaisUsuario? =
        readableDatabase.query(
            TABLE_USUARIOS,
            arrayOf(COL_ID, COL_SENHA, COL_SALT),
            "$COL_EMAIL = ?",
            arrayOf(email),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                CredenciaisUsuario(cursor.getLong(0), cursor.getBlob(1), cursor.getBlob(2))
            } else null
        }

    fun buscarUsuarioPorId(id: Long): UsuarioData? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
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


    fun atualizarUsuario(usuario: UsuarioData): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, usuario.nome)
            put(COL_IDADE, usuario.idade)
            put(COL_ALTURA, usuario.altura)
            put(COL_PESO, usuario.peso)
            put(COL_PLANO, usuario.plano)
            put(COL_PROFESSOR_ID, usuario.professorId)
        }
        return db.update(TABLE_USUARIOS, values, "$COL_ID = ?", arrayOf(usuario.id.toString()))
    }

    fun atualizarFoto(id: Long, caminho: String?): Int {
        val values = ContentValues().apply { put(COL_FOTO, caminho) }
        return writableDatabase.update(TABLE_USUARIOS, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun excluirUsuario(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_USUARIOS, "$COL_ID = ?", arrayOf(id.toString()))
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


    fun inserirEquipamento(nome: String, categoria: String, quantidade: Int, status: String): Long {
        val values = ContentValues().apply {
            put(COL_EQUIP_NOME, nome)
            put(COL_EQUIP_CATEGORIA, categoria)
            put(COL_EQUIP_QUANTIDADE, quantidade)
            put(COL_EQUIP_STATUS, status)
        }
        return writableDatabase.insert(TABLE_EQUIPAMENTOS, null, values)
    }

    fun listarEquipamentos(): List<Equipamento> {
        val lista = mutableListOf<Equipamento>()
        val cursor = readableDatabase.query(
            TABLE_EQUIPAMENTOS, null, null, null, null, null, "$COL_EQUIP_NOME ASC"
        )
        cursor.use { while (it.moveToNext()) lista.add(extrairEquipamento(it)) }
        return lista
    }

    fun atualizarEquipamento(equipamento: Equipamento): Int {
        val values = ContentValues().apply {
            put(COL_EQUIP_NOME, equipamento.nome)
            put(COL_EQUIP_CATEGORIA, equipamento.categoria)
            put(COL_EQUIP_QUANTIDADE, equipamento.quantidade)
            put(COL_EQUIP_STATUS, equipamento.status)
        }
        return writableDatabase.update(
            TABLE_EQUIPAMENTOS, values, "$COL_EQUIP_ID = ?", arrayOf(equipamento.id.toString())
        )
    }

    fun excluirEquipamento(id: Long): Int =
        writableDatabase.delete(TABLE_EQUIPAMENTOS, "$COL_EQUIP_ID = ?", arrayOf(id.toString()))

    fun inserirProfessor(nome: String, especialidade: String, foto: String = ""): Long {
        val values = ContentValues().apply {
            put(COL_PROF_NOME, nome)
            put(COL_PROF_ESPECIALIDADE, especialidade)
            put(COL_PROF_FOTO, foto)
        }
        return writableDatabase.insert(TABLE_PROFESSORES, null, values)
    }

    fun listarProfessores(): List<Professor> {
        val professores = mutableListOf<Professor>()
        val cursor = readableDatabase.query(
            TABLE_PROFESSORES,
            null,
            null,
            null,
            null,
            null,
            "$COL_PROF_NOME ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                professores.add(extrairProfessor(it))
            }
        }
        return professores
    }

    fun atualizarProfessor(professor: Professor): Int {
        val values = ContentValues().apply {
            put(COL_PROF_NOME, professor.nome)
            put(COL_PROF_ESPECIALIDADE, professor.especialidade)
            put(COL_PROF_FOTO, professor.foto)
        }
        return writableDatabase.update(
            TABLE_PROFESSORES,
            values,
            "$COL_PROF_ID = ?",
            arrayOf(professor.id.toString())
        )
    }

    fun excluirProfessor(id: Long): Int = writableDatabase.delete(
        TABLE_PROFESSORES,
        "$COL_PROF_ID = ?",
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

    private fun criarTabelaEquipamentos(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_EQUIPAMENTOS (
                $COL_EQUIP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EQUIP_NOME TEXT NOT NULL,
                $COL_EQUIP_CATEGORIA TEXT NOT NULL,
                $COL_EQUIP_QUANTIDADE INTEGER NOT NULL DEFAULT 1,
                $COL_EQUIP_STATUS TEXT NOT NULL DEFAULT 'Disponível'
            )
            """.trimIndent()
        )
    }

    private fun criarTabelaProfessores(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_PROFESSORES (
                $COL_PROF_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PROF_NOME TEXT NOT NULL,
                $COL_PROF_ESPECIALIDADE TEXT NOT NULL,
                $COL_PROF_FOTO TEXT DEFAULT ''
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

    private fun inserirEquipamentosIniciais(db: SQLiteDatabase) {
        val total = db.rawQuery("SELECT COUNT(*) FROM $TABLE_EQUIPAMENTOS", null).use {
            it.moveToFirst(); it.getInt(0)
        }
        if (total > 0) return

        listOf(
            arrayOf("Esteira", "Cardio", 5, "Disponível"),
            arrayOf("Bicicleta Ergométrica", "Cardio", 4, "Disponível"),
            arrayOf("Leg Press", "Musculação", 2, "Manutenção"),
            arrayOf("Halteres", "Musculação", 20, "Disponível")
        ).forEach { (nome, categoria, qtd, status) ->
            val values = ContentValues().apply {
                put(COL_EQUIP_NOME, nome as String)
                put(COL_EQUIP_CATEGORIA, categoria as String)
                put(COL_EQUIP_QUANTIDADE, qtd as Int)
                put(COL_EQUIP_STATUS, status as String)
            }
            db.insert(TABLE_EQUIPAMENTOS, null, values)
        }
    }

    private fun inserirProfessoresIniciais(db: SQLiteDatabase) {
        val total = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PROFESSORES", null).use {
            it.moveToFirst(); it.getInt(0)
        }
        if (total > 0) return

        listOf(
            Triple("Joan Mendes", "Crossfit e Força", "joan_mendes"),
            Triple("Ana Costa", "Ginástica & Mobilidade", "ana_costa"),
            Triple("Pedro Lima", "Cardio e Ritmo", "pedro_lima"),
            Triple("Joana Mendes", "Flexibilidade e Prevenção", "joan_mendes2")
        ).forEach { (nome, especialidade, foto) ->
            val values = ContentValues().apply {
                put(COL_PROF_NOME, nome)
                put(COL_PROF_ESPECIALIDADE, especialidade)
                put(COL_PROF_FOTO, foto)
            }
            db.insert(TABLE_PROFESSORES, null, values)
        }
    }

    private fun extrairUsuario(cursor: android.database.Cursor): UsuarioData {
        val colunaProfessor = cursor.getColumnIndexOrThrow(COL_PROFESSOR_ID)
        return UsuarioData(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
            nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
            idade = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IDADE)),
            altura = cursor.getString(cursor.getColumnIndexOrThrow(COL_ALTURA)),
            peso = cursor.getString(cursor.getColumnIndexOrThrow(COL_PESO)),
            plano = cursor.getString(cursor.getColumnIndexOrThrow(COL_PLANO)),
            professorId = if (cursor.isNull(colunaProfessor)) null else cursor.getLong(colunaProfessor),
            treinos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TREINOS)),
            seguidores = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEGUIDORES)),
            diasSeguidos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DIAS_SEGUIDOS)),
            cep = cursor.getString(cursor.getColumnIndexOrThrow(COL_CEP)) ?: "",
            endereco = cursor.getString(cursor.getColumnIndexOrThrow(COL_ENDERECO)) ?: "",
            foto = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO))
        )
    }

    private fun extrairExercicio(cursor: android.database.Cursor): Exercicio = Exercicio(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXERCICIO_ID)),
        nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCICIO_NOME)),
        categoria = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCICIO_CATEGORIA))
    )

    private fun extrairEquipamento(cursor: Cursor): Equipamento = Equipamento(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_EQUIP_ID)),
        nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_EQUIP_NOME)),
        categoria = cursor.getString(cursor.getColumnIndexOrThrow(COL_EQUIP_CATEGORIA)),
        quantidade = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EQUIP_QUANTIDADE)),
        status = cursor.getString(cursor.getColumnIndexOrThrow(COL_EQUIP_STATUS))
    )

    private fun extrairProfessor(cursor: Cursor): Professor {
        return Professor(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PROF_ID)),
            nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROF_NOME)),
            especialidade = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROF_ESPECIALIDADE)),
            foto = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROF_FOTO))
        )
    }
}
