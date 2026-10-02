package com.kokteyl.rehberi.ui.cocktails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.kokteyl.rehberi.ui.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailsScreen(
    vm: CocktailsViewModel,
    onOpenCocktail: (String) -> Unit
) {
    val query by vm.query.collectAsStateWithLifecycle()
    val tag by vm.tag.collectAsStateWithLifecycle()
    val results by vm.results.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Text(
            "KOKTEYLLER",
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 20.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )
        SearchField(
            value = query,
            onValueChange = vm::onQueryChange,
            placeholder = "Kokteyl veya malzeme ara...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(COCKTAIL_FILTERS, key = { it.second }) { (label, value) ->
                FilterChip(
                    selected = tag == value,
                    onClick = { vm.onTagChange(value) },
                    label = { Text(label) }
                )
            }
        }
        val list = results
        when {
            list == null -> LoadingBox()
            list.isEmpty() -> EmptyState("🔍", "Sonuç bulunamadı.", subtitle = "Farklı bir arama veya filtre deneyin.")
            else -> CocktailGrid(
                items = list,
                onOpen = onOpenCocktail,
                onFavorite = vm::toggleFavorite,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
