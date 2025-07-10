package com.emmeliejohansson.thirtydicegame.ui.state

import android.content.Context
import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.DieColor
import com.emmeliejohansson.thirtydicegame.models.ScoreOption

class GameUIState {
    fun getDieColor(die: Die, hasNoMoreRolls: Boolean): DieColor {
        return when {
            !die.hasBeenRolled -> DieColor.GRAY
            die.isSelected -> DieColor.RED
            hasNoMoreRolls -> DieColor.GRAY
            else -> DieColor.WHITE
        }
    }

    fun getInstructionText(
        context: Context,
        rollCount: Int,
        isScoreCategoryChosen: Boolean,
        selectedCategory: ScoreOption?,
        maxRolls: Int
    ): String {
        return when {
            rollCount == 0 -> context.getString(R.string.instruction_text_start)
            isScoreCategoryChosen -> {
                val categoryName = selectedCategory?.label ?: "?"
                context.getString(R.string.instruction_text_scoring_in_category, categoryName)
            }
            rollCount >= maxRolls -> context.getString(R.string.instruction_text_round_over)
            else -> context.getString(R.string.instruction_text_select_dice_or_score)
        }
    }
}
