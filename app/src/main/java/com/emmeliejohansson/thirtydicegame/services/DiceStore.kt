package com.emmeliejohansson.thirtydicegame.services

import com.emmeliejohansson.thirtydicegame.models.Die

class DiceStore {
    companion object {
        private val diceList = mutableListOf<Die>()
        fun addDie(die: Die) {
            diceList.add(die)
        }

        fun getDieById(id: Int): Die? {
            return diceList.find { it.id == id }
        }

        fun resetAllDiceToActive() {
            diceList.forEach {
                it.isRollable = true
            }
        }

        fun getAllDice(): List<Die> = diceList.toList()

        fun getActiveDice(): List<Die> {
            return diceList.filter { it.isRollable }
        }

        fun getInactiveDice(): List<Die> {
            return diceList.filter { !it.isRollable }
        }

        fun resetDice() {
            diceList.clear()
        }

    }
}