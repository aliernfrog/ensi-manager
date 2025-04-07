package com.aliernfrog.ensimanager.util.staticutil

import android.util.Base64
import androidx.annotation.Keep
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY_ALGORITHM = "AES"
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val SALT_LENGTH = 16
    private const val ITERATION_COUNT = 65536
    private const val KEY_LENGTH = 256

    fun encrypt(string: String, password: String): EncryptedData {
        val salt = generateSalt()
        val secretKey = deriveKey(password, salt)
        val iv = generateIV()
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(string.toByteArray(Charsets.UTF_8))
        val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
        val saltString = Base64.encodeToString(salt, Base64.DEFAULT)
        return EncryptedData(encryptedString, ivString, saltString)
    }

    fun decrypt(encryptedData: EncryptedData, password: String): String {
        val salt = Base64.decode(encryptedData.salt, Base64.DEFAULT)
        val iv = Base64.decode(encryptedData.iv, Base64.DEFAULT)
        val encryptedBytes = Base64.decode(encryptedData.data, Base64.DEFAULT)
        val secretKey = deriveKey(password, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val key = factory.generateSecret(spec)
        return SecretKeySpec(key.encoded, SECRET_KEY_ALGORITHM)
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    private fun generateIV(): ByteArray {
        val random = SecureRandom()
        val iv = ByteArray(16)
        random.nextBytes(iv)
        return iv
    }
}

@Keep
data class EncryptedData(
    val data: String,
    val iv: String,
    val salt: String
)