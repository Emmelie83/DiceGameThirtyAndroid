package com.emmeliejohansson.thirtydicegame.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.emmeliejohansson.thirtydicegame.managers.GameManager
import com.emmeliejohansson.thirtydicegame.repository.DefaultDiceRepository
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository
import com.emmeliejohansson.thirtydicegame.ui.state.GameUIState

/**
 * ViewModel that manages the game logic and state for the UI layer.
 * It acts as a bridge between UI components and the game logic handled by GameManager.
 */
class GameViewModel : ViewModel() {

    /** Core game logic and state */
    private val game = Game()

    /** Dice repository providing data access */
    private val diceRepository: DiceRepository = DefaultDiceRepository(game)

    /** Manages game logic like rolling, tracking rounds, etc. */
    private val gameManager = GameManager(game, diceRepository)

    /** Provides UI-specific logic like text and color */
    private val uiState = GameUIState()

    /** Currently selected score category */
    var selectedCategory: ScoreOption? = null
        private set

    /** Current state of dice */
    val dice: List<Die> get() = gameManager.dice

    /** Number of rolls left in the current round */
    val rollsLeft: Int get() = gameManager.maxRolls - gameManager.rollCount

    /** Number of times the dice have been rolled this round */
    private val rollCount: Int get() = gameManager.rollCount

    /** Current round number (1-based index) */
    val roundNumber: Int get() = gameManager.roundNumber

    /** Stores scores per category */
    private val scoreMap = mutableMapOf<ScoreOption, Int>()

    /** Remaining unselected score categories */
    var remainingCategories = ScoreOption.entries.toMutableList()

    /** Controls whether score buttons are enabled in the UI */
    var areScoreButtonsEnabled: Boolean = false

    /** True if at least one die is currently selected */
    private var isDiceSelected: Boolean = false

    /** Updates the selection state for dice */
    private fun updateDiceSelectionState() {
        isDiceSelected = dice.any { it.isSelected }
    }

    /** Rolls the dice and updates selection state */
    fun rollDice() {
        gameManager.rollDice()
        updateDiceSelectionState()
    }

    /** Prepares the game for the next roll and updates selection state */
    fun prepareForNextRoll() {
        gameManager.prepareForNextRoll()
        updateDiceSelectionState()
    }

    /**
     * Toggles selection state of a die at given index
     *
     * @param index index of die to toggle
     */
    fun toggleDieSelected(index: Int) {
        if (rollCount > 0) {
            gameManager.toggleDieSelected(index)
            updateDiceSelectionState()
        }
    }

    /**
     * Returns instruction text based on current game state
     *
     * @param context context used for localization
     * @return formatted instruction string
     */
    fun getInstructionText(context: Context): String {
        return uiState.getInstructionText(
            context = context,
            rollCount = gameManager.rollCount,
            isScoreCategoryChosen = selectedCategory != null,
            selectedCategory = selectedCategory,
            maxRolls = gameManager.maxRolls
        )
    }

    /**
     * Returns the appropriate die color for UI based on state
     *
     * @param die the die to evaluate
     * @return color representation for the die
     */
    fun getDieColor(die: Die): DieColor {
        return uiState.getDieColor(die, gameManager.isRollLimitReached())
    }

    /** Returns true if the game is over */
    fun isGameOver() = gameManager.isGameOver

    fun completeRound(
        selectedCategory: ScoreOption,
        onSuccess: (isGameOver: Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val result = gameManager.completeRound(selectedCategory)
        handleScoreResult(selectedCategory, result, onSuccess, onFailure)
    }


    /**
     * Checks if the roll button should be enabled
     *
     * @return true if roll is allowed, false otherwise
     */
    fun isRollButtonEnabled() =
        (gameManager.rollCount == 0 || isDiceSelected) && !gameManager.isEndOfRound()

    /**
     * Sets the selected score category
     *
     * @param category the category to select
     */
    fun selectCategory(category: ScoreOption) {
        selectedCategory = category
    }

    /** Clears the selected score category */
    private fun clearSelectedCategory() {
        selectedCategory = null
    }

    /**
     * Enables or disables the score category buttons
     *
     * @param enabled true to enable, false to disable
     */
    fun setScoreButtonsEnabled(enabled: Boolean) {
        areScoreButtonsEnabled = enabled
    }

    /** Returns the currently selected dice */
    private fun getSelectedDice(): List<Die> = gameManager.getSelectedDice()

    /** Returns the total accumulated score */
    fun getTotalScore(): Int = scoreMap.values.sum()

    /** Returns true if "Next Round" button should be enabled */
    val isNextRoundButtonEnabled: Boolean
        get() = selectedCategory != null && isDiceSelected




    /** Handles the result of score calculation */
    private fun handleScoreResult(
        category: ScoreOption,
        result: Result<Int>,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        result.fold(
            onSuccess = { score ->
                registerScore(category, score)
                //resetRound()
                onSuccess(isGameOver())
            },
            onFailure = { error ->
                onFailure(error)
            }
        )
    }

    /** Resets internal state for UI round preparation */
    fun prepareRoundForUI() {
        resetRound()
    }


    /** Resets the game state for the next round */
    private fun resetRound() {
        clearSelectedCategory()
        gameManager.resetDice()
        setScoreButtonsEnabled(false)
    }


    /**
     * Registers the score for a given category and updates category list
     *
     * @param category score category used
     * @param score score earned in this category
     */
    private fun registerScore(category: ScoreOption, score: Int) {
        if (!scoreMap.containsKey(category)) {
            scoreMap[category] = score
            remainingCategories.remove(category)
        }
    }

    /**
     * Returns a map of score category names to their scores for export (e.g., to result screen)
     *
     * @return map of category name to score
     */
    fun getExportableScores(): Map<String, Int> {
        return scoreMap.mapKeys { it.key.name }
    }
}