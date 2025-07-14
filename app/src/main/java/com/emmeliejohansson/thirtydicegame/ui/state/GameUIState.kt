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
            !die.hasBeenRolled     -> DieColor.GRAY    // Unused die
            die.isSelected         -> DieColor.RED     // Selected by player
            hasNoMoreRolls         -> DieColor.GRAY    // Locked due to no rolls left
            else                   -> DieColor.WHITE   // Default, active state
        }
    }

    /**
     * Generates localized instruction text to guide the user based on game state.
     *
     * @param context Android context used for accessing string resources.
     * @param rollCount Current roll count in the round.
     * @param isScoreCategoryChosen Whether a scoring category has been selected.
     * @param selectedCategory The selected category, if any.
     * @param maxRolls The maximum number of rolls allowed per round.
     * @return A localized instruction string.
     */
    fun getInstructionText(
        context: Context,
        rollCount: Int,
        isScoreCategoryChosen: Boolean,
        selectedCategory: ScoreOption?,
        maxRolls: Int
    ): String {
        return when {
            isFirstRoll(rollCount) ->
                context.getString(R.string.instruction_text_start)

            isScoreCategoryChosen ->
                getScoringInstruction(context, selectedCategory)

            isRollLimitReached(rollCount, maxRolls) ->
                context.getString(R.string.instruction_text_round_over)

            else ->
                context.getString(R.string.instruction_text_select_dice_or_score)
        }
    }

    /**
     * Checks if the player is on their first roll of the round.
     */
    private fun isFirstRoll(rollCount: Int) = rollCount == 0

    /**
     * Checks if the player has exhausted all rolls for the round.
     */
    private fun isRollLimitReached(rollCount: Int, maxRolls: Int) = rollCount >= maxRolls

    /**
     * Returns a localized string instructing the user to score in the selected category.
     */
    private fun getScoringInstruction(context: Context, category: ScoreOption?): String {
        val categoryLabel = category?.label ?: "?"
        return context.getString(R.string.instruction_text_scoring_in_category, categoryLabel)
    }
}
