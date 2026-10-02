package com.kokteyl.rehberi.data.remote

import com.kokteyl.rehberi.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * TheCocktailDB yanıtı. Her içki, alan adı -> değer haritası olarak gelir
 * (strDrink, strIngredient1..15, strMeasure1..15 ...). Boş alanlar null'dır.
 */
data class DrinksResponse(val drinks: List<Map<String, String?>>?)

interface CocktailApi {
    /** İsmi verilen harf/rakam ile başlayan kokteylleri getirir (a-z, 0-9). */
    @GET("search.php")
    suspend fun searchByFirstLetter(@Query("f") letter: String): DrinksResponse
}

object NetworkModule {
    val api: CocktailApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl("https://www.thecocktaildb.com/api/json/v1/${BuildConfig.COCKTAILDB_API_KEY}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CocktailApi::class.java)
    }
}
