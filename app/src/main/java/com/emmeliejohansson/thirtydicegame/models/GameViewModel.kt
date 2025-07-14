package com.emmeliejohansson.thirtydicegame.models

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.emmeliejohansson.thirtydicegame.managers.GameManager
import com.emmeliejohansson.thirtydicegame.repository.DefaultDiceRepository
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository
import com.emmeliejohansson.thirtydicegame.ui.state.GameUIState

/**
 * ViewModel responsible for managing game logic, player interactions,
 * state restoration, and UI-related information in the Thirty Dice Game.
 */
class GameViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        // Keys used for persisting and restoring game state
        private const val KEY_SCORE_MAP = "scoreMap"
        private const val KEY_REMAINING_CATEGORIES = "remainingCategories"
        private const val KEY_ROLLS_LEFT = "rollsLeft"
        private const val KEY_ROUND_NUMBER = "roundNumber"
        private const val KEY_DICE = "dice"
    }

    private val game = Game()
    private val diceRepository: DiceRepository = DefaultDiceRepository(game)
    private val gameManager = GameManager(game, diceRepository)
    private val uiState = GameUIState()

    var selectedCategory: ScoreOption? = null
        private set

    val dice: List<Die> get() = gameManager.dice
    val rollsLeft: Int get() = gameManager.maxRolls - gameManager.rollCount
    private val rollCount: Int get() = gameManager.rollCount
    val roundNumber: Int get() = gameManager.roundNumber

    private val scoreMap = mutableMapOf<ScoreOption, Int>()
    var remainingCategories = ScoreOption.entries.toMutableList()
    var areScoreButtonsEnabled: Boolean = false
    private var isDiceSelected: Boolean = false
    private var wasStateRestored = false

    init {
        restoreGameState()
    }

    /** Rolls the dice and updates selection state and persistence */
    fun rollDice() {
        gameManager.rollDice()
        updateDiceSelectionState()
        saveGameState()
    }

    /** Resets dice selection after a roll and persists state */
    fun prepareForNextRoll() {
        gameManager.prepareForNextRoll()
        updateDiceSelectionState()
        saveGameState()
    }

    /** Toggles selection of a die (if the user has already rolled at least once) */
    fun toggleDieSelected(index: Int) {
        if (rollCount > 0) {
            gameManager.toggleDieSelected(index)
            updateDiceSelectionState()
        }
    }

    /** Returns user instruction text based on the current game state */
    fun getInstructionText(context: Context): String {
        return uiState.getInstructionText(
            context = context,
            rollCount = rollCount,
            isScoreCategoryChosen = selectedCategory != null,
            selectedCategory = selectedCategory,
            maxRolls = gameManager.maxRolls
        )
    }

    /** Returns appropriate color for a die depending on its current status */
    fun getDieColor(die: Die): DieColor {
        return uiState.getDieColor(die, gameManager.isRollLimitReached())
    }

    /** Checks whether the game has ended (all categories scored) */
    fun isGameOver() = gameManager.isGameOver

    /**
     * Finalizes a round by calculating and registering score.
     * Invokes the given callbacks for success/failure.
     */
    fun completeRound(
        selectedCategory: ScoreOption,
        onSuccess: (isGameOver: Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val result = gameManager.completeRound(selectedCategory)
        handleScoreResult(selectedCategory, result, onSuccess, onFailure)
    }

    /** Determines whether the roll button should be enabled */
    fun isRollButtonEnabled(): Boolean {
        return (rollCount == 0 || isDiceSelected) && gameManager.canRollMore()
    }

    /** Sets the currently selected scoring category */
    fun selectCategory(category: ScoreOption) {
        selectedCategory = category
    }

    /** Clears the selected scoring category */
    private fun clearSelectedCategory() {
        selectedCategory = null
    }

    /** Enables or disables the category scoring buttons */
    fun setScoreButtonsEnabled(enabled: Boolean) {
        areScoreButtonsEnabled = enabled
    }

    /** Calculates and returns the total score accumulated by the player */
    fun getTotalScore(): Int = scoreMap.values.sum()

    /** Returns whether the 'Next Round' button should be active */
    val isNextRoundButtonEnabled: Boolean
        get() = selectedCategory != null && isDiceSelected

    /** Resets relevant state at the start of a new round */
    fun prepareRoundForUI() {
        resetRound()
    }

    /** Clears state and prepares dice for a new round */
    private fun resetRound() {
        clearSelectedCategory()
        gameManager.resetDice()
        if (!wasStateRestored) setScoreButtonsEnabled(false)
        saveGameState()
    }

    /** Registers score for a selected category and removes it from the list */
    private fun registerScore(category: ScoreOption, score: Int) {
        if (!scoreMap.containsKey(category)) {
            scoreMap[category] = score
            remainingCategories.remove(category)
            saveGameState()
        }
    }

    /** Converts score data to a serializable map with string keys */
    fun getExportableScores(): Map<String, Int> {
        return scoreMap.mapKeys { it.key.name }
    }

    /** Handles result of scoring operation: register on success, report on failure */
    private fun handleScoreResult(
        category: ScoreOption,
        result: Result<Int>,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        result.fold(
            onSuccess = { score ->
                registerScore(category, score)
                saveGameState()
                onSuccess(isGameOver())
            },
            onFailure = { error ->
                onFailure(error)
            }
        )
    }

    /** Updates whether any dice are currently selected */
    private fun updateDiceSelectionState() {
        isDiceSelected = dice.any { it.isSelected }
    }

    /** Saves all current game state into SavedStateHandle for process restoration */
    private fun saveGameState() {
        savedStateHandle[KEY_SCORE_MAP] = scoreMap.mapKeys { it.key.name }
        savedStateHandle[KEY_REMAINING_CATEGORIES] = remainingCategories.map { it.name }
        savedStateHandle[KEY_ROLLS_LEFT] = rollsLeft
        savedStateHandle[KEY_ROUND_NUMBER] = roundNumber
        savedStateHandle[KEY_DICE] = dice.map {
            mapOf(
                "value" to it.value,
                "isSelected" to it.isSelected,
                "hasBeenRolled" to it.hasBeenRolled
            )
        }
        savedStateHandle["scoreButtonsEnabled"] = areScoreButtonsEnabled
    }

    /** Restores all persisted game state from SavedStateHandle */
    private fun restoreGameState() {
        restoreScoreMap()
        restoreRemainingCategories()
        restoreRollCount()
        restoreRoundNumber()
        restoreDice()
        restoreScoreButtonsEnabled()
        updateDiceSelectionState()
        setScoreButtonsEnabled(areScoreButtonsEnabled)
        wasStateRestored = true
    }

    /** Restores score values for each category */
    private fun restoreScoreMap() {
        val savedScoreMap = savedStateHandle.get<Map<String, Int>>(KEY_SCORE_MAP)
        savedScoreMap?.forEach { (key, value) ->
            val category = ScoreOption.valueOf(key)
            scoreMap[category] = value
        }
    }

    /** Restores which scoring categories remain available */
    private fun restoreRemainingCategories() {
        val savedRemaining = savedStateHandle.get<List<String>>(KEY_REMAINING_CATEGORIES)
        savedRemaining?.let {
            remainingCategories = it.map { name -> ScoreOption.valueOf(name) }.toMutableList()
        }
    }

    /** Restores how many rolls were left in the current round */
    private fun restoreRollCount() {
        val savedRollsLeft = savedStateHandle.get<Int>(KEY_ROLLS_LEFT)
        savedRollsLeft?.let {
            val rollCount = Game.ROLLS_PER_ROUND - it
            gameManager.restoreRollCount(rollCount)
        }
    }

    /** Restores the current round number */
    private fun restoreRoundNumber() {
        val savedRoundNumber = savedStateHandle.get<Int>(KEY_ROUND_NUMBER)
        savedRoundNumber?.let {
            gameManager.restoreRoundNumber(it)
        }
    }

    /** Restores the dice values, selections, and roll status */
    private fun restoreDice() {
        val savedDice = savedStateHandle.get<List<Map<String, Any>>>(KEY_DICE)
        savedDice?.let {
            gameManager.restoreDice(it)
        }
    }

    /** Restores whether score buttons were enabled */
    private fun restoreScoreButtonsEnabled() {
        val scoreButtonsEnabled = savedStateHandle.get<Boolean>("scoreButtonsEnabled")
        areScoreButtonsEnabled = scoreButtonsEnabled ?: false
    }
}
