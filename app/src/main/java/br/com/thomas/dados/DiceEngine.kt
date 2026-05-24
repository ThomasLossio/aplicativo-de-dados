package br.com.thomas.dados

import kotlin.random.Random

object DiceEngine {
    const val minDiceCount = 1
    const val maxDiceCount = 40

    fun initialState(): DiceState {
        val settings = DiceSettings()
        return DiceState(
            dice = listOf(Die(id = 1, range = settings.defaultRange, value = 1)),
            settings = settings,
        )
    }

    fun setDiceCount(state: DiceState, count: Int): DiceState {
        val desiredCount = count.coerceIn(minDiceCount, maxDiceCount)
        val current = state.dice

        val nextDice = when {
            desiredCount == current.size -> current
            desiredCount < current.size -> current.take(desiredCount)
            else -> current + ((current.size + 1)..desiredCount).map { id ->
                Die(
                    id = id,
                    range = state.settings.defaultRange,
                    value = state.settings.defaultRange.min,
                )
            }
        }

        return state.copy(dice = nextDice)
    }

    fun setDefaultRange(state: DiceState, range: DiceRange): DiceState {
        val nextDice = state.dice.map { die ->
            if (die.hasCustomRange) {
                die.copy(value = die.value.coerceIn(die.range.min, die.range.max))
            } else {
                die.copy(range = range, value = die.value.coerceIn(range.min, range.max))
            }
        }

        return state.copy(
            dice = nextDice,
            settings = state.settings.copy(defaultRange = range),
        )
    }

    fun setDieRange(state: DiceState, dieId: Int, range: DiceRange): DiceState =
        state.copy(
            dice = state.dice.map { die ->
                if (die.id == dieId) {
                    die.copy(
                        range = range,
                        value = die.value.coerceIn(range.min, range.max),
                        hasCustomRange = true,
                    )
                } else {
                    die
                }
            },
        )

    fun clearDieRange(state: DiceState, dieId: Int): DiceState =
        state.copy(
            dice = state.dice.map { die ->
                if (die.id == dieId) {
                    die.copy(
                        range = state.settings.defaultRange,
                        value = die.value.coerceIn(state.settings.defaultRange.min, state.settings.defaultRange.max),
                        hasCustomRange = false,
                    )
                } else {
                    die
                }
            },
        )

    fun updateSettings(state: DiceState, transform: (DiceSettings) -> DiceSettings): DiceState =
        state.copy(settings = transform(state.settings))

    fun roll(state: DiceState, random: DiceRandom = KotlinDiceRandom): DiceState =
        state.copy(
            dice = state.dice.map { die ->
                die.copy(value = random.nextInt(die.range.min, die.range.max))
            },
        )

    fun total(state: DiceState): Int = state.dice.sumOf { it.value }
}

object KotlinDiceRandom : DiceRandom {
    override fun nextInt(min: Int, max: Int): Int = Random.nextInt(from = min, until = max + 1)
}

object DiceFaceRenderer {
    fun render(value: Int, mode: DisplayMode): FaceRender {
        if (mode == DisplayMode.NumbersOnly || value !in 1..6) {
            return FaceRender(number = value.toString())
        }

        return FaceRender(
            pips = when (value) {
                1 -> listOf(PipPosition.Center)
                2 -> listOf(PipPosition.TopLeft, PipPosition.BottomRight)
                3 -> listOf(PipPosition.TopLeft, PipPosition.Center, PipPosition.BottomRight)
                4 -> listOf(PipPosition.TopLeft, PipPosition.TopRight, PipPosition.BottomLeft, PipPosition.BottomRight)
                5 -> listOf(PipPosition.TopLeft, PipPosition.TopRight, PipPosition.Center, PipPosition.BottomLeft, PipPosition.BottomRight)
                6 -> listOf(PipPosition.TopLeft, PipPosition.MiddleLeft, PipPosition.BottomLeft, PipPosition.TopRight, PipPosition.MiddleRight, PipPosition.BottomRight)
                else -> emptyList()
            },
        )
    }
}
