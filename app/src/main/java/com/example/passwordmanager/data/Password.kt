package com.example.passwordmanager.data

import androidx.compose.runtime.saveable.Saver
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val accountType: String,
    val username: String,
    val encryptedPassword: String
)

val PasswordSaver: Saver<Password, Any> = Saver(
    save = { password ->
        listOf(password.id, password.accountType, password.username, password.encryptedPassword)
    },
    restore = { list ->
        val (id, accountType, username, encryptedPassword) = list as List<*>
        Password(
            id = id as Int,
            accountType = accountType as String,
            username = username as String,
            encryptedPassword = encryptedPassword as String
        )
    }
)