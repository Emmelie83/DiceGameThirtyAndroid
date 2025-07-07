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

        fun getAllDice(): List<Die> = diceList.toList()

        fun getSelectedDice(): List<Die> {
            return diceList.filter { it.isSelected }
        }

        fun deselectAllDice() {
            diceList.forEach { it.isSelected = false }
        }

    }
}