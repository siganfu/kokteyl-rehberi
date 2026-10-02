package com.kokteyl.rehberi.data.mapping

/**
 * Malzeme kataloğu (İngilizce anahtar -> Türkçe ad, kategori, grup).
 * Format: key|Türkçe|kategori|grup|bayraklar|karşılayanlar|takma adlar
 * Yeni malzeme eklemek için buraya bir satır eklemeniz yeterlidir.
 */
object IngredientData {
    val TSV = """
# ---------- ALKOLLÜ: VOTKA ----------
vodka|Votka|A|Votka|x||absolut vodka;smirnoff vodka;vodka (plain)
citrus vodka|Limonlu Votka|A|Votka|||absolut citron;lemon vodka
vanilla vodka|Vanilyalı Votka|A|Votka|||absolut vanilia
# ---------- CİN ----------
gin|Cin|A|Cin|x||london dry gin;dry gin;gordon's gin;tanqueray
sloe gin|Çakal Eriği Likörü (Sloe Gin)|A|Likör|||
# ---------- ROM ----------
rum|Rom|A|Rom|xh||
white rum|Beyaz Rom|A|Rom|||light rum;silver rum;bacardi;havana club;white rum (light)
dark rum|Koyu Rom|A|Rom|||black rum;jamaican rum
gold rum|Altın Rom|A|Rom|||golden rum;amber rum
spiced rum|Baharatlı Rom|A|Rom|||captain morgan
coconut rum|Hindistan Cevizli Rom (Malibu)|A|Rom|||malibu;malibu rum
overproof rum|Yüksek Alkollü Rom|A|Rom|||151 proof rum;151 rum;bacardi 151;overproof
cachaca|Cachaça|A|Rom|||cachaça
# ---------- TEKİLA ----------
tequila|Tekila|A|Tekila|x||silver tequila;blanco tequila;tequila blanco;gold tequila;reposado tequila
mezcal|Mezcal|A|Tekila|||
# ---------- VİSKİ ----------
whiskey|Viski|A|Viski|x||whisky;blended whiskey;blended whisky;canadian whisky
bourbon|Burbon Viski|A|Viski|||bourbon whiskey;jack daniels;jim beam;maker's mark
scotch|İskoç Viskisi (Scotch)|A|Viski|||scotch whisky;blended scotch;single malt
rye whiskey|Çavdar Viskisi (Rye)|A|Viski|||rye;rye whisky
irish whiskey|İrlanda Viskisi|A|Viski|||irish whisky;jameson
# ---------- BRENDİ ----------
brandy|Brendi|A|Brendi|x||
cognac|Konyak|A|Brendi|||
apple brandy|Elma Brendisi (Calvados)|A|Brendi|||calvados;applejack
pisco|Pisco|A|Brendi|||
# ---------- LİKÖR ----------
triple sec|Triple Sec|A|Likör|||cointreau;orange curacao;curacao;grand marnier;orange liqueur
blue curacao|Mavi Curaçao|A|Likör|||blue curaçao
amaretto|Amaretto|A|Likör|||disaronno
coffee liqueur|Kahve Likörü (Kahlúa)|A|Likör|||kahlua;kahlúa;tia maria
irish cream|İrlanda Kreması (Baileys)|A|Likör|||baileys irish cream;baileys;bailey's irish cream
creme de menthe|Nane Likörü|A|Likör|||green creme de menthe;white creme de menthe;crème de menthe;peppermint schnapps
creme de cacao|Kakao Likörü|A|Likör|||dark creme de cacao;white creme de cacao;crème de cacao
creme de cassis|Frenk Üzümü Likörü (Cassis)|A|Likör|||crème de cassis;cassis
creme de banana|Muz Likörü|A|Likör|||crème de banane;banana liqueur
creme de violette|Menekşe Likörü|A|Likör|||
raspberry liqueur|Ahududu Likörü (Chambord)|A|Likör|||chambord;chambord raspberry liqueur
melon liqueur|Kavun Likörü (Midori)|A|Likör|||midori;midori melon liqueur
peach schnapps|Şeftali Likörü (Schnapps)|A|Likör|||peach liqueur;peachtree schnapps
passion fruit liqueur|Passion Fruit Likörü|A|Likör|||passoa
sambuca|Sambuca|A|Likör|||
galliano|Galliano|A|Likör|||
drambuie|Drambuie|A|Likör|||
frangelico|Frangelico (Fındık Likörü)|A|Likör|||hazelnut liqueur
southern comfort|Southern Comfort|A|Likör|||
jagermeister|Jägermeister|A|Likör|||jägermeister
chartreuse|Chartreuse|A|Likör|||green chartreuse;yellow chartreuse
benedictine|Bénédictine|A|Likör|||bénédictine
maraschino liqueur|Maraschino Likörü|A|Likör|||luxardo;maraschino
limoncello|Limoncello|A|Likör|||
elderflower liqueur|Mürver Çiçeği Likörü|A|Likör|||st germain;st-germain;st. germain
cherry liqueur|Vişne Likörü|A|Likör|||cherry brandy;cherry heering;kirsch
apricot liqueur|Kayısı Likörü|A|Likör|||apricot brandy
strawberry liqueur|Çilek Likörü|A|Likör|||
absinthe|Absinthe|A|Likör|||
pastis|Pastis|A|Likör|||pernod;ricard
ouzo|Uzo|A|Likör|||
raki|Rakı|A|Likör|||rakı
campari|Campari|A|Likör|||
aperol|Aperol|A|Likör|||
fernet|Fernet|A|Likör|||fernet-branca;fernet branca
# ---------- VERMUT ----------
vermouth|Vermut|A|Vermut|xh||
sweet vermouth|Tatlı Vermut (Rosso)|A|Vermut|||red vermouth;rosso vermouth
dry vermouth|Kuru Vermut|A|Vermut|||white vermouth;french vermouth
lillet|Lillet Blanc|A|Vermut|||lillet blanc
# ---------- ŞAMPANYA / ŞARAP / BİRA ----------
champagne|Şampanya|A|Şampanya|||
prosecco|Prosecco|A|Şampanya|||
sparkling wine|Köpüklü Şarap|A|Şampanya|||cava;spumante
red wine|Kırmızı Şarap|A|Şarap|||
white wine|Beyaz Şarap|A|Şarap|||
rose wine|Roze Şarap|A|Şarap|||rosé;rose
port|Port Şarabı|A|Şarap|||port wine;ruby port
sherry|Şeri|A|Şarap|||dry sherry
beer|Bira|A|Bira|||lager;pilsner;ale
stout|Stout Bira (Guinness)|A|Bira|||guinness;guinness stout
# ---------- BİTTER (alkol sayılmaz) ----------
angostura bitters|Angostura Bitter|A|Bitter|n||angostura;angostura bitter
orange bitters|Portakal Bitter|A|Bitter|n||
peychauds bitters|Peychaud's
