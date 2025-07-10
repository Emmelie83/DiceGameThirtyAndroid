package com.emmeliejohansson.thirtydicegame.managers

import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.repository.DiceRepository

class GameManager(private val game: Game, private val diceRepository: DiceRepository) {
    val dice: List<Die> get() = diceRepository.getAllDice()
    val rollCount: Int get() = game.currentRound.rollCount
    val maxRolls: Int get() = game.currentRound.maxRolls
    val roundNumber: Int get() = game.getCurrentRoundNumber()
    val isGameOver: Boolean get() = game.isGameOver

    fun canRoll(): Boolean = game.canRoll()
    fun hasNoMoreRolls(): Boolean = rollCount >= maxRolls
    fun isEndOfRound(): Boolean = !canRoll()
    fun incrementRollCount() = game.incrementRollCount()
    fun resetRound() = game.resetRound()

    fun rollDice() {
        if (!canRoll()) return
        if (rollCount == 0) {
            dice.forEach { it.roll() }
        } else {
            diceRepository.getSelectedDice().forEach { it.roll() }
        }
        diceRepository.deselectAllDice()
        incrementRollCount()
    }

    fun toggleDieSelected(index: Int) {
        diceRepository.getDieById(index + 1)?.toggleIsSelected()
    }

    fun getSelectedDice(): List<Die> = diceRepository.getSelectedDice()

    fun resetDice() {
        diceRepository.deselectAllDice()
        diceRepository.resetDice()
    }
}
