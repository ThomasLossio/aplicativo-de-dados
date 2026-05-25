package br.com.thomas.dados

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceEngineTest {
    @Test
    fun initialStateStartsWithOneStandardDie() {
        val state = DiceEngine.initialState()

        assertEquals(1, state.dice.size)
        assertEquals(DiceRange(1, 6), state.settings.defaultRange)
        assertEquals(FaceColorMode.Black, state.settings.faceColorMode)
        assertEquals(DiceRange(1, 6), state.dice.single().range)
        assertEquals(1, state.dice.single().value)
    }

    @Test
    fun diceCountIsClampedBetweenOneAndForty() {
        val state = DiceEngine.initialState()

        val tooMany = DiceEngine.setDiceCount(state, 99)
        val tooFew = DiceEngine.setDiceCount(state, 0)

        assertEquals(40, tooMany.dice.size)
        assertEquals(1, tooFew.dice.size)
    }

    @Test
    fun diceCountPresetsIncludeQuickChoicesUpToForty() {
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 10, 20, 30, 40), DiceEngine.diceCountPresets)
    }

    @Test
    fun newDiceUseCurrentDefaultRange() {
        val state = DiceEngine.initialState()
        val withDefault = DiceEngine.setDefaultRange(state, DiceRange(1, 20))

        val withThreeDice = DiceEngine.setDiceCount(withDefault, 3)

        assertEquals(listOf(DiceRange(1, 20), DiceRange(1, 20), DiceRange(1, 20)), withThreeDice.dice.map { it.range })
    }

    @Test
    fun changingDefaultRangeKeepsIndividualOverrides() {
        val state = DiceEngine.setDiceCount(DiceEngine.initialState(), 2)
        val customized = DiceEngine.setDieRange(state, dieId = 2, range = DiceRange(1, 12))

        val updated = DiceEngine.setDefaultRange(customized, DiceRange(1, 20))

        assertEquals(DiceRange(1, 20), updated.dice[0].range)
        assertEquals(DiceRange(1, 12), updated.dice[1].range)
        assertTrue(updated.dice[1].hasCustomRange)
    }

    @Test
    fun rollUsesEachDieRange() {
        val state = DiceEngine.setDiceCount(DiceEngine.initialState(), 2)
        val customized = DiceEngine.setDieRange(state, dieId = 2, range = DiceRange(10, 10))

        val rolled = DiceEngine.roll(customized, FixedDiceRandom(6))

        assertEquals(6, rolled.dice[0].value)
        assertEquals(10, rolled.dice[1].value)
    }

    @Test
    fun totalSumsCurrentDiceValues() {
        val state = DiceEngine.setDiceCount(DiceEngine.initialState(), 3)
        val rolled = state.copy(
            dice = listOf(
                state.dice[0].copy(value = 2),
                state.dice[1].copy(value = 4),
                state.dice[2].copy(value = 6),
            ),
        )

        assertEquals(12, DiceEngine.total(rolled))
    }

    @Test
    fun classicPipsUseExpectedPatternsForOneThroughSix() {
        assertEquals(listOf(PipPosition.Center), DiceFaceRenderer.render(1, DisplayMode.Automatic).pips)
        assertEquals(listOf(PipPosition.TopLeft, PipPosition.BottomRight), DiceFaceRenderer.render(2, DisplayMode.Automatic).pips)
        assertEquals(listOf(PipPosition.TopLeft, PipPosition.Center, PipPosition.BottomRight), DiceFaceRenderer.render(3, DisplayMode.Automatic).pips)
        assertEquals(listOf(PipPosition.TopLeft, PipPosition.TopRight, PipPosition.BottomLeft, PipPosition.BottomRight), DiceFaceRenderer.render(4, DisplayMode.Automatic).pips)
        assertEquals(listOf(PipPosition.TopLeft, PipPosition.TopRight, PipPosition.Center, PipPosition.BottomLeft, PipPosition.BottomRight), DiceFaceRenderer.render(5, DisplayMode.Automatic).pips)
        assertEquals(listOf(PipPosition.TopLeft, PipPosition.MiddleLeft, PipPosition.BottomLeft, PipPosition.TopRight, PipPosition.MiddleRight, PipPosition.BottomRight), DiceFaceRenderer.render(6, DisplayMode.Automatic).pips)
    }

    @Test
    fun automaticDisplayUsesNumbersAboveSix() {
        val rendered = DiceFaceRenderer.render(7, DisplayMode.Automatic)

        assertEquals("7", rendered.number)
        assertTrue(rendered.pips.isEmpty())
    }

    @Test
    fun valuesAreLimitedFromOneToOneHundred() {
        assertFalse(DiceRange.isValid(0, 6))
        assertFalse(DiceRange.isValid(1, 101))
        assertFalse(DiceRange.isValid(8, 6))
        assertTrue(DiceRange.isValid(1, 100))
    }

    @Test
    fun standardRangePresetsIncludeCommonDiceTypes() {
        assertEquals(
            listOf(
                DiceRange(1, 4),
                DiceRange(1, 6),
                DiceRange(1, 8),
                DiceRange(1, 10),
                DiceRange(1, 12),
                DiceRange(1, 20),
                DiceRange(1, 100),
            ),
            DiceRange.standardPresets,
        )
    }

    @Test
    fun boardLayoutUsesLargeFullScreenRegionsForSmallDiceCounts() {
        assertEquals(DiceBoardLayout.Single, DiceBoardLayout.forDiceCount(1))
        assertEquals(DiceBoardLayout.Split, DiceBoardLayout.forDiceCount(2))
        assertEquals(DiceBoardLayout.Split, DiceBoardLayout.forDiceCount(3))
        assertEquals(DiceBoardLayout.TwoByTwo, DiceBoardLayout.forDiceCount(4))
        assertEquals(DiceBoardLayout.Grid, DiceBoardLayout.forDiceCount(5))
    }

    @Test
    fun rollSpeedsUseLongReadableDurations() {
        assertEquals(3_000L, RollSpeed.Slow.durationMillis)
        assertEquals(2_000L, RollSpeed.Normal.durationMillis)
        assertEquals(1_000L, RollSpeed.Fast.durationMillis)
    }

    @Test
    fun rollSoundsCoverTheFullAnimationDuration() {
        assertEquals(16, RollSpeed.Slow.soundTapCount)
        assertEquals(11, RollSpeed.Normal.soundTapCount)
        assertEquals(6, RollSpeed.Fast.soundTapCount)
        assertEquals(200L, RollSpeed.Slow.soundIntervalMillis)
        assertEquals(200L, RollSpeed.Normal.soundIntervalMillis)
        assertEquals(200L, RollSpeed.Fast.soundIntervalMillis)
    }
}

private class FixedDiceRandom(private val value: Int) : DiceRandom {
    override fun nextInt(min: Int, max: Int): Int = value.coerceIn(min, max)
}
