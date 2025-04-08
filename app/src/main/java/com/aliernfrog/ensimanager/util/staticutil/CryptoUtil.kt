package com.aliernfrog.ensimanager.util.staticutil

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.Keep
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
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

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val BIOMETRIC_KEY_ALIAS = "ensimanager_biometric_key"
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    fun encryptWithPassword(string: String, password: String, withBiometrics: Boolean): EncryptedData {
        val masterKey = generateMasterKey()
        val iv = generateIV()
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(string.toByteArray(Charsets.UTF_8))
        val passwordSalt = generateSalt()
        val passwordWrappedKey = wrapKey(masterKey, deriveKey(password, passwordSalt))
        return EncryptedData(
            data = Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            iv = Base64.encodeToString(iv, Base64.DEFAULT),
            salt = Base64.encodeToString(passwordSalt, Base64.DEFAULT),
            passwordWrappedKey = Base64.encodeToString(passwordWrappedKey, Base64.DEFAULT),
            biometricWrappedKey = if (withBiometrics) getBiometricKey()?.let {
                Base64.encodeToString(wrapKey(masterKey, it), Base64.DEFAULT)
            } else null
        )
    }

    fun reencryptWithKey(
        string: String,
        masterKey: SecretKey,
        reference: EncryptedData,
        withBiometrics: Boolean
    ): EncryptedData {
        val iv = Base64.decode(reference.iv, Base64.DEFAULT)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(string.toByteArray(Charsets.UTF_8))
        return EncryptedData(
            data = Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            iv = Base64.encodeToString(iv, Base64.DEFAULT),
            salt = reference.salt,
            passwordWrappedKey = reference.passwordWrappedKey,
            biometricWrappedKey = if (withBiometrics) getBiometricKey()?.let {
                Base64.encodeToString(wrapKey(masterKey, it), Base64.DEFAULT)
            } else reference.biometricWrappedKey
        )
    }

    fun decryptWithPassword(encryptedData: EncryptedData, password: String): DecryptResult {
        val masterKey = unwrapKey(
            Base64.decode(encryptedData.passwordWrappedKey, Base64.DEFAULT),
            deriveKey(password, Base64.decode(encryptedData.salt, Base64.DEFAULT))
        )
        val data = decrypt(encryptedData, masterKey!!)
        return DecryptResult(data, masterKey)
    }

    fun decryptWithBiometrics(encryptedData: EncryptedData): DecryptResult? {
        return getBiometricKey()?.let {
            val masterKey = unwrapKey(
                Base64.decode(encryptedData.biometricWrappedKey, Base64.DEFAULT),
                it
            )
            val data = decrypt(encryptedData, masterKey!!)
            DecryptResult(data, masterKey)
        }
    }

    private fun decrypt(encryptedData: EncryptedData, secretKey: SecretKey): String {
        val iv = Base64.decode(encryptedData.iv, Base64.DEFAULT)
        val encryptedBytes = Base64.decode(encryptedData.data, Base64.DEFAULT)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun generateMasterKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(SECRET_KEY_ALGORITHM)
        keyGenerator.init(KEY_LENGTH)
        return keyGenerator.generateKey()
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val key = factory.generateSecret(spec)
        return SecretKeySpec(key.encoded, SECRET_KEY_ALGORITHM)
    }

    private fun wrapKey(keyToWrap: SecretKey, wrappingKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding", ANDROID_KEY_STORE)
        cipher.init(Cipher.WRAP_MODE, wrappingKey)
        return cipher.wrap(keyToWrap)
    }

    private fun unwrapKey(wrappedKey: ByteArray, unwrappingKey: SecretKey): SecretKey? {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding", ANDROID_KEY_STORE)
        cipher.init(Cipher.UNWRAP_MODE, unwrappingKey)
        return cipher.unwrap(wrappedKey, SECRET_KEY_ALGORITHM, Cipher.SECRET_KEY) as? SecretKey
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

    fun generateBiometricKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            BIOMETRIC_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    fun hasBiometricKey(): Boolean = keyStore.containsAlias(BIOMETRIC_KEY_ALIAS)

    private fun getBiometricKey(): SecretKey? {
        return if (hasBiometricKey()) {
            keyStore.getKey(BIOMETRIC_KEY_ALIAS, null) as? SecretKey
        } else {
            null
        }
    }
}

@Keep
data class EncryptedData(
    val data: String,
    val iv: String,
    val salt: String,
    val passwordWrappedKey: String? = null,
    val biometricWrappedKey: String? = null
)

data class DecryptResult(
    val decryptedData: String,
    val masterKey: SecretKey
)