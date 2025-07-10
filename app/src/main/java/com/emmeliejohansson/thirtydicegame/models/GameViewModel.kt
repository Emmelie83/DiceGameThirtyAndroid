package com.emmeliejohansson.thirtydicegame.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.emmeliejohansson.thirtydicegame.managers.GameManager
import com.emmeliejohansson.thirtydicegame.repository.DefaultDiceRepository
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository
import com.emmeliejohansson.thirtydicegame.ui.state.GameUIState

class GameViewModel : ViewModel() {

    private val game = Game()
    private val diceRepository: DiceRepository = DefaultDiceRepository(game)
    private val gameManager = GameManager(game, diceRepository)
    private val uiState = GameUIState()

    var selectedCategory: ScoreOption? = null
        private set

    val dice: List<Die> get() = gameManager.dice
    val rollsLeft: Int get() = gameManager.maxRolls - gameManager.rollCount
    val rollCount: Int get() = gameManager.rollCount
    val roundNumber: Int get() = gameManager.roundNumber

    val scoreMap = mutableMapOf<ScoreOption, Int>()
    var remainingCategories = ScoreOption.entries.toMutableList()

    var areScoreButtonsEnabled: Boolean = false
    var isDiceSelected: Boolean = false
        private set

    fun updateDiceSelectionState() {
        isDiceSelected = dice.any { it.isSelected }
    }

    fun rollDice() {
        gameManager.rollDice()
        updateDiceSelectionState()
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
            rollCount = gameManager.rollCount,
            isScoreCategoryChosen = selectedCategory != null,
            selectedCategory = selectedCategory,
            maxRolls = gameManager.maxRolls
        )
    }

    fun getDieColor(die: Die): DieColor {
        return uiState.getDieColor(die, gameManager.hasNoMoreRolls())
    }

    fun isGameOver() = gameManager.isGameOver
    fun isRollButtonEnabled() = (gameManager.rollCount == 0 || isDiceSelected) && !gameManager.isEndOfRound()

    fun selectCategory(category: ScoreOption) {
        selectedCategory = category
    }

    fun clearSelectedCategory() {
        selectedCategory = null
    }

    fun setScoreButtonsEnabled(enabled: Boolean) {
        areScoreButtonsEnabled = enabled
    }

    fun getSelectedDice(): List<Die> = gameManager.getSelectedDice()

    fun getTotalScore(): Int = scoreMap.values.sum()

    val isNextRoundButtonEnabled: Boolean
        get() = selectedCategory != null && isDiceSelected

    fun applyScoreAndAdvance(
        selectedCategory: ScoreOption,
        onSuccess: (Int, Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val selectedDiceValues = getSelectedDice().map { it.value }
        val result = ScoreCalculator.calculateScore(selectedCategory, selectedDiceValues)

        result.fold(
            onSuccess = { score ->
                registerScore(selectedCategory, score)
                onSuccess(score, isGameOver())
            },
            onFailure = { error -> onFailure(error) }
        )
    }

    fun registerScore(category: ScoreOption, score: Int) {
        if (!scoreMap.containsKey(category)) {
            scoreMap[category] = score
            remainingCategories.remove(category)
        }
    }

    fun resetForNextRound() {
        gameManager.resetRound()
        clearSelectedCategory()
        gameManager.resetDice()
    }

    fun prepareForNextRound() {
        resetForNextRound()
        setScoreButtonsEnabled(false)
    }

    fun getExportableScores(): Map<String, Int> {
        return scoreMap.mapKeys { it.key.name }
    }
}
