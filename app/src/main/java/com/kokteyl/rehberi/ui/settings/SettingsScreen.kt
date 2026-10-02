package com.kokteyl.rehberi.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kokteyl.rehberi.BuildConfig
import com.kokteyl.rehberi.data.mapping.TextUtils
import com.kokteyl.rehberi.data.repository.SyncState
import com.kokteyl.rehberi.data.repository.TranslateState
import com.kokteyl.rehberi.data.settings.ThemeMode
import com.kokteyl.rehberi.ui.components.SectionTitle
import java.text.SimpleDateFormat
import java.util.Date

private enum class InfoDialog { ABOUT, PRIVACY }

@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    onBack: () -> Unit
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    val sync by vm.syncState.collectAsStateWithLifecycle()
    val translate by vm.translateState.collectAsStateWithLifecycle()
    var dialog by remember { mutableStateOf<InfoDialog?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
            Text("AYARLAR", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        }

        // ---------------- Veri
        SectionTitle("VERİLER", Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp))
        SettingsCard {
            Button(
                onClick = vm::sync,
                enabled = sync !is SyncState.Running,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Verileri Güncelle", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(10.dp))
            when (val s = sync) {
                is SyncState.Running -> {
                    Text("İndiriliyor… ${s.done}/${s.total}", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = { if (s.total == 0) 0f else s.done / s.total.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                is SyncState.Success -> Text(
                    "Güncelleme tamamlandı. Toplam ${s.total} kokteyl kayıtlı.",
                    style = MaterialTheme.typography.bodyMedium
                )

                is SyncState.Failed -> Text(
                    s.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                SyncState.Idle -> Text(
                    lastSyncText(vm.lastSyncMillis()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "Mevcut kokteyller, favoriler ve seçimlerin korunur; yalnızca yeni veya değişen tarifler eklenir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        SettingsCard {
            OutlinedButton(
                onClick = vm::translateAll,
                enabled = translate !is TranslateState.Running,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tarifleri Türkçeye çevir") }
            Spacer(Modifier.height(8.dp))
            when (val t = translate) {
                is TranslateState.Running -> {
                    Text("Çevriliyor… ${t.done}/${t.total}", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = { if (t.total == 0) 0f else t.done / t.total.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                TranslateState.Done -> Text("Tüm tarifler Türkçe.", style = MaterialTheme.typography.bodyMedium)
                is TranslateState.Failed -> Text(
                    t.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                TranslateState.Idle -> Text(
                    "İndirilen tarifler cihazında Türkçeye çevrilir ve internetsiz kullanılabilir. İlk seferde çeviri modeli (~30 MB) indirilir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ---------------- Tema
        SectionTitle("TEMA", Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp))
        SettingsCard {
            ThemeRow("Koyu Tema", theme == ThemeMode.DARK) { vm.setTheme(ThemeMode.DARK) }
            ThemeRow("Açık Tema", theme == ThemeMode.LIGHT) { vm.setTheme(ThemeMode.LIGHT) }
            ThemeRow("Sistem Teması", theme == ThemeMode.SYSTEM) { vm.setTheme(ThemeMode.SYSTEM) }
        }

        // ---------------- Uygulama
        SectionTitle("UYGULAMA", Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp))
        SettingsCard {
            InfoLine("Hakkında") { dialog = InfoDialog.ABOUT }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            InfoLine("Gizlilik") { dialog = InfoDialog.PRIVACY }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Uygulama Sürümü")
                Text(BuildConfig.VERSION_NAME, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(32.dp))
    }

    dialog?.let { d ->
        val (title, body) = when (d) {
            InfoDialog.ABOUT -> "Hakkında" to
                "Kokteyl Rehberi, kokteyl tariflerini Türkçe sunar ve elindeki malzemelerle hangi kokteylleri hazırlayabileceğini gösterir.\n\n" +
                "Tarif ve görsel verileri TheCocktailDB (thecocktaildb.com) kaynağından alınır. Tarif metinleri cihaz üzerinde Türkçeye çevrilir; çeviriler otomatik olduğu için bazı ifadeler hatalı olabilir."

            InfoDialog.PRIVACY -> "Gizlilik" to
                "Uygulama kişisel veri toplamaz ve hesap gerektirmez.\n\n" +
                "Favorilerin ve seçtiğin malzemeler yalnızca cihazında saklanır. İnternet bağlantısı yalnızca kokteyl verilerini, görselleri ve çeviri modelini indirmek için kullanılır."
        }
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = { TextButton(onClick = { dialog = null }) { Text("Tamam") } },
            title = { Text(title) },
            text = { Text(body) }
        )
    }
}

private fun lastSyncText(ms: Long): String =
    if (ms <= 0L) "Henüz güncelleme yapılmadı."
    else "Son güncelleme: " + SimpleDateFormat("dd.MM.yyyy HH:mm", TextUtils.TR).format(Date(ms))

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun ThemeRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}

@Composable
private fun InfoLine(label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
    ) { Text(label) }
}
