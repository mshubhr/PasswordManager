package com.example.passwordmanager.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providePasswordDatabase(@ApplicationContext context: Context): PasswordDatabase {
        return Room.databaseBuilder(
            context,
            PasswordDatabase::class.java,
            "password_database"
        ).build()
    }

    @Provides
    fun providePasswordDao(database: PasswordDatabase): PasswordDao {
        return database.passwordDao()
    }

    @Provides
    fun providePasswordRepository(dao: PasswordDao):PasswordRepository{
        return PasswordRepository(dao)
    }
}