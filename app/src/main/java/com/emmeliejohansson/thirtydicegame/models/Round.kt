package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents a single round in the dice game.
 * Manages the dice, roll counts, scoring, and category selection for that round.
 */
class Round {

    companion object {
        /**
         * The maximum number of dice rolls allowed per round.
         */
        const val MAX_ROLLS = 3
    }

    /**
     * The list of dice used in the round.
     * Initialized with 6 dice, each with a unique initial value from 1 to 6.
     */
    val dice: List<Die> = List(6) { Die(it + 1) }

    /**
     * The number of rolls that have been made so far in this round.
     * Private setter to prevent external modification.
     */
    var rollCount = 0
        private set

    /**
     * The category selected by the player for scoring this round.
     * Nullable, since it may not be set until scoring occurs.
     */
    private var selectedCategory: ScoreOption? = null

    /**
     * The score value assigned for the selected category in this round.
     * Nullable until a score has been set.
     */
    private var score: Int? = null

    /**
     * Flag indicating whether the score has already been set for this round.
     * Prevents scoring more than once per round.
     */
    private var isScored: Boolean = false

    /**
     * Returns whether the player can roll the dice again in this round.
     * True if rollCount is less than the allowed MAX_ROLLS.
     */
    fun canRoll(): Boolean = rollCount < MAX_ROLLS

    /**
     * Increments the roll count by one if rolling is still allowed.
     */
    fun incrementRollCount() {
        if (canRoll()) {
            rollCount++
        }
    }

    /**
     * Sets the score and category for this round.
     * Throws IllegalStateException if the score was already set before.
     *
     * @param category The scoring category selected by the player.
     * @param value The score value to assign for the selected category.
     */
    fun setScore(category: ScoreOption, value: Int) {
        if (isScored) throw IllegalStateException("Score already set for this round.")
        selectedCategory = category
        score = value
        isScored = true
    }

    /**
     * Restores the roll count to a given value, ensuring it is within valid bounds.
     *
     * @param count The roll count to restore (clamped between 0 and MAX_ROLLS).
     */
    fun restoreRollCount(count: Int) {
        rollCount = count.coerceIn(0, MAX_ROLLS)
    }
}



