package com.example.evogym.validation

import android.util.Patterns

internal object CadastroValidator {
    fun validar(nome: String, email: String, senha: String) {
        validarNome(nome)
        require(email.length <= 254 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Informe um e-mail válido."
        }
        require(senha.length in 6..128 && senha.isNotBlank()) {
            "Use uma senha entre 6 e 128 caracteres."
        }
    }

    fun validarNome(nome: String) {
        require(nome.length in 3..100) { "Informe um nome entre 3 e 100 caracteres." }
    }

    fun validarIdade(idade: String): Int {
        val anos = idade.trim().toIntOrNull()
        require(anos != null && anos > 0) { "Informe uma idade válida." }
        return anos
    }

    fun validarAltura(altura: String): String {
        val valor = altura.trim()
        require(medidaValida(valor)) { "Informe uma altura válida em metros." }
        return "$valor m"
    }

    fun validarPeso(peso: String): String {
        val valor = peso.trim()
        require(medidaValida(valor)) { "Informe um peso válido em quilos." }
        return "$valor kg"
    }

    fun validarPlano(plano: String): String {
        require(plano.isNotBlank()) { "Informe o plano." }
        return plano.trim()
    }

    fun cepValido(cep: String): Boolean = Regex("[0-9]{5}-?[0-9]{3}").matches(cep)

    private fun medidaValida(valor: String): Boolean =
        Regex("[0-9]+([,.][0-9]+)?").matches(valor) &&
            valor.replace(',', '.').toDoubleOrNull()?.let { it.isFinite() && it > 0 } == true
}
