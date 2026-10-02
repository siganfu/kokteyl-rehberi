package com.kokteyl.rehberi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CocktailEntity::class,
        IngredientEntity::class,
        CocktailIngredientEntity::class,
        UserIngredientEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cocktailDao(): CocktailDao
    abstract fun ingredientDao(): IngredientDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "kokteyl.db")
                // Şema değişirse veriler yeniden indirilebildiği için basit tutuldu.
                // Yayın öncesi gerçek Migration yazmanız önerilir (favoriler korunsun diye).
                .fallbackToDestructiveMigration()
                .build()
    }
}
