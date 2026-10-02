package com.kokteyl.rehberi.ui.results

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kokteyl.rehberi.domain.MatchItem
import com.kokteyl.rehberi.ui.components.CocktailImage
import com.kokteyl.rehberi.ui.components.LoadingBox
import kotlin.math.min

private val GROUPS = listOf(
    0 to "TAM UYUMLU",
    1 to "EKSİK 1 MALZEME",
    2 to "EKSİK 2 MALZEME",
    3 to "EKSİK 3 VEYA DAHA FAZLA MALZEME"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    vm: ResultsViewModel,
    onBack: () -> Unit,
    onOpenCocktail: (String) -> Unit,
    onPickIngredients: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
            Column {
                Text(
                    "SONUÇLAR",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                if (state.selectedCount > 0) {
                    Text(
                        "${state.selectedCount} malzemeye göre ${state.items.size} kokteyl",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        when {
            state.loading -> LoadingBox()

            state.selectedCount == 0 -> CenterMessage(
                emoji = "🧊",
                title = "Henüz malzeme seçmedin",
                subtitle = "Elindeki malzemeleri seç, hangi kokteylleri yapabileceğini gösterelim.",
                buttonText = "MALZEME SEÇ",
                onClick = onPickIngredients
            )

            state.items.isEmpty() -> CenterMessage(
                emoji = "🔍",
                title = "Sonuç bulunamadı.",
                subtitle = "Seçtiğin malzemelerle eşleşen bir kokteyl yok. Biraz daha malzeme ekleyerek tekrar dene.",
                buttonText = "MALZEMELERİ DÜZENLE",
                onClick = onPickIngredients
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GROUPS.forEach { (bucket, title) ->
                    val list = state.items.filter { min(it.missing, 3) == bucket }
                    if (list.isNotEmpty()) {
                        item(key = "header_$bucket") {
                            Text(
                                "$title · ${list.size}",
                                modifier = Modifier.padding(top = 10.dp, start = 4.dp),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = if (bucket == 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        }
                        items(list, key = { "${bucket}_${it.summary.id}" }) { item ->
                            MatchCard(item) { onOpenCocktail(item.summary.id) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchCard(item: MatchItem, onClick: () -> Unit) {
    val progress by animateFloatAsState(
        targetValue = if (item.required == 0) 0f else item.have / item.required.toFloat(),
        label = "matchProgress"
    )
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CocktailImage(
                url = item.summary.imageUrl,
                contentDescription = item.summary.turkishName,
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.summary.turkishName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${item.have} / ${item.required} MALZEME · %${item.percent} TAMAM",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    color = if (item.missing == 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
                if (item.missing == 0) {
                    Text(
                        "✓ Tamamen hazır",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        "⚠ ${item.missing} malzeme eksik",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val shown = item.missingNames.take(3).joinToString(", ")
                    val more = item.missingNames.size - 3
                    Text(
                        "Eksik: $shown" + if (more > 0) " +$more" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterMessage(
    emoji: String,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onClick) { Text(buttonText, fontWeight = FontWeight.Black) }
    }
}
