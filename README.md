# Kokteyl Rehberi (Android)

Kotlin · Jetpack Compose · Material 3 · MVVM · Repository · Room · Retrofit · Coroutines · Coil · Navigation Compose
Tamamen Türkçe, koyu tema ağırlıklı, internetsiz çalışabilen kokteyl tarifi uygulaması.

> ## ⚠ Durum: DERLENMEDİ
> Bu proje, Android SDK / Gradle / internet erişimi olmayan bir ortamda yazıldı. **Gradle build hiç çalıştırılmadı,
> uygulama hiç açılmadı, birim testleri hiç koşturulmadı.** Yapılabilen kontroller: parantez dengesi, proje içi
> import'ların varlığı, malzeme kataloğu ve seed JSON tutarlılığı (Python ile). İlk derlemede küçük hatalar
> (import, tip çıkarımı, sürüm uyumsuzluğu) çıkması olasıdır; aşağıda "İlk derleme" bölümüne bakın.

## 1. Proje klasör yapısı

```
KokteylRehberi/
├── settings.gradle.kts / build.gradle.kts / gradle.properties
├── gradle/wrapper/gradle-wrapper.properties      (Gradle 8.10.2)
├── tools/gen_seed.py                              (yerel Türkçe tarifleri üreten betik)
└── app/
    ├── build.gradle.kts                           (bağımlılıklar, API anahtarı)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── assets/cocktails_seed.json         (38 elle yazılmış Türkçe tarif: ilk açılış / offline)
        │   ├── res/                               (tema, adaptive ikon: kokteyl kadehi)
        │   └── java/com/kokteyl/rehberi/
        │       ├── KokteylApp.kt, AppContainer.kt, MainActivity.kt
        │       ├── data/
        │       │   ├── local/       Entities, Daos, AppDatabase          (Room)
        │       │   ├── remote/      CocktailApi (Retrofit), NetworkModule
        │       │   ├── mapping/     IngredientData/Catalog (EN->TR malzeme sözlüğü), MeasureParser (ölçü çevirici),
        │       │   │                Translations (bardak/kategori/teknik), TagClassifier (filtre etiketleri), CocktailBuilder
        │       │   ├── repository/  CocktailRepository (seed, API senkronu, çeviri, akışlar)
        │       │   └── settings/    SettingsStore (tema, son güncelleme)
        │       ├── domain/          Models + MatchEngine (malzeme eşleştirme algoritması)
        │       ├── translate/       TextTranslator (ML Kit, cihaz üstü EN->TR)
        │       └── ui/              theme, components, nav, home, cocktails, detail, ingredients, results, favorites, settings
        └── test/                    MeasureParserTest, MatchEngineTest, CatalogTest
```

## 2. Kullanılan API'ler

| Servis | Kullanım |
|---|---|
| **TheCocktailDB** `https://www.thecocktaildb.com/api/json/v1/{KEY}/search.php?f={harf}` | a–z ve 0–9 harfleriyle tüm kokteyller çekilir (isim, görsel, kategori, alkol durumu, bardak, malzeme + ölçü, İngilizce tarif). Görseller `…/medium` (kart) ve `…/large` (detay) olarak istenir. |
| **Google ML Kit Translate** (cihaz üstü) | API'den gelen İngilizce tarif metinlerini Türkçeye çevirir. İlk kullanımda ~30 MB model indirilir, sonra internetsiz çalışır. |

**⚠ TheCocktailDB kullanım şartı (önemli):** `1` anahtarı yalnızca **geliştirme/eğitim** içindir. Uygulamayı
Play Store'da yayınlamak için TheCocktailDB'den ücretli üretim (Premium/Patreon) anahtarı almanız gerekir; anahtarı
`app/build.gradle.kts` içindeki `COCKTAILDB_API_KEY` alanına yazın. Ücretsiz anahtarda listelemeler sınırlıdır
(sonuç sayısı sınırlanabilir); bu yüzden uygulamada yerel seed veri vardır. Görsellerin telif/atıf koşullarını da
yayın öncesi kontrol edin. Şartlar değişebilir: https://www.thecocktaildb.com/api.php

## 3. Veritabanı yapısı (Room, `kokteyl.db`)

