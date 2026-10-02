package com.kokteyl.rehberi

import com.kokteyl.rehberi.data.local.IngredientEntity
import com.kokteyl.rehberi.data.local.LinkRow
import com.kokteyl.rehberi.domain.MatchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchEngineTest {

    private fun ing(id: String, group: String? = null, generic: Boolean = false) = IngredientEntity(
        id = id, name = id, turkishName = id, category = "Alkollü", ingredientGroup = group,
        alcoholic = group != null, garnish = false, generic = generic, hidden = false, translated = true
    )

    private val ingredients = listOf(
        ing("vodka", "Votka"), ing("triple sec", "Likör"), ing("lime juice"), ing("cranberry juice"),
        ing("salt"), ing("rum", "Rom", generic = true), ing("white rum", "Rom"), ing("lime")
    ).associateBy { it.id }

    private val links = listOf(
        // Cosmopolitan: 4 zorunlu
        LinkRow("cosmo", "vodka", false), LinkRow("cosmo", "triple sec", false),
        LinkRow("cosmo", "lime juice", false), LinkRow("cosmo", "cranberry juice", false),
        // Margarita benzeri: tuz garnitür (opsiyonel)
        LinkRow("marg", "triple sec", false), LinkRow("marg", "lime juice", false), LinkRow("marg", "salt", true),
        // Genel "rom" isteyen tarif
        LinkRow("rumdrink", "rum", false)
    )

    @Test fun optionalIngredientsAreNotRequired() {
        val r = MatchEngine.compute(links, setOf("triple sec", "lime juice"), ingredients)
        val marg = r.first { it.cocktailId == "marg" }
        assertEquals(2, marg.required)
        assertEquals(0, marg.missing)
        assertEquals(100, marg.percent)
    }

    @Test fun missingIngredientsAreReported() {
        val r = MatchEngine.compute(links, setOf("vodka", "triple sec", "lime juice"), ingredients)
        val cosmo = r.first { it.cocktailId == "cosmo" }
        assertEquals(4, cosmo.required)
        assertEquals(3, cosmo.have)
        assertEquals(listOf("cranberry juice"), cosmo.missingIds)
        assertEquals(75, cosmo.percent)
    }

    @Test fun sortedByFewestMissingThenPercent() {
        val r = MatchEngine.compute(links, setOf("vodka", "triple sec", "lime juice"), ingredients)
        assertEquals("marg", r.first().cocktailId) // 0 eksik
        assertEquals("cosmo", r[1].cocktailId)     // 1 eksik
    }

    @Test fun genericIngredientAcceptsSpecificOne() {
        val r = MatchEngine.compute(links, setOf("white rum"), ingredients)
        assertTrue(r.any { it.cocktailId == "rumdrink" && it.missing == 0 })
    }

    @Test fun noSelectionOrNoOverlapGivesNothing() {
        assertTrue(MatchEngine.compute(links, emptySet(), ingredients).isEmpty())
        assertTrue(MatchEngine.compute(links, setOf("salt"), ingredients).isEmpty())
    }

    @Test fun substituteRuleLimeSatisfiesLimeJuice() {
        val r = MatchEngine.compute(links, setOf("triple sec", "lime"), ingredients)
        assertEquals(0, r.first { it.cocktailId == "marg" }.missing)
    }
}
