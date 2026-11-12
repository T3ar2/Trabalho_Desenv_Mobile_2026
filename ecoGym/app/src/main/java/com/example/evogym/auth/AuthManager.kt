package com.example.evogym.auth

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal object AuthManager {
    private const val ITERACOES = 100_000

    fun criarSalt(): ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }

    fun gerarHash(senha: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(senha.toCharArray(), salt, ITERACOES, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    fun validarSenha(senha: String, salt: ByteArray, hash: ByteArray): Boolean {
        val calculado = gerarHash(senha, salt)
        return try {
            MessageDigest.isEqual(hash, calculado)
        } finally {
            calculado.fill(0)
        }
    }
}
