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

    // ----------------------------
    // Game State Properties
    // ----------------------------

    /** The current list of all dice in play. */
    val dice: List<Die> get() = diceRepository.getAllDice()

    /** Current number of rolls made in this round. */
    val rollCount: Int get() = game.currentRound.rollCount

    /** Maximum rolls allowed per round. */
    val maxRolls: Int get() = Game.ROLLS_PER_ROUND

    /** 1-based index of the current round. */
    val roundNumber: Int get() = game.getCurrentRoundNumber()

    /** Indicates if the game has ended (all rounds played). */
    val isGameOver: Boolean get() = game.isGameOver

    fun restoreRoundNumber(number: Int) {
        game.setCurrentRoundNumber(number)
    }

    /** Returns true if player can roll more dice this round. */
    private fun canRoll(): Boolean = game.canRoll()

    /** Returns true if max number of rolls is reached. */
    fun isRollLimitReached(): Boolean = rollCount >= maxRolls

    /** Returns true if no more rolls are available in this round. */
    fun isEndOfRound(): Boolean = !canRoll()

    // ----------------------------
    // Game Actions
    // ----------------------------

    /**
     * Rolls all or selected dice depending on the roll count.
     */
    fun rollDice() {
        if (!canRoll()) return

        if (rollCount == 0) {
            rollAllDice()
        } else {
            rollSelectedDice()
        }
    }

    /**
     * Prepares for the next roll:
     * - Deselects all dice.
     * - Increments roll count.
     */
    fun prepareForNextRoll() {
        diceRepository.deselectAllDice()
        incrementRollCount()
    }

    fun completeRound(category: ScoreOption): Result<Int> {
        val selectedDice = getSelectedDice().map { it.value }

        if (selectedDice.isEmpty()) {
            return Result.failure(IllegalArgumentException("No dice selected."))
        }

        val scoreResult = ScoreCalculator.calculateScore(category, selectedDice)

        return scoreResult.fold(
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

    // ----------------------------
    // Dice Manipulation
    // ----------------------------

    /** Rolls all six dice — only used at the start of a round. */
    private fun rollAllDice() {
        dice.forEach { it.roll() }
    }

    /** Rolls only the dice that the player has selected to keep. */
    private fun rollSelectedDice() {
        diceRepository.getSelectedDice().forEach { it.roll() }
    }

    /**
     * Toggles the selected state of a die by index (0-based).
     * Does nothing if index is out of bounds or die not found.
     */
    fun toggleDieSelected(index: Int) {
        if (index in 0 until dice.size) {
            diceRepository.getDieById(index + 1)?.toggleIsSelected()
        }
    }

    /** Returns a list of all currently selected dice. */
    fun getSelectedDice(): List<Die> = diceRepository.getSelectedDice()

    /**
     * Resets all dice to initial state and clears selection.
     */
    fun resetDice() {
        diceRepository.deselectAllDice()
        diceRepository.resetDice()
    }

    // ----------------------------
    // Internal Helpers
    // ----------------------------

    /** Advances the game's internal roll counter. */
    private fun incrementRollCount() {
        game.currentRound.incrementRollCount()
    }

    fun restoreRollCount(rollCount: Int) {
        game.currentRound.restoreRollCount(rollCount)
    }

    fun restoreDice(savedDice: List<Map<String, Any>>) {
        val currentDice = diceRepository.getAllDice()
        savedDice.forEachIndexed { index, savedDie ->
            val value = savedDie["value"] as? Int ?: return@forEachIndexed
            val isSelected = savedDie["isSelected"] as? Boolean ?: false
            val hasBeenRolled = savedDie["hasBeenRolled"] as? Boolean ?: false

            if (index in currentDice.indices) {
                currentDice[index].value = value
                currentDice[index].isSelected = isSelected
                currentDice[index].hasBeenRolled = hasBeenRolled
            }
        }
    }
}
