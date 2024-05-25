package com.example.passwordmanager.util

import android.annotation.SuppressLint
import android.util.Base64
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object Utils {
    private var key: String = "mySecretKey12345"
    private var secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")

    fun generateStrongPassword(): String {
        val length = 8
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf(
            '!',
            '@',
            '#',
            '$',
            '%',
            '^',
            '&',
            '*',
            '(',
            ')',
            '-',
            '_',
            '=',
            '+'
        )
        val password = StringBuilder()


        password.append(allowedChars.random())
        password.append(('A'..'Z').random())
        password.append(('a'..'z').random())
        password.append(('0'..'9').random())


        repeat(length - 4) {
            password.append(allowedChars.random())
        }

        return password.toString().toList().shuffled().joinToString("")
    }

    fun capitalizeFirstLetter(input: String): String {
        if (input.isEmpty()) {
            return input
        }
        return input.substring(0, 1).uppercase(Locale.ROOT) + input.substring(1)
    }


    @SuppressLint("GetInstance")
    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    @SuppressLint("GetInstance")
    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decrypted = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT))
        return String(decrypted)
    }

    fun getPasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) {
            return PasswordStrength.WEAK
        }

        val digitRegex = Regex("\\d")
        val upperCaseRegex = Regex("[A-Z]")
        val lowerCaseRegex = Regex("[a-z]")
        val specialCharRegex = Regex("[^A-Za-z0-9]")

        val containsDigit = digitRegex.containsMatchIn(password)
        val containsUpperCase = upperCaseRegex.containsMatchIn(password)
        val containsLowerCase = lowerCaseRegex.containsMatchIn(password)
        val containsSpecialChar = specialCharRegex.containsMatchIn(password)

        val hasEnoughLength = password.length >= 8

        return when {
            hasEnoughLength && containsDigit && containsUpperCase && containsLowerCase && containsSpecialChar -> PasswordStrength.STRONG
            (hasEnoughLength && containsDigit && containsUpperCase) || (hasEnoughLength && containsDigit && containsLowerCase) || (hasEnoughLength && containsDigit && containsSpecialChar) ||
                    (hasEnoughLength && containsUpperCase && containsLowerCase) || (hasEnoughLength && containsUpperCase && containsSpecialChar) ||
                    (hasEnoughLength && containsLowerCase && containsSpecialChar) -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }
}

enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG
}