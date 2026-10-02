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
peychauds bitters|Peychaud's Bitter|A|Bitter|n||peychaud's bitters;peychaud bitters
bitters|Bitter|A|Bitter|n||
# ---------- MEYVE SULARI VE PÜRELER ----------
lemon juice|Limon Suyu|J|||lemon|fresh lemon juice;juice of lemon
lime juice|Lime Suyu|J|||lime|fresh lime juice;juice of lime
orange juice|Portakal Suyu|J|||orange|fresh orange juice;juice of orange
pineapple juice|Ananas Suyu|J||||
apple juice|Elma Suyu|J||||apple cider
grapefruit juice|Greyfurt Suyu|J|||grapefruit|fresh grapefruit juice
tomato juice|Domates Suyu|J||||
cranberry juice|Cranberry Suyu|J||||cranberry juice cocktail
pomegranate juice|Nar Suyu|J|||pomegranate|
grape juice|Üzüm Suyu|J||||
passion fruit juice|Passion Fruit Suyu|J|||passion fruit|
passion fruit puree|Passion Fruit Püresi|J||||passion fruit pulp
mango juice|Mango Suyu|J|||mango|
mango puree|Mango Püresi|J||||
peach puree|Şeftali Püresi|J|||peach|peach nectar;peach juice
strawberry puree|Çilek Püresi|J|||strawberry|
raspberry puree|Ahududu Püresi|J|||raspberry|
coconut water|Hindistan Cevizi Suyu|J||||
cherry juice|Kiraz Suyu|J||||
# ---------- ŞURUPLAR ----------
simple syrup|Şeker Şurubu|S||||sugar syrup;gomme syrup;syrup;sugar water
grenadine|Grenadin|S||||
passion fruit syrup|Passion Fruit Şurubu|S||||
vanilla syrup|Vanilya Şurubu|S||||
coconut syrup|Hindistan Cevizi Şurubu|S||||
cream of coconut|Hindistan Cevizi Kreması|S||||coconut cream;coco lopez;coco lopez cream of coconut
orgeat syrup|Orjeat Şurubu (Badem)|S||||orgeat
honey syrup|Bal Şurubu|S||||
honey|Bal|S||||
agave syrup|Agave Şurubu|S||||agave nectar
maple syrup|Akçaağaç Şurubu|S||||
ginger syrup|Zencefil Şurubu|S||||
raspberry syrup|Ahududu Şurubu|S||||
strawberry syrup|Çilek Şurubu|S||||
cinnamon syrup|Tarçın Şurubu|S||||
mint syrup|Nane Şurubu|S||||
falernum|Falernum|S||||
lime cordial|Lime Kordiyal|S||||lime juice cordial;rose's lime juice;roses lime juice
sweet and sour|Ekşi-Tatlı Miks|S||||sour mix;sweet and sour mix
elderflower cordial|Mürver Çiçeği Şurubu|S||||
chocolate syrup|Çikolata Sosu|S||||chocolate sauce
caramel sauce|Karamel Sosu|S||||caramel syrup
# ---------- GAZLI İÇECEKLER VE MİKSERLER ----------
soda water|Soda|M||||club soda;carbonated water;sparkling water;seltzer;soda
tonic water|Tonik|M||||tonic
cola|Kola|M||||coca-cola;coke;coca cola;pepsi
lemon-lime soda|Sprite / Gazoz|M||||sprite;7-up;7up;lemon lime soda;lemon soda
ginger ale|Ginger Ale|M||||
ginger beer|Ginger Beer|M||||
bitter lemon|Bitter Lemon|M||||
lemonade|Limonata|M||||
energy drink|Enerji İçeceği|M||||red bull
water|Su|M||b||cold water;hot water;boiling water;warm water
ice|Buz|M||b||ice cubes;crushed ice;cracked ice;ice cube;shaved ice
coffee|Kahve|M||||hot coffee;black coffee;cold brew
espresso|Espresso|M||||
tea|Çay|M||||black tea;iced tea;green tea
# ---------- MEYVE VE SEBZELER ----------
lemon|Limon|F||||
lime|Lime|F||||
orange|Portakal|F||||
grapefruit|Greyfurt|F||||
pineapple|Ananas|F||||
strawberry|Çilek|F||||strawberries
raspberry|Ahududu|F||||raspberries
blackberry|Böğürtlen|F||||blackberries
blueberry|Yaban Mersini|F||||blueberries
maraschino cherry|Kokteyl Kirazı|F||g||cherry;cherries;cocktail cherry
banana|Muz|F||||
apple|Elma|F||||
peach|Şeftali|F||||
watermelon|Karpuz|F||||
mango|Mango|F||||
kiwi|Kivi|F||||
cucumber|Salatalık|F||||
olive|Zeytin|F||g||green olive;olives;green olives;black olive
cocktail onion|Kokteyl Soğanı|F||g||pearl onion;onion
celery|Kereviz|F||g||celery stalk;celery stick
lemon peel|Limon Kabuğu|F||g||lemon twist;lemon zest;lemon rind
lime peel|Lime Kabuğu|F||g||lime twist;lime zest;lime rind
orange peel|Portakal Kabuğu|F||g||orange twist;orange zest;orange spiral;orange rind
coconut|Hindistan Cevizi|F||||coconut flakes;shredded coconut
passion fruit|Passion Fruit|F||||maracuya
pomegranate|Nar|F||||
pear|Armut|F||||
# ---------- OTLAR VE BAHARATLAR ----------
mint|Nane|H||||fresh mint;mint leaves;mint sprig;spearmint;peppermint leaves
basil|Fesleğen|H||||
rosemary|Biberiye|H||||
thyme|Kekik|H||||
ginger|Zencefil|H||||fresh ginger;ginger root
cinnamon|Tarçın|H||||cinnamon powder;ground cinnamon
cinnamon stick|Tarçın Çubuğu|H||g||
nutmeg|Muskat|H||g||grated nutmeg;ground nutmeg
cloves|Karanfil|H||||clove
star anise|Yıldız Anason|H||||
cardamom|Kakule|H||||
salt|Tuz|H||g||kosher salt;sea salt;rim salt
celery salt|Kereviz Tuzu|H||g||
black pepper|Karabiber|H||g||pepper;ground pepper;freshly ground pepper
cayenne pepper|Acı Biber (Cayenne)|H||||chili;chilli;chili powder;cayenne
tabasco sauce|Tabasco Sosu|H||||tabasco;hot sauce
worcestershire sauce|Worcestershire Sosu|H||||
horseradish|Yaban Turpu|H||||
vanilla extract|Vanilya Özütü|H||||vanilla;vanilla essence
# ---------- SÜT ÜRÜNLERİ VE YUMURTA ----------
milk|Süt|D||||whole milk
cream|Krema|D||||heavy cream;light cream;double cream;single cream;half-and-half;half and half
whipped cream|Krem Şanti|D||g||
egg white|Yumurta Akı|D|||egg|egg whites
egg yolk|Yumurta Sarısı|D||||
egg|Yumurta|D||||whole egg
ice cream|Dondurma|D||||vanilla ice cream
yogurt|Yoğurt|D||||
condensed milk|Yoğunlaştırılmış Süt|D||||sweetened condensed milk
coconut milk|Hindistan Cevizi Sütü|D||||
butter|Tereyağı|D||||
# ---------- DİĞER ----------
sugar|Şeker|O|||sugar cube;powdered sugar;brown sugar|granulated sugar;superfine sugar;caster sugar;white sugar
powdered sugar|Pudra Şekeri|O||||icing sugar;confectioners sugar
brown sugar|Esmer Şeker|O||||
sugar cube|Küp Şeker|O||||sugar cubes
chocolate|Çikolata|O||||dark chocolate;chocolate shavings
cocoa powder|Kakao|O||||cocoa;cacao
olive brine|Zeytin Suyu|O||||
"""
}
