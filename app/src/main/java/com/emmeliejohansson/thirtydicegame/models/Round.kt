package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents a single round in the dice game.
 */
class Round {

    companion object {
        const val MAX_ROLLS = 3
    }

    val dice: List<Die> = List(6) { Die(it + 1) }

    var rollCount = 0
        private set

    private var selectedCategory: ScoreOption? = null
    private var score: Int? = null

    var isScored: Boolean = false
        private set

    fun canRoll(): Boolean = rollCount < MAX_ROLLS

    fun incrementRollCount() {
        if (canRoll()) {
            rollCount++
        }
    }

    fun setScore(category: ScoreOption, value: Int) {
        if (isScored) throw IllegalStateException("Score already set for this round.")
        selectedCategory = category
        score = value
        isScored = true
    }
}

