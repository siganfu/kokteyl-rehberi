package com.kokteyl.rehberi.ui.ingredients

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kokteyl.rehberi.ui.components.EmptyState
import com.kokteyl.rehberi.ui.components.SearchField
import com.kokteyl.rehberi.ui.components.SectionTitle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IngredientsScreen(
    vm: IngredientsViewModel,
    onFind: () -> Unit
) {
    val sections by vm.sections.collectAsStateWithLifecycle()
    val selected by vm.selected.collectAsStateWithLifecycle()
    val query by vm.query.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ELİMDEKİ MALZEMELER",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                if (selected.isNotEmpty()) {
                    TextButton(onClick = vm::clear) { Text("Temizle (${selected.size})") }
                }
            }
            SearchField(
                value = query,
                onValueChange = vm::onQueryChange,
                placeholder = "Malzeme ara... (örn. passion)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (sections.isEmpty()) {
                EmptyState("🔍", "Sonuç bulunamadı.", subtitle = "Farklı bir malzeme adı deneyin.")
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
                    sections.forEach { section ->
                        item(key = section.title) {
                            Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                                SectionTitle(section.title)
                                Spacer(Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    section.items.forEach { ing ->
                                        val isSelected = ing.id in selected
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { vm.toggle(ing.id, !isSelected) },
                                            label = { Text(ing.turkishName) },
                                            leadingIcon = if (isSelected) {
                                                {
                                                    Icon(
                                                        Icons.Filled.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                    )
                                                }
                                            } else null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selected.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            ExtendedFloatingActionButton(
                onClick = onFind,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("COCKTAIL BUL (${selected.size})", fontWeight = FontWeight.Black)
            }
        }
    }
}
