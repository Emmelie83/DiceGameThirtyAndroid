package com.emmeliejohansson.thirtydicegame.repository

import com.emmeliejohansson.thirtydicegame.models.Die

interface DiceRepository {
    fun getAllDice(): List<Die>
    fun getSelectedDice(): List<Die>
    fun getDieById(id: Int): Die?
    fun deselectAllDice()
    fun resetDice()
}