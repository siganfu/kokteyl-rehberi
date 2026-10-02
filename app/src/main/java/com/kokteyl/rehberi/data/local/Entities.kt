package com.kokteyl.rehberi.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Kokteyl kaydı. Malzemeler ayrı tabloda (CocktailIngredientEntity) ilişkisel olarak tutulur.
 * instructions: Türkçe adımlar, satır sonu (\n) ile ayrılmış. Boşsa henüz çevrilmemiştir.
 * tags: boşlukla ayrılmış küçük harf ASCII etiketler (filtreleme için), örn: "votka klasik sour".
 * searchText: aramada kullanılan, aksanları temizlenmiş metin (başında boşluk ile).
 */
@Entity(
    tableName = "cocktails",
    indices = [Index(value = ["nameKey"], unique = true), Index("popularRank"), Index("favorite")]
)
data class CocktailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val turkishName: String,
    val nameKey: String,
    val imageUrl: String?,
    val category: String,
    val alcoholic: Boolean,
    val mainSpirit: String?,
    val glass: String?,
    val method: String?,
    val garnish: String?,
    val instructions: String,
    val instructionsEn: String?,
    val shortInfo: String,
    val tags: String,
    val searchText: String,
    val curated: Boolean,
    val popularRank: Int,
    val favorite: Boolean = false
)

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val turkishName: String,
    val category: String,
    val ingredientGroup: String?,
    val alcoholic: Boolean,
    val garnish: Boolean,
    val generic: Boolean,
    val hidden: Boolean,
    val translated: Boolean
)

@Entity(
    tableName = "cocktail_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = CocktailEntity::class,
            parentColumns = ["id"],
            childColumns = ["cocktailId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("cocktailId"), Index("ingredientId")]
)
data class CocktailIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cocktailId: String,
    val ingredientId: String,
    val position: Int,
    /** Ekranda gösterilen hazır metin, örn: "50 ml", "10 yaprak", "Üstünü tamamla" */
    val amount: String,
    /** Mililitre karşılığı (varsa). İleride maliyet/ölçek hesabı için. */
    val amountMl: Double?,
    val unit: String?,
    /** Garnitür / buz / su gibi eşleştirmede zorunlu sayılmayan malzeme */
    val optional: Boolean
)

/** Kullanıcının "Elimdeki malzemeler" seçimi */
@Entity(tableName = "user_ingredients")
data class UserIngredientEntity(
    @PrimaryKey val ingredientId: String
)

/** Liste kartları için hafif projeksiyon */
data class CocktailSummary(
    val id: String,
    val turkishName: String,
    val imageUrl: String?,
    val category: String,
    val mainSpirit: String?,
    val shortInfo: String,
    val alcoholic: Boolean,
    val favorite: Boolean
)

/** Eşleştirme algoritması için hafif projeksiyon */
data class LinkRow(
    val cocktailId: String,
    val ingredientId: String,
    val optional: Boolean
)
