package com.emmeliejohansson.thirtydicegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.services.DiceStore

class ThirtyDiceGameViewModel : ViewModel() {

    // Game instance
    private val _game = Game().apply { fillDiceStore() }
    val game: Game get() = _game

    // Score map: stores category -> score
    private val _scoreMap = mutableMapOf<String, Int>()
    val scoreMap: Map<String, Int> get() = _scoreMap

    // Tracks available categories (LiveData so UI can observe)
    private val _remainingCategories = MutableLiveData<List<String>>(
        listOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    )
    val remainingCategories: LiveData<List<String>> = _remainingCategories

    // Register score for a category
    fun registerScore(category: String, score: Int) {
        if (!_scoreMap.containsKey(category)) {
            _scoreMap[category] = score
            removeCategory(category)
        }
    }

    // Remove category from available list
    private fun removeCategory(category: String) {
        _remainingCategories.value = _remainingCategories.value?.filter { it != category }
    }

    // Roll dice
    fun rollDice() {
        _game.rollDice()
    }

    // Reset game for next round
    fun resetForNextRound() {
        _game.resetForNextRound()
    }

    // Check if game is finished
    fun isGameOver(): Boolean = _game.isGameOver()

    // Optional: check if current round is over (e.g. max rolls used)
    fun isEndOfRound(): Boolean = _game.isEndOfRound()

    // Use category (marks it used internally too)
    fun useCategory(category: String) {
        _game.useScoringCategory(category)
        removeCategory(category)
    }

    // Expose current dice
    fun getDice(): List<Die> = DiceStore.getAllDice()

    // Expose selected dice
    fun getSelectedDice(): List<Die> = DiceStore.getSelectedDice()

    // Get current total score
    fun getTotalScore(): Int = _scoreMap.values.sum()
}
