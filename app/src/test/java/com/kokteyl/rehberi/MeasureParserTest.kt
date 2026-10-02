package com.kokteyl.rehberi

import com.kokteyl.rehberi.data.mapping.MeasureParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MeasureParserTest {

    @Test fun ounceIsConvertedToMl() {
        assertEquals("45 ml", MeasureParser.parse("1 1/2 oz").display)
        assertEquals(45.0, MeasureParser.parse("1 1/2 oz").ml!!, 0.001)
        assertEquals("15 ml", MeasureParser.parse("1/2 oz").display)
        assertEquals("25 ml", MeasureParser.parse("3/4 oz").display)
        assertEquals("60 ml", MeasureParser.parse("2 oz").display)
    }

    @Test fun centilitreAndMlAreExact() {
        assertEquals("20 ml", MeasureParser.parse("2 cl").display)
        assertEquals("50 ml", MeasureParser.parse("50 ml").display)
        assertEquals(50.0, MeasureParser.parse("5 cl").ml!!, 0.001)
    }

    @Test fun textUnitsAreTurkish() {
        assertEquals("1 dash", MeasureParser.parse("1 dash").display)
        assertEquals("1–2 dash", MeasureParser.parse("1-2 dashes").display)
        assertEquals("2 çay kaşığı", MeasureParser.parse("2 tsp").display)
        assertEquals("10 yaprak", MeasureParser.parse("10 leaves").display)
        assertEquals("1 dilim", MeasureParser.parse("1 slice").display)
        assertTrue(MeasureParser.parse("1 slice").garnishUnit)
    }

    @Test fun phrasesAndEmpty() {
        assertEquals("Üstünü tamamla", MeasureParser.parse("Top up with").display)
        assertEquals("1/2 adet (suyu)", MeasureParser.parse("Juice of 1/2").display)
        assertEquals("", MeasureParser.parse(null).display)
        assertNull(MeasureParser.parse("").ml)
    }
}