- **cocktails**: `id (PK)`, `name`, `turkishName`, `nameKey (unique)`, `imageUrl`, `category`, `alcoholic`, `mainSpirit`, `glass`,
  `method`, `garnish`, `instructions` (Türkçe adımlar, `\n` ile), `instructionsEn`, `shortInfo`, `tags` (filtre etiketleri),
  `searchText` (aksansız arama metni), `curated`, `popularRank`, `favorite`
- **ingredients**: `id (PK, İngilizce anahtar)`, `name`, `turkishName`, `category`, `ingredientGroup`, `alcoholic`, `garnish`,
  `generic`, `hidden`, `translated`
- **cocktail_ingredients**: `id`, `cocktailId (FK→cocktails, CASCADE)`, `ingredientId (FK→ingredients)`, `position`,
  `amount` (ekranda gösterilen "50 ml"), `amountMl`, `unit`, `optional`
- **user_ingredients**: `ingredientId (PK)` — "Elimdeki malzemeler" seçimi

Şema değişirse `fallbackToDestructiveMigration()` veriyi sıfırlar (kokteyller yeniden yüklenir; favoriler gider).
Yayın öncesi gerçek `Migration` yazın.

## 4. Nasıl build alınır

1. **Android Studio Ladybug (2024.2) veya daha yenisi** ile klasörü açın (JDK 17 ile gelir). İlk açılışta Gradle
   senkronu bağımlılıkları indirir (internet gerekir).
2. **Run ▶** ile emülatör/telefonda çalıştırın veya menüden **Build > Build APK(s)**.
3. Komut satırı: bu pakette `gradlew` betiği/jar'ı yok (indirilemedi). Bir kez `gradle wrapper --gradle-version 8.10.2`
   çalıştırın (ya da Android Studio'nun Gradle'ını kullanın), sonra:
   ```
   ./gradlew assembleDebug        # test APK'sı
   ./gradlew test                 # birim testleri
   ./gradlew assembleRelease      # imzasız release APK (yayın için keystore ile imzalayın)
   ```
4. Sürüm uyumsuzluğu çıkarsa: AGP 8.7.2 · Kotlin 2.0.21 · KSP 2.0.21-1.0.28 · Compose BOM 2024.10.01 · Room 2.6.1.
   Android Studio "Upgrade" önerirse kabul edebilirsiniz (KSP sürümü Kotlin sürümüyle eşleşmeli).

## 5. APK nerede oluşur

- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 6. Sonradan yeni kokteyl / malzeme nasıl eklenir

- **Elle Türkçe tarif:** `tools/gen_seed.py` içindeki listeye `c("Ad", kategori, bardak, yöntem, garnitür, etiketler, adımlar, malzemeler)`
  satırı ekleyip `python3 tools/gen_seed.py` çalıştırın; `assets/cocktails_seed.json` yeniden üretilir. Sonra
  `CocktailRepository.SEED_VERSION` değerini 1 artırın (mevcut kullanıcılarda yeniden yüklenir; favoriler korunur).
  Elle yazılan tarifler (`curated`) API güncellemesiyle ezilmez; API yalnızca eksik görseli tamamlar.
- **Yeni malzeme:** `data/mapping/IngredientData.kt` içine `anahtar|Türkçe|kategori|grup|bayraklar|karşılayanlar|takma adlar`
  satırı ekleyin (kategori kodları dosyanın başında). Yeni malzeme seçim ekranında otomatik görünür.
- **API'den:** Ayarlar > "Verileri Güncelle" yeni kokteylleri ekler/günceller; mevcut veriyi, favorileri ve seçimleri bozmaz.

## Tasarım kararları ve bilinen sınırlar (dürüst liste)

- **Türkçe çeviri:** malzeme adları, ölçüler, bardak, kategori ve teknikler yerel sözlükle çevrilir (kalite yüksek).
  API tarif metinleri ise ML Kit ile **otomatik** çevrilir (kalite orta; bazı cümleler hatalı olabilir). Bir tarif ilk
  açıldığında çevrilir ve saklanır; Ayarlar'dan hepsi toplu çevrilebilir. Çeviri modeli inmemişse ve internet yoksa,
  talimattaki "İngilizce gösterme" kuralı gereği tarif metni yerine bilgi mesajı gösterilir. Yerel 38 tarif her zaman Türkçedir.
