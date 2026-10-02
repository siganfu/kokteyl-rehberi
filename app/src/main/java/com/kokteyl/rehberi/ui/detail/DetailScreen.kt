package com.kokteyl.rehberi.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kokteyl.rehberi.data.mapping.TextUtils
import com.kokteyl.rehberi.domain.CocktailDetail
import com.kokteyl.rehberi.ui.components.CocktailImage
import com.kokteyl.rehberi.ui.components.EmptyState
import com.kokteyl.rehberi.ui.components.FavoriteButton
import com.kokteyl.rehberi.ui.components.LoadingBox
import com.kokteyl.rehberi.ui.components.SectionTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    vm: DetailViewModel,
    onBack: () -> Unit
) {
    val detail by vm.detail.collectAsStateWithLifecycle()
    val translation by vm.translation.collectAsStateWithLifecycle()
    val d = detail

    if (d == null) {
        Box(Modifier.fillMaxSize()) {
            LoadingBox()
            BackButton(onBack, Modifier.align(Alignment.TopStart).padding(12.dp))
        }
        return
    }

    val c = d.cocktail
    val needsTranslation = c.instructions.isBlank() && !c.instructionsEn.isNullOrBlank()
    LaunchedEffect(c.id, needsTranslation) {
        if (needsTranslation) vm.ensureTranslated()
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { Hero(d, onBack, vm::toggleFavorite) }

        item {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(c.category) })
                    AssistChip(onClick = {}, label = { Text(if (c.alcoholic) "Alkollü" else "Alkolsüz") })
                }
            }
        }

        item { SectionTitle("MALZEMELER", Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)) }
        items(d.lines) { line ->
            Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(line.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (line.optional) {
                        Text(
                            "İsteğe bağlı / süsleme",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (line.owned) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Elinde var",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
                Text(
                    TextUtils.upperTr(line.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(
                Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            }
        }

        item { SectionTitle("HAZIRLANIŞI", Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)) }
        when {
            d.steps.isNotEmpty() -> itemsIndexed(d.steps) { index, step ->
                StepRow(index + 1, step)
            }

            translation == TranslationUiState.Failed -> item {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        "Tarif Türkçeye çevrilemedi. Çeviri modeli için ilk seferde internet bağlantısı gerekir.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = vm::ensureTranslated) { Text("Tekrar dene") }
                }
            }

            needsTranslation -> item {
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.size(12.dp))
                    Text(
                        "Tarif Türkçeye çevriliyor… (ilk kullanımda çeviri modeli indirilir)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> item {
                Text(
                    "Bu kokteyl için tarif metni bulunamadı.",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item { SectionTitle("KOKTEYL BİLGİLERİ", Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)) }
        item {
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    InfoRow("Bardak", c.glass)
                    InfoRow("Hazırlama", c.method)
                    InfoRow("Alkol", if (c.alcoholic) c.mainSpirit ?: "Alkollü" else "Alkolsüz")
                    InfoRow("Kategori", c.category)
                    InfoRow("Garnitür", c.garnish)
                }
            }
        }
    }
}

@Composable
private fun Hero(d: CocktailDetail, onBack: () -> Unit, onFavorite: (Boolean) -> Unit) {
    val c = d.cocktail
    Box(
        Modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        CocktailImage(
            url = c.imageUrl,
            contentDescription = c.turkishName,
            modifier = Modifier.fillMaxSize(),
            large = true
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to Color.Transparent,
                        1f to MaterialTheme.colorScheme.background
                    )
                )
        )
        BackButton(onBack, Modifier.align(Alignment.TopStart).padding(12.dp))
        FavoriteButton(
            favorite = c.favorite,
            onToggle = onFavorite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )
        Text(
            c.turkishName,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun BackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(onClick = onBack),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Geri",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$number",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
        Spacer(Modifier.size(14.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Column {
        Text(
            label.uppercase(TextUtils.TR),
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
