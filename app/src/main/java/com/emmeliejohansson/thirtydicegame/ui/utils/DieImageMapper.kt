package com.emmeliejohansson.thirtydicegame.ui.utils

import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.models.DieColor

/**
 * Maps a die’s value and color to the corresponding drawable resource ID.
 * This helps the UI display the correct die image based on game state.
 */
object DieImageMapper {

    /**
     * Returns the drawable resource ID for the given die value and color.
     *
     * @param value The face value of the die (1-6).
     * @param color The color variant of the die.
     * @return The drawable resource ID to use for this die.
     * @throws IllegalArgumentException if the die value is not between 1 and 6.
     */
    fun getDieImageRes(value: Int, color: DieColor): Int {
        return when (color) {
            DieColor.RED -> getRedDieRes(value)
            DieColor.GRAY -> getGrayDieRes(value)
            DieColor.WHITE -> getWhiteDieRes(value)
        }
    }

    // Helper functions to map die value to drawable resource per color variant

    private fun getRedDieRes(value: Int) = when (value) {
        1 -> R.drawable.red_die_1
        2 -> R.drawable.red_die_2
        3 -> R.drawable.red_die_3
        4 -> R.drawable.red_die_4
        5 -> R.drawable.red_die_5
        6 -> R.drawable.red_die_6
        else -> throw IllegalArgumentException("Invalid die value: $value")
    }

    private fun getGrayDieRes(value: Int) = when (value) {
        1 -> R.drawable.gray_die_1
        2 -> R.drawable.gray_die_2
        3 -> R.drawable.gray_die_3
        4 -> R.drawable.gray_die_4
        5 -> R.drawable.gray_die_5
        6 -> R.drawable.gray_die_6
        else -> throw IllegalArgumentException("Invalid die value: $value")
    }

    private fun getWhiteDieRes(value: Int) = when (value) {
        1 -> R.drawable.die_1
        2 -> R.drawable.die_2
        3 -> R.drawable.die_3
        4 -> R.drawable.die_4
        5 -> R.drawable.die_5
        6 -> R.drawable.die_6
        else -> throw IllegalArgumentException("Invalid die value: $value")
    }
}
