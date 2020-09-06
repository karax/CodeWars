package com.jeankarax.codewars.model.room

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jeankarax.codewars.model.response.UserResponse

@Database(entities = [UserResponse::class], version = 1)
@TypeConverters(
    StringListConverter::class,
    LanguageRankConverter::class,
    DateConverter::class,
    RankConverter::class
)
abstract class UserLocalDataBase: RoomDatabase() {
    abstract fun userDAO(): UserDAO

    companion object{
        @Volatile
        private var instance: UserLocalDataBase? = null
        private val LOCK = Any()

        operator fun invoke(application: Application) = instance?: synchronized(LOCK){
            instance?: buildDataBase(application).also{
                instance = it
            }
        }

        private fun buildDataBase(application: Application) = Room.databaseBuilder(
            application.applicationContext,
            UserLocalDataBase::class.java,
            "userdatabase"
        ).build()
    }
}