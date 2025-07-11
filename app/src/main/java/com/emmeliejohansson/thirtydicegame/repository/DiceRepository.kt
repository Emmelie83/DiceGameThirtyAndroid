package com.emmeliejohansson.thirtydicegame.repository

import com.emmeliejohansson.thirtydicegame.models.Die

/**
 * Interface that defines operations for accessing and modifying dice state.
 * This abstraction allows flexibility in how dice are managed (e.g., for testing or alternate implementations).
 */
interface DiceRepository {

    /**
     * Retrieves all dice in the current round.
     *
     * @return A list of all [Die] objects.
     */
    fun getAllDice(): List<Die>

    /**
     * Retrieves only the dice that are currently selected by the player.
     *
     * @return A list of selected [Die] objects.
     */
    fun getSelectedDice(): List<Die>

    /**
     * Retrieves a die by its unique identifier (typically 1-based index).
     *
     * @param id The ID of the die.
     * @return The [Die] object with the given ID, or null if not found.
     */
    fun getDieById(id: Int): Die?

    /**
     * Deselects all dice, clearing any selection state.
     * Commonly used after rolling or before a new round.
     */
    fun deselectAllDice()

    /**
     * Resets all dice to their initial state (e.g., value and selection).
     * Used when starting a new round or restarting the game.
     */
    fun resetDice()
}
