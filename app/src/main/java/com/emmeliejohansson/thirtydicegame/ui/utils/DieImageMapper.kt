package com.emmeliejohansson.thirtydicegame.ui.utils

import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.models.DieColor

object DieImageMapper {

    fun getDieImageRes(value: Int, color: DieColor): Int {
        return when (color) {
            DieColor.RED -> getRedDieRes(value)
            DieColor.GRAY -> getGrayDieRes(value)
            DieColor.WHITE -> getWhiteDieRes(value)
        }
    }

    private fun getRedDieRes(value: Int) = when (value) {
        1 -> R.drawable.red_die_1
        2 -> R.drawable.red_die_2
        3 -> R.drawable.red_die_3
        4 -> R.drawable.red_die_4
        5 -> R.drawable.red_die_5
        6 -> R.drawable.red_die_6
        else -> error("Invalid die value: $value")
    }

    private fun getGrayDieRes(value: Int) = when (value) {
        1 -> R.drawable.gray_die_1
        2 -> R.drawable.gray_die_2
        3 -> R.drawable.gray_die_3
        4 -> R.drawable.gray_die_4
        5 -> R.drawable.gray_die_5
        6 -> R.drawable.gray_die_6
        else -> error("Invalid die value: $value")
    }

    private fun getWhiteDieRes(value: Int) = when (value) {
        1 -> R.drawable.die_1
        2 -> R.drawable.die_2
        3 -> R.drawable.die_3
        4 -> R.drawable.die_4
        5 -> R.drawable.die_5
        6 -> R.drawable.die_6
        else -> error("Invalid die value: $value")
    }
}