package br.com.thomas.dados

data class DiceRange(
    val min: Int,
    val max: Int,
) {
    init {
        require(isValid(min, max)) { "Dice range must be between 1 and 100 and min must be <= max." }
    }

    companion object {
        val standardPresets = listOf(
            DiceRange(1, 4),
            DiceRange(1, 6),
            DiceRange(1, 8),
            DiceRange(1, 10),
            DiceRange(1, 12),
            DiceRange(1, 20),
            DiceRange(1, 100),
        )

        fun isValid(min: Int, max: Int): Boolean = min in 1..100 && max in 1..100 && min <= max
    }
}

data class Die(
    val id: Int,
    val range: DiceRange,
    val value: Int,
    val hasCustomRange: Boolean = false,
)

data class DiceSettings(
    val defaultRange: DiceRange = DiceRange(1, 6),
    val displayMode: DisplayMode = DisplayMode.Automatic,
    val colorMode: ColorMode = ColorMode.Multicolor,
    val faceColorMode: FaceColorMode = FaceColorMode.Black,
    val rollSpeed: RollSpeed = RollSpeed.Normal,
    val tapToRoll: Boolean = true,
    val shakeToRoll: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showTotal: Boolean = true,
)

data class DiceState(
    val dice: List<Die>,
    val settings: DiceSettings = DiceSettings(),
)

enum class DisplayMode {
    Automatic,
    NumbersOnly,
}

enum class ColorMode {
    Multicolor,
    Single,
}

enum class FaceColorMode {
    Black,
    White,
}

enum class DiceBoardLayout {
    Single,
    Split,
    TwoByTwo,
    Grid;

    companion object {
        fun forDiceCount(count: Int): DiceBoardLayout = when (count) {
            1 -> Single
            2, 3 -> Split
            4 -> TwoByTwo
            else -> Grid
        }
    }
}

enum class RollSpeed(
    val steps: Int,
    val delayMillis: Long,
) {
    Slow(30, 100L),
    Normal(20, 100L),
    Fast(10, 100L),
    ;

    val durationMillis: Long
        get() = steps * delayMillis

    val soundTapCount: Int
        get() = (durationMillis / soundIntervalMillis).toInt() + 1

    val soundIntervalMillis: Long
        get() = 200L
}

enum class PipPosition {
    TopLeft,
    TopCenter,
    TopRight,
    MiddleLeft,
    Center,
    MiddleRight,
    BottomLeft,
    BottomCenter,
    BottomRight,
}

data class FaceRender(
    val pips: List<PipPosition> = emptyList(),
    val number: String? = null,
)

interface DiceRandom {
    fun nextInt(min: Int, max: Int): Int
}
