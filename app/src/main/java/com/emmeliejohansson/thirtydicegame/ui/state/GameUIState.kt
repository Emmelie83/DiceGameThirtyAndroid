package com.emmeliejohansson.thirtydicegame.ui.state

import android.content.Context
import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.DieColor
import com.emmeliejohansson.thirtydicegame.models.ScoreOption

/**
 * Handles UI state logic related to game instructions and die appearance.
 * This class abstracts how the UI should represent the current game state visually and textually.
 */
class GameUIState {

    /**
     * Determines the color to be used for a die based on its current state.
     *
     * @param die The die object whose color is to be determined.
     * @param hasNoMoreRolls True if the player has exhausted all allowed rolls for the round.
     * @return The [DieColor] representing the visual state of the die.
     */
    fun getDieColor(die: Die, hasNoMoreRolls: Boolean): DieColor {
        return when {
            // If the die hasn't been rolled yet, show it as gray (inactive)
            !die.hasBeenRolled -> DieColor.GRAY

            // If the die is selected by the player, highlight it as red
            die.isSelected -> DieColor.RED

            // If no more rolls are allowed in this round, show dice as gray to indicate locked
            hasNoMoreRolls -> DieColor.GRAY

            // Otherwise, display dice in default white color (available for interaction)
            else -> DieColor.WHITE
        }
    }

    /**
     * Generates an instruction text string to guide the player based on the current game state.
     *
     * @param context Android context used for string resource access and localization.
     * @param rollCount Number of rolls that have already been performed this round.
     * @param isScoreCategoryChosen True if the player has selected a scoring category.
     * @param selectedCategory The currently selected scoring category, if any.
     * @param maxRolls The maximum number of rolls allowed per round.
     * @return A localized instruction string to be displayed to the user.
     */
    fun getInstructionText(
        context: Context,
        rollCount: Int,
        isScoreCategoryChosen: Boolean,
        selectedCategory: ScoreOption?,
        maxRolls: Int
    ): String {
        return when {
            // When no rolls have been made yet, instruct the player to start rolling
            rollCount == 0 -> context.getString(R.string.instruction_text_start)

            // When a scoring category is selected, instruct to score in that category
            isScoreCategoryChosen -> {
                val categoryName = selectedCategory?.label ?: "?"
                context.getString(R.string.instruction_text_scoring_in_category, categoryName)
            }

            // When maximum rolls have been reached, indicate the round is over
            rollCount >= maxRolls -> context.getString(R.string.instruction_text_round_over)

            // Default case: prompt player to select dice or score
            else -> context.getString(R.string.instruction_text_select_dice_or_score)
        }
    }
}
