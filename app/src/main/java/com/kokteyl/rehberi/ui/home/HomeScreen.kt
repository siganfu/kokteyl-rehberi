package com.kokteyl.rehberi.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kokteyl.rehberi.data.repository.SyncState
import com.kokteyl.rehberi.ui.components.CocktailCard
import com.kokteyl.rehberi.ui.components.CocktailGrid
import com.kokteyl.rehberi.ui.components.EmptyState
import com.kokteyl.rehberi.ui.components.SearchField
import com.kokteyl.rehberi.ui.components.SectionTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onOpenCocktail: (String) -> Unit,
    onOpenIngredients: () -> Unit,
    onOpenResults: () -> Unit,
    onOpenCocktails: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val query by vm.query.collectAsStateWithLifecycle()
    val popular by vm.popular.collectAsStateWithLifecycle()
    val results by vm.results.collectAsStateWithLifecycle()
    val selectedCount by vm.selectedCount.collectAsStateWithLifecycle()
    val sync by vm.syncState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🍸 KOKTEYL REHBERİ",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Ayarlar")
            }
        }
        SearchField(
            value = query,
            onValueChange = vm::onQueryChange,
            placeholder = "Kokteyl veya malzeme ara...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (query.isNotBlank()) {
            if (results.isEmpty()) {
                EmptyState("🔍", "Sonuç bulunamadı.", subtitle = "Farklı bir kelime deneyin.")
            } else {
                CocktailGrid(
                    items = results,
                    onOpen = onOpenCocktail,
                    onFavorite = vm::toggleFavorite,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { SyncBanner(sync) }

                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BigTile(
                            emoji = "🍸",
                            title = "KOKTEYL BUL",
                            subtitle = if (selectedCount > 0) "$selectedCount malzemeyle eşleştir" else "Malzemelerini seç, önerelim",
                            container = MaterialTheme.colorScheme.primary,
                            content = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            onClick = if (selectedCount > 0) onOpenResults else onOpenIngredients
                        )
                        BigTile(
                            emoji = "🧊",
                            title = "ELİMDEKİ MALZEMELER",
                            subtitle = if (selectedCount > 0) "$selectedCount seçili" else "Henüz seçim yok",
                            container = MaterialTheme.colorScheme.surfaceVariant,
                            content = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenIngredients
                        )
                    }
                }

                item {
                    Button(
                        onClick = { vm.pickRandom(onOpenCocktail) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("🎲  BUGÜN NE İÇSEM?", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { AssistChip(onClick = { onOpenCocktails("alkolsuz") }, label = { Text("🥤 Alkolsüz Kokteyller") }) }
                        item { AssistChip(onClick = { onOpenCocktails("klasik") }, label = { Text("🥃 Klasikler") }) }
                        item { AssistChip(onClick = { onOpenCocktails("tiki") }, label = { Text("🌴 Tiki") }) }
                        item { AssistChip(onClick = { onOpenCocktails("sour") }, label = { Text("🍋 Sour") }) }
                        item { AssistChip(onClick = { onOpenCocktails("") }, label = { Text("📖 Tümü") }) }
                    }
                }

                item { SectionTitle("POPÜLER KOKTEYLLER", Modifier.padding(horizontal = 20.dp)) }

                item {
                    if (popular.isEmpty()) {
                        EmptyState(
                            emoji = "🍹",
                            title = "Kokteyller hazırlanıyor…",
                            modifier = Modifier.height(200.dp)
                        )
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(popular, key = { it.id }) { c ->
                                CocktailCard(
                                    item = c,
                                    onClick = { onOpenCocktail(c.id) },
                                    onFavorite = { fav -> vm.toggleFavorite(c.id, fav) },
                                    modifier = Modifier.width(210.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BigTile(
    emoji: String,
    title: String,
    subtitle: String,
    container: Color,
    content: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(128.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 30.sp)
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SyncBanner(state: SyncState) {
    when (state) {
        is SyncState.Running -> Card(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Kokteyl veritabanı güncelleniyor…",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { if (state.total == 0) 0f else state.done / state.total.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }
        }

        is SyncState.Failed -> Card(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(
                state.message,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        else -> Unit
    }
}
