package com.jeankarax.codewars.model.room

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.UserResponse

@Database(entities = [UserResponse::class, ChallengeResponse::class], version = 1)
@TypeConverters(
    StringListConverter::class,
    LanguageRankConverter::class,
    DateConverter::class,
    RankConverter::class,
    UserConverter::class
)
abstract class UserLocalDataBase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun challengeDAO(): ChallengesDAO

    companion object{
        @Volatile
        private var instance: UserLocalDataBase? = null
        private val LOCK = Any()

        operator fun invoke(application: Application) = instance?: synchronized(LOCK){
            instance?: buildDataBase(application).also{
                instance = it
            }
        }

        //private val migration1to2 = object : Migration(1,2){
        //    override fun migrate(database: SupportSQLiteDatabase) {
        //        database.execSQL("CREATE TABLE IF NOT EXISTS 'Challenge'()")
        //    }
        //}

        private fun buildDataBase(application: Application) = Room.databaseBuilder(
            application.applicationContext,
            UserLocalDataBase::class.java,
            "userdatabase"
        ).allowMainThreadQueries().build()
    }
}