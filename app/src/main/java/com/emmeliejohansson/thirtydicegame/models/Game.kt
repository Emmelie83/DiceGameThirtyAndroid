package com.emmeliejohansson.thirtydicegame.models

class Game {
    private val rounds = mutableListOf<Round>()
    var currentRound: Round = Round()
        private set

    val isGameOver: Boolean
        get() = rounds.size >= 10

    private fun roundsPlayed(): Int = rounds.size

    fun getCurrentRoundNumber(): Int = roundsPlayed() + 1

    fun canRoll(): Boolean = currentRound.canRoll()

    fun isFirstRoll(): Boolean = currentRound.rollCount == 0

    fun incrementRollCount() {
        currentRound.incrementRollCount()
    }

    fun resetRound() {
        rounds.add(currentRound)
        currentRound = Round()
    }
}

