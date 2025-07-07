package com.emmeliejohansson.thirtydicegame.models

import com.emmeliejohansson.thirtydicegame.services.DiceStore

class Game {
    var rollCount = 0
    var round = 1
    val scoringCategories = mutableListOf<Any>("Low", 4, 5, 6, 7, 8, 9, 10, 11, 12)

    fun fillDiceStore(numberOfDice: Int = 6) {
        for (i in 1..numberOfDice) {
            DiceStore.addDie(Die(i))
        }
    }

    fun rollDice() {
        if (rollCount == 0) {
            rollAllDice()
        } else {
            rollActiveDice()
        }
        rollCount++
    }



    private fun rollActiveDice() {
        if (rollCount < 3) {
            DiceStore.getActiveDice().forEach { it.roll() }
        }
    }

    private fun rollAllDice() {
            DiceStore.getAllDice().forEach { it.roll() }
    }

    fun isEndOfRound(): Boolean {
        return rollCount == 3
    }

    fun isGameOver(): Boolean = round > 10

    fun resetForNextRound() {
        DiceStore.getAllDice().forEach {
            it.isRollable = true
            it.value = it.id
        }
        rollCount = 0
        round++
    }


    fun useScoringCategory(category: Any) {
        scoringCategories.remove(category)
    }
}