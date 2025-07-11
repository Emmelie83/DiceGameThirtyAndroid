package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents a single round in the dice game.
 *
 * A round consists of up to [MAX_ROLLS] dice rolls, a fixed set of six dice,
 * and one optional scoring category once the round is completed.
 */
class Round {

    /**
     * A fixed list of six dice, each initialized with a unique ID from 1 to 6.
     */
    val dice: List<Die> = List(6) { Die(it + 1) }

    /**
     * Tracks how many times the player has rolled in this round.
     */
    var rollCount = 0
        private set

    /**
     * The score category selected by the player during this round.
     */
    private var selectedCategory: ScoreOption? = null

    /**
     * Indicates whether the round has been scored.
     */
    var isScored: Boolean = false

    companion object {
        /** Maximum number of rolls allowed per round. */
        const val MAX_ROLLS = 3
    }

    /**
     * Checks if another roll is allowed in this round.
     *
     * @return true if the player has remaining rolls, false otherwise.
     */
    fun canRoll(): Boolean = rollCount < MAX_ROLLS

    /**
     * Increments the roll count, if rolling is still allowed.
     */
    fun incrementRollCount() {
        if (canRoll()) {
            rollCount++
        }
    }

    /**
     * Resets the round to its initial state so it can be reused.
     *
     * This resets the dice, clears the selected category, and sets the score status to false.
     */
    fun reset() {
        rollCount = 0
        selectedCategory = null
        isScored = false
        dice.forEach { it.reset() }
    }

    // TODO (optional): Consider exposing rollCount as read-only to external classes,
    // and providing a public method to safely advance or limit state changes.
}
