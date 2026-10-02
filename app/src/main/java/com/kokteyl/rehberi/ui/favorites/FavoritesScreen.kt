package com.kokteyl.rehberi.ui.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kokteyl.rehberi.ui.components.CocktailGrid
import com.kokteyl.rehberi.ui.components.EmptyState
import com.kokteyl.rehberi.ui.components.LoadingBox

@Composable
fun FavoritesScreen(
    vm: FavoritesViewModel,
    onOpenCocktail: (String) -> Unit
) {
    val favorites by vm.favorites.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Text(
            "FAVORİLER",
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 4.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )
        val list = favorites
        when {
            list == null -> LoadingBox()
            list.isEmpty() -> EmptyState(
                emoji = "🤍",
                title = "Henüz favori kokteylin yok",
                subtitle = "Bir kokteylin kalp butonuna dokunarak buraya ekleyebilirsin. Favorilerin internetsiz de çalışır."
            )
            else -> CocktailGrid(
                items = list,
                onOpen = onOpenCocktail,
                onFavorite = vm::toggleFavorite,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
