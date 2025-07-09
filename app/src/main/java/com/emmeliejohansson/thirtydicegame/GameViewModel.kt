package com.emmeliejohansson.thirtydicegame

import androidx.lifecycle.ViewModel
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.models.ScoreCalculator
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.emmeliejohansson.thirtydicegame.repository.DefaultDiceRepository
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository

class GameViewModel : ViewModel() {

    private val game = Game()
    private val diceRepository: DiceRepository = DefaultDiceRepository(game)

    var selectedCategory: ScoreOption? = null
        private set

    val isScoreCategoryChosen: Boolean
        get() = selectedCategory != null

    var isDiceSelected = false
        private set

    val dice: List<Die>
        get() = diceRepository.getAllDice()

    val roundNumber: Int
        get() = game.getCurrentRoundNumber()

    val rollCount: Int
        get() = game.currentRound.rollCount

    val maxRolls: Int
        get() = game.currentRound.maxRolls

    val scoreMap: MutableMap<ScoreOption, Int> = mutableMapOf()
    var remainingCategories = ScoreOption.entries.toMutableList()

    var areScoreButtonsEnabled: Boolean = false

    fun isGameOver(): Boolean = game.isGameOver

    fun isEndOfRound(): Boolean = !game.currentRound.canRoll()

    fun setScoreButtonsEnabled(enabled: Boolean) {
        areScoreButtonsEnabled = enabled
    }

    fun rollDice() {
        if (!game.canRoll()) return

        if (game.isFirstRoll()) {
            dice.forEach { it.roll() }
        } else {
            diceRepository.getSelectedDice().forEach { it.roll() }
        }

        diceRepository.deselectAllDice()
        game.incrementRollCount()
        updateDiceSelectionState()
    }

    fun toggleDieSelected(index: Int) {
        diceRepository.getDieById(index + 1)?.toggleIsSelected()
        updateDiceSelectionState()
    }

    private fun updateDiceSelectionState() {
        isDiceSelected = dice.any { it.isSelected }
    }

    fun selectCategory(category: ScoreOption) {
        selectedCategory = category
    }

    fun clearSelectedCategory() {
        selectedCategory = null
    }

    fun registerScore(category: ScoreOption, score: Int) {
        if (!scoreMap.containsKey(category)) {
            scoreMap[category] = score
            remainingCategories.remove(category)
        }
    }

    fun resetForNextRound() {
        game.resetRound()
        clearSelectedCategory()
        diceRepository.deselectAllDice()
        diceRepository.resetDice()
    }

    fun calculateScoreForCategory(category: ScoreOption, selectedDice: List<Int>): Result<Int> {
        return ScoreCalculator.calculateScore(category, selectedDice)
    }

    fun getSelectedDice(): List<Die> = diceRepository.getSelectedDice()

    fun getTotalScore(): Int = scoreMap.values.sum()
}
