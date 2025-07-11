package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents the overall game state.
 */
class Game {

    companion object {
        const val MAX_ROUNDS = 10
        const val ROLLS_PER_ROUND = 3
    }

    private var currentRoundNumber = 1

    /** List of completed rounds */
    private val rounds = mutableListOf<Round>()

    /** The current active round */
    var currentRound: Round = Round()
        private set

    /** True if the maximum number of rounds has been played */
    val isGameOver: Boolean
        get() = getRoundsPlayed() >= MAX_ROUNDS

    /** Returns how many rounds have been completed */
    private fun getRoundsPlayed(): Int = rounds.size

    /** Returns the 1-based index of the current round */
    fun getCurrentRoundNumber(): Int = currentRoundNumber

    /** Returns true if another roll is allowed in this round */
    fun canRoll(): Boolean = currentRound.canRoll()


    fun scoreCategory(category: ScoreOption, score: Int) {
        currentRound.setScore(category, score)
    }

    /**
     * Finalizes the current round and starts a new one.
     * This should be called after scoring is applied.
     */
    fun resetRound() {
        rounds.add(currentRound)
        currentRound = Round()
        currentRoundNumber++
    }

}
