package com.emmeliejohansson.thirtydicegame.managers

import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.models.ScoreCalculator
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository

/**
 * Manages the core game logic: dice rolling, round progression, and player interactions.
 *
 * @param game The current game model instance.
 * @param diceRepository Abstraction over dice state handling.
 */
class GameManager(
    private val game: Game,
    private val diceRepository: DiceRepository
) {

    /** List of all dice in the current round. */
    val dice: List<Die> get() = game.currentRound.dice

    /** Number of times the player has rolled in the current round. */
    val rollCount: Int get() = game.currentRound.rollCount

    /** Maximum rolls allowed per round. */
    val maxRolls: Int get() = Game.ROLLS_PER_ROUND

    /** The current round number (1-based index). */
    val roundNumber: Int get() = game.getCurrentRoundNumber()

    /** Indicates whether the game has ended. */
    val isGameOver: Boolean get() = game.isGameOver

    /**
     * Rolls all dice on first roll, or only selected dice on subsequent rolls.
     * Increments the roll count. No effect if roll limit is reached.
     */
    fun rollDice() {
        if (!canRollMore()) return

        if (rollCount == 0) {
            dice.forEach { it.roll() }
        } else {
            getSelectedDice().forEach { it.roll() }
        }

        game.currentRound.incrementRollCount()
    }

    /**
     * Toggles the selected state of a die at the given index.
     * Has no effect if the index is out of bounds.
     *
     * @param index Index of the die in the dice list.
     */
    fun toggleDieSelected(index: Int) {
        if (index in dice.indices) {
            dice[index].toggleIsSelected()
        }
    }

    /**
     * Completes the round by calculating the score for a selected category
     * using the selected dice. Resets the round afterward.
     *
     * @param category The selected score category.
     * @return Result containing score or an error if no dice were selected.
     */
    fun completeRound(category: ScoreOption): Result<Int> {
        val selectedDice = getSelectedDice().map { it.value }

        if (selectedDice.isEmpty()) {
            return Result.failure(IllegalArgumentException("No dice selected."))
        }

        return ScoreCalculator.calculateScore(category, selectedDice).fold(
            onSuccess = { score ->
                game.scoreCategory(category, score)
                game.resetRound()
                Result.success(score)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    /**
     * Resets all dice to their initial state (unselected and unrolled).
     * Typically used when starting a new round.
     */
    fun resetDice() {
        dice.forEach { it.reset() }
    }

    /**
     * Deselects all dice.
     * Used to prepare for the next roll within the same round.
     */
    fun prepareForNextRoll() {
        dice.forEach { it.deselect() }
    }

    /**
     * Restores the round number (used when restoring saved game state).
     *
     * @param number Round number to restore.
     */
    fun restoreRoundNumber(number: Int) {
        game.setCurrentRoundNumber(number)
    }

    /**
     * Restores the current round's roll count (used when restoring saved game state).
     *
     * @param count Number of rolls to set.
     */
    fun restoreRollCount(count: Int) {
        game.currentRound.restoreRollCount(count)
    }

    /**
     * Restores the state of all dice from saved data.
     *
     * @param savedDice List of maps representing die properties.
     */
    fun restoreDice(savedDice: List<Map<String, Any>>) {
        val currentDice = dice
        savedDice.forEachIndexed { index, savedDie ->
            val value = savedDie["value"]
            val isSelected = savedDie["isSelected"]
            val hasBeenRolled = savedDie["hasBeenRolled"]

            if (value is Int && isSelected is Boolean && hasBeenRolled is Boolean) {
                if (index in currentDice.indices) {
                    currentDice[index].restoreState(value, isSelected, hasBeenRolled)
                }
            }
        }
    }

    /**
     * Returns true if the player is allowed to roll again in the current round.
     */
    fun canRollMore(): Boolean = rollCount < maxRolls

    /**
     * Returns true if the player has reached the roll limit for the current round.
     */
    fun isRollLimitReached(): Boolean = rollCount >= maxRolls

    /**
     * Returns a list of all currently selected dice.
     */
    private fun getSelectedDice(): List<Die> = dice.filter { it.isSelected }
}
