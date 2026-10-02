package com.kokteyl.rehberi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

private const val SUMMARY =
    "SELECT id, turkishName, imageUrl, category, mainSpirit, shortInfo, alcoholic, favorite FROM cocktails"

@Dao
interface CocktailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cocktail: CocktailEntity): Long

    @Update
    suspend fun update(cocktail: CocktailEntity)

    @Insert
    suspend fun insertLinks(links: List<CocktailIngredientEntity>)

    @Query("DELETE FROM cocktail_ingredients WHERE cocktailId = :cocktailId")
    suspend fun deleteLinks(cocktailId: String)

    @Query("SELECT * FROM cocktails WHERE nameKey = :key LIMIT 1")
    suspend fun findByKey(key: String): CocktailEntity?

    @Query("SELECT * FROM cocktails WHERE id = :id LIMIT 1")
    suspend fun get(id: String): CocktailEntity?

    @Query("SELECT * FROM cocktails WHERE id = :id LIMIT 1")
    fun observe(id: String): Flow<CocktailEntity?>

    @Query("SELECT * FROM cocktail_ingredients WHERE cocktailId = :id ORDER BY position")
    fun observeLinks(id: String): Flow<List<CocktailIngredientEntity>>

    @Query("SELECT cocktailId, ingredientId, optional FROM cocktail_ingredients")
    fun observeLinkRows(): Flow<List<LinkRow>>

    @Query("SELECT COUNT(*) FROM cocktails")
    suspend fun count(): Int

    @Query("SELECT id FROM cocktails ORDER BY RANDOM() LIMIT 1")
    suspend fun randomId(): String?

    @Query("UPDATE cocktails SET favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("UPDATE cocktails SET imageUrl = :url WHERE id = :id")
    suspend fun setImage(id: String, url: String)

    @Query("UPDATE cocktails SET instructions = :text WHERE id = :id")
    suspend fun setInstructions(id: String, text: String)

    @Query("SELECT id FROM cocktails WHERE instructions = '' AND instructionsEn IS NOT NULL AND instructionsEn != ''")
    suspend fun pendingTranslationIds(): List<String>

    @Query(
        "$SUMMARY WHERE (:query = '' OR searchText LIKE '% ' || :query || '%') " +
            "AND (:tag = '' OR (' ' || tags || ' ') LIKE '% ' || :tag || ' %') " +
            "ORDER BY (popularRank = 0), popularRank, turkishName COLLATE NOCASE"
    )
    fun observeSummaries(query: String, tag: String): Flow<List<CocktailSummary>>

    @Query("$SUMMARY ORDER BY (popularRank = 0), popularRank, turkishName COLLATE NOCASE")
    fun observeAllSummaries(): Flow<List<CocktailSummary>>

    @Query("$SUMMARY WHERE popularRank > 0 ORDER BY popularRank")
    fun observePopular(): Flow<List<CocktailSummary>>

    @Query("$SUMMARY WHERE favorite = 1 ORDER BY turkishName COLLATE NOCASE")
    fun observeFavorites(): Flow<List<CocktailSummary>>
}

@Dao
interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<IngredientEntity>)

    @Update
    suspend fun update(ingredient: IngredientEntity)

    @Query("SELECT * FROM ingredients")
    fun observeAll(): Flow<List<IngredientEntity>>

    /** Seçim ekranında gösterilecek (çevirisi hazır, gizli olmayan) malzemeler */
    @Query("SELECT * FROM ingredients WHERE translated = 1 AND hidden = 0")
    fun observePicker(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE translated = 0")
    suspend fun pendingTranslation(): List<IngredientEntity>

    // Kullanıcının elindeki malzemeler
    @Query("SELECT ingredientId FROM user_ingredients")
    fun observeSelected(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun select(item: UserIngredientEntity)

    @Query("DELETE FROM user_ingredients WHERE ingredientId = :id")
    suspend fun deselect(id: String)

    @Query("DELETE FROM user_ingredients")
    suspend fun clearSelection()
}
