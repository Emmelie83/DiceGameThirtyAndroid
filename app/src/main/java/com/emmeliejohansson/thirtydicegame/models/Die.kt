package com.emmeliejohansson.thirtydicegame.models

import kotlin.random.Random

/**
 * Represents a single die in the game.
 *
 * @property id The value of the die when it is created.
 */
class Die(val id: Int) {

    /** The current face value of the die (1–6 when rolled). */
    var value: Int = id

    /** Whether the die is selected by the player (for re-roll or scoring). */
    var isSelected: Boolean = false

    /** Whether the die has been rolled in the current round. */
    var hasBeenRolled: Boolean = false

    /**
     * Rolls the die, assigning it a random value between 1 and 6,
     * and marks it as rolled.
     */
    fun roll() {
        value = Random.nextInt(1, 7)
        hasBeenRolled = true
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
     * Resets the die to its original state.
     * Useful when starting a new game or round.
     */
    fun reset() {
        value = id
        isSelected = false
        hasBeenRolled = false
    }
}
