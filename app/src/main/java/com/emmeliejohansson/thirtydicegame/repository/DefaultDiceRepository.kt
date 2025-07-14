package com.emmeliejohansson.thirtydicegame.repository

import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.Game

/**
 * Default implementation of the DiceRepository interface.
 * Provides access and operations on dice for the current round of the game.
 *
 * @param game The game instance containing the current round and dice.
 */
class DefaultDiceRepository(
    private val game: Game
) : DiceRepository {

    /**
     * Returns all dice from the current round.
     */
    override fun getAllDice(): List<Die> = game.currentRound.dice

    /**
     * Returns only the dice that are currently selected.
     */
    override fun getSelectedDice(): List<Die> = game.currentRound.dice.filter { it.isSelected }

    /**
     * Retrieves a single die by its ID (1-based index).
     *
     * @param id The ID of the die to retrieve.
     * @return The matching Die object, or null if not found.
     */
    override fun getDieById(id: Int): Die? = game.currentRound.dice.find { it.initialValue == id }

    /**
     * Deselects all dice, typically after a roll or when resetting state.
     */
    override fun deselectAllDice() {
        game.currentRound.dice.forEach { it.deselect() }
    }

    /**
     * Resets all dice to their initial state.
     */
    override fun resetDice() {
        game.currentRound.dice.forEach { it.reset() }
    }
}
