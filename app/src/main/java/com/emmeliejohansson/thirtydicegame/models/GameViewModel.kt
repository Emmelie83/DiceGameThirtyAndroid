package com.emmeliejohansson.thirtydicegame.models

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.emmeliejohansson.thirtydicegame.managers.GameManager
import com.emmeliejohansson.thirtydicegame.repository.DefaultDiceRepository
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository
import com.emmeliejohansson.thirtydicegame.ui.state.GameUIState

class GameViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
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

    init {
        restoreGameState()
    }

    fun rollDice() {
        gameManager.rollDice()
        updateDiceSelectionState()
        saveGameState()
    }

    fun prepareForNextRoll() {
        gameManager.prepareForNextRoll()
        updateDiceSelectionState()
        saveGameState()
    }

    fun toggleDieSelected(index: Int) {
        if (rollCount > 0) {
            gameManager.toggleDieSelected(index)
            updateDiceSelectionState()
        }
    }

    fun getInstructionText(context: Context): String {
        return uiState.getInstructionText(
            context = context,
            rollCount = rollCount,
            isScoreCategoryChosen = selectedCategory != null,
            selectedCategory = selectedCategory,
            maxRolls = gameManager.maxRolls
        )
    }

    fun getDieColor(die: Die): DieColor {
        return uiState.getDieColor(die, gameManager.isRollLimitReached())
    }

    fun isGameOver() = gameManager.isGameOver

    fun completeRound(
        selectedCategory: ScoreOption,
        onSuccess: (isGameOver: Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val result = gameManager.completeRound(selectedCategory)
        handleScoreResult(selectedCategory, result, onSuccess, onFailure)
    }

    fun isRollButtonEnabled(): Boolean {
        return (rollCount == 0 || isDiceSelected) && !gameManager.isEndOfRound()
    }

    fun selectCategory(category: ScoreOption) {
        selectedCategory = category
    }

    private fun clearSelectedCategory() {
        selectedCategory = null
    }

    fun setScoreButtonsEnabled(enabled: Boolean) {
        areScoreButtonsEnabled = enabled
    }

    private fun getSelectedDice(): List<Die> = gameManager.getSelectedDice()
    fun getTotalScore(): Int = scoreMap.values.sum()

    val isNextRoundButtonEnabled: Boolean
        get() = selectedCategory != null && isDiceSelected

    fun prepareRoundForUI() {
        resetRound()
    }

    private fun resetRound() {
        clearSelectedCategory()
        gameManager.resetDice()
        setScoreButtonsEnabled(false)
        saveGameState()
    }

    private fun registerScore(category: ScoreOption, score: Int) {
        if (!scoreMap.containsKey(category)) {
            scoreMap[category] = score
            remainingCategories.remove(category)
            saveGameState()
        }
    }

    fun getExportableScores(): Map<String, Int> {
        return scoreMap.mapKeys { it.key.name }
    }

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

    private fun updateDiceSelectionState() {
        isDiceSelected = dice.any { it.isSelected }
    }

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

    private fun restoreGameState() {
        val savedScoreMap = savedStateHandle.get<Map<String, Int>>(KEY_SCORE_MAP)
        savedScoreMap?.forEach { (key, value) ->
            val category = ScoreOption.valueOf(key)
            scoreMap[category] = value
        }

        val savedRemaining = savedStateHandle.get<List<String>>(KEY_REMAINING_CATEGORIES)
        savedRemaining?.let {
            remainingCategories = it.map { name -> ScoreOption.valueOf(name) }.toMutableList()
        }

        val savedRollsLeft = savedStateHandle.get<Int>(KEY_ROLLS_LEFT)
        savedRollsLeft?.let {
            val rollCount = Game.ROLLS_PER_ROUND - it
            gameManager.restoreRollCount(rollCount)
        }

        val savedRoundNumber = savedStateHandle.get<Int>(KEY_ROUND_NUMBER)
        savedRoundNumber?.let {
            gameManager.restoreRoundNumber(it)
        }

        val savedDice = savedStateHandle.get<List<Map<String, Any>>>(KEY_DICE)
        savedDice?.let {
            gameManager.restoreDice(it)
        }

        val scoreButtonsEnabled = savedStateHandle.get<Boolean>("scoreButtonsEnabled")
        areScoreButtonsEnabled = scoreButtonsEnabled ?: false

        updateDiceSelectionState()
        setScoreButtonsEnabled(areScoreButtonsEnabled)

    }
}
