package com.emmeliejohansson.thirtydicegame.repository

import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.Game

class DefaultDiceRepository(
    private val game: Game
) : DiceRepository {

    override fun getAllDice(): List<Die> = game.currentRound.dice

    override fun getSelectedDice(): List<Die> = game.currentRound.dice.filter { it.isSelected }

    override fun getDieById(id: Int): Die? = game.currentRound.dice.find { it.id == id }

    override fun deselectAllDice() {
        game.currentRound.dice.forEach { it.isSelected = false }
    }

    override fun resetDice() {
        game.currentRound.dice.forEach { it.reset() }
    }
}

