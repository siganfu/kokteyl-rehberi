package com.kokteyl.rehberi

import com.kokteyl.rehberi.data.mapping.IngredientCatalog
import com.kokteyl.rehberi.data.mapping.TextUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogTest {

    @Test fun apiNamesAreResolvedToTurkish() {
        assertEquals("Beyaz Rom", IngredientCatalog.resolve("Light rum")?.tr)
        assertEquals("Limon Suyu", IngredientCatalog.resolve("Lemon Juice")?.tr)
        assertEquals("Şeker Şurubu", IngredientCatalog.resolve("Sugar syrup")?.tr)
        assertEquals("Votka", IngredientCatalog.resolve("Vodka")?.tr)
        assertNull(IngredientCatalog.resolve("Some Unknown Thing"))
    }

    @Test fun catalogIsConsistent() {
        val all = IngredientCatalog.all
        assertTrue(all.size > 150)
        assertEquals(all.size, all.map { it.key }.toSet().size)
        assertNotNull(IngredientCatalog.byKey("passion fruit syrup"))
        assertTrue(IngredientCatalog.byKey("ice")!!.basic)
        assertTrue(IngredientCatalog.byKey("maraschino cherry")!!.garnish)
    }

    @Test fun textUtils() {
        assertEquals("pinacolada", TextUtils.nameKey("Piña Colada"))
        assertEquals("istanbul seker", TextUtils.fold("İstanbul ŞEKER"))
        assertEquals("passion", TextUtils.sanitizeQuery("  Passion% "))
    }
}
