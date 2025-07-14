package com.emmeliejohansson.thirtydicegame.models

import kotlin.random.Random

/**
 * Represents a single die in the game.
 *
 * @property initialValue The initial face value of the die when created.
 */
class Die(val initialValue: Int) {

    /** The current face value of the die (1–6 when rolled). */
    var value: Int = initialValue
        private set

    /** Whether the die is selected by the player (for re-roll or scoring). */
    var isSelected: Boolean = false
        private set

    /** Whether the die has been rolled in the current round. */
    var hasBeenRolled: Boolean = false
        private set

    /**
     * Rolls the die, assigning it a random value between 1 and 6,
     * and sets its state after the roll.
     */
    fun roll() {
        value = Random.nextInt(1, 7)
        updateStateAfterRoll()
    }

    /**
     * Resets selection and marks the die as rolled after rolling.
     */
    private fun updateStateAfterRoll() {
        hasBeenRolled = true
        isSelected = false
    }

    /**
     * Toggles the selected state of the die.
     * Only works if the die has already been rolled.
     */
    fun toggleIsSelected() {
        if (hasBeenRolled) {
            isSelected = !isSelected
        }
    }

    /**
     * Deselects the die (used when resetting or preparing for a new roll).
     */
    fun deselect() {
        isSelected = false
    }

    /**
     * Resets the die to its original state.
     * Useful when starting a new game or round.
     */
    fun reset() {
        value = initialValue
        isSelected = false
        hasBeenRolled = false
    }

    /**
     * Restores the die's state (used when loading saved games).
     */
    fun restoreState(value: Int, isSelected: Boolean, hasBeenRolled: Boolean) {
        this.value = value
        this.isSelected = isSelected
        this.hasBeenRolled = hasBeenRolled
    }
}