- **Katalogda olmayan malzemeler** (API'de yüzlerce nadir malzeme var) çevrilene kadar seçim ekranında gizlenir;
  o malzemeyi gerektiren tarifler "eksik" sayılır. Kapsamı artırmak için kataloğa ekleyin.
- **Eşleştirme kuralları:** garnitür (kiraz, kabuk, nane dalı, tuz kenarı…), buz ve su zorunlu sayılmaz.
  Bu yüzden talimattaki örnekten farklı olarak Margarita'da "Tuz" eksikliği %75 yapmaz (tuz garnitür kabul edildi;
  talimattaki "garnitürü zorunlu sayma" kuralı esas alındı). Genel malzeme ("Viski") herhangi bir türüyle (Burbon…) karşılanır;
  "limon" seçili ise "limon suyu" karşılanmış sayılır. Hiçbir malzemesi eşleşmeyen kokteyller listelenmez.
- **Ölçü dönüşümü:** 1 oz = 30 ml (bar standardı); ons'tan çevrilen değerler 5 ml'ye yuvarlanır (3/4 oz → 25 ml).
  cl/ml birebir korunur. Kaşık, dash, yaprak, dilim vb. Türkçe birimlere çevrilir.
- **Filtreler** (Klasik, Tiki, Tropikal, Sour, Frozen, Highball, Martini) malzeme/bardak/isim kurallarıyla tahmin edilir; kusursuz değildir.
- **Alkolsüz:** tarifler aynı detay seviyesindedir; "Alkolsüz" filtresi ve ana sayfada kısayol vardır.
- **Gelecek özellikler** için mimari hazır: malzeme başına `amountMl`/`unit` saklanır (maliyet, ölçek), ayrı `ingredients`
  tablosu (stok, alışveriş listesi), `curated` bayrağı (kullanıcı tarifleri), hafif Repository/ViewModel katmanı.
- Depolama/Hilt gibi ek kütüphane kullanılmadı; bağımlılık enjeksiyonu elle (`AppContainer`).

## 7. APK'yı otomatik aldırmanın en kolay yolu (önerilen)

Bu projeye `.github/workflows/build.yml` eklendi. Android Studio kurmadan, GitHub üzerinden otomatik derleme yaptırabilirsiniz:

1. GitHub'da boş bir depo (repository) oluşturun (örn. `kokteyl-rehberi`), herkese açık ya da özel fark etmez.
2. Bu klasörün tamamını o depoya yükleyin:
   ```
   cd KokteylRehberi
   git init
   git add .
   git commit -m "İlk sürüm"
   git branch -M main
   git remote add origin https://github.com/KULLANICI_ADINIZ/kokteyl-rehberi.git
   git push -u origin main
   ```
3. GitHub'da depo sayfasında **Actions** sekmesine girin. "Build APK" iş akışı otomatik başlar
   (başlamazsa "Run workflow" butonuna basın).
4. Derleme bitince (birkaç dakika sürer) çalıştırma sayfasının altındaki **Artifacts** bölümünden
   `KokteylRehberi-debug-apk` dosyasını indirin. İçinden çıkan `app-debug.apk` dosyasını telefonunuza
   kopyalayıp kurabilirsiniz (telefonda "Bilinmeyen kaynaklardan kuruluma izin ver" ayarını açmanız gerekebilir).

Bu yöntem gerçek bir Android SDK + Gradle ortamında, gerçek derleme yaparak APK üretir; benim bu sohbette
çalıştığım ortamda SDK ve internet erişimi olmadığı için APK'yı doğrudan burada üretemiyorum.

## 8. Alternatif: Android Studio ile (grafik arayüzle, GitHub'sız)

1. [Android Studio](https://developer.android.com/studio) kurun (ücretsiz).
2. "Open" ile `KokteylRehberi` klasörünü açın. İlk açılışta Gradle senkronizasyonu otomatik gerekli
   Gradle sürümünü indirir (internet gerekir).
3. Üstteki yeşil ▶ (Run) butonuna basın; bağlı telefon veya emülatörde uygulama açılır.
4. APK dosyası için: üst menüden **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
   Derleme bitince sağ altta çıkan bildirimden "locate" ile APK'nın bulunduğu klasörü açabilirsiniz
   (`app/build/outputs/apk/debug/app-debug.apk`).
