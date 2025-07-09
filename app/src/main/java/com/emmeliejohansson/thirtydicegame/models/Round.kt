package com.emmeliejohansson.thirtydicegame.models

class Round {
    val dice: List<Die> = List(6) { Die(it + 1) }
    var rollCount = 0
    val maxRolls = 3

    fun canRoll(): Boolean = rollCount < maxRolls

    fun incrementRollCount() {
        if (canRoll()) rollCount++
    }

    fun resetDice() {
        dice.forEach { it.reset() }
        rollCount = 0
    }
}
