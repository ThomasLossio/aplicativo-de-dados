package br.com.thomas.dados

data class DiceRange(
    val min: Int,
    val max: Int,
) {
    init {
        require(isValid(min, max)) { "Dice range must be between 1 and 100 and min must be <= max." }
    }

    companion object {
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

enum class RollSpeed(
    val steps: Int,
    val delayMillis: Long,
) {
    Slow(18, 90L),
    Normal(12, 55L),
    Fast(7, 32L),
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
