package com.example.dulz_mealapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE meals_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, category TEXT NOT NULL, area TEXT NOT NULL, instructions TEXT NOT NULL, tags TEXT NOT NULL, youtubeLink TEXT NOT NULL, measures TEXT NOT NULL, ingredients TEXT NOT NULL, mealThumb TEXT NOT NULL)")
        database.execSQL("ALTER TABLE meals ADD COLUMN mealThumb NOT NULL DEFAULT ''")
        database.execSQL("INSERT INTO meals_new (id, name, category, area, instructions, tags, youtubeLink, measures, ingredients,mealThumb) SELECT id, name, category, area, instructions, tags, youtubeLink, measures, ingredients,'' FROM meals")
        database.execSQL("DROP TABLE meals")
        database.execSQL("ALTER TABLE meals_new RENAME TO meals")
    }
}


@Database(entities = [Meal::class], version = 3)
@TypeConverters(ListStringConverter::class)
abstract class MealDatabase : RoomDatabase(){
    abstract fun mealDao() : MealDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getInstance(context: Context): MealDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_db"
                ).addMigrations(migration_2_3)
                    .build()
                INSTANCE = instance
                instance  
            }
        }
    }
}