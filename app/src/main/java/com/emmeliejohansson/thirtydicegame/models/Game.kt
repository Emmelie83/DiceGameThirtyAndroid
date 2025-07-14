package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents the overall game state, managing rounds, dice, and progression.
 */
class Game {

    companion object {
        /**
         * The maximum number of rounds in a game.
         */
        const val MAX_ROUNDS = 3

        /**
         * The maximum number of rolls allowed per round.
         */
        const val ROLLS_PER_ROUND = 3
    }

    /**
     * Tracks the current round number (1-based).
     */
    private var currentRoundNumber = 1

    /**
     * List holding all completed rounds.
     */
    private val rounds = mutableListOf<Round>()

    /**
     * The dice currently in use for the game.
     * Mutable to allow updates during the game.
     */
    var dice: MutableList<Die> = mutableListOf()

    /**
     * The active round currently being played.
     * Only one active round at a time.
     */
    var currentRound: Round = Round()
        private set

    /**
     * Returns true if the game has finished all rounds.
     * Game is over if the current round number exceeds the max rounds.
     */
    val isGameOver: Boolean
        get() = currentRoundNumber > MAX_ROUNDS

    /**
     * Sets the current round number to a specific value.
     *
     * @param number The round number to set.
     */
    fun setCurrentRoundNumber(number: Int) {
        currentRoundNumber = number
    }

    /**
     * Returns how many rounds have been completed.
     */
    private fun getRoundsPlayed(): Int = rounds.size

    /**
     * Returns the current round number (1-based).
     */
    fun getCurrentRoundNumber(): Int = currentRoundNumber

    /**
     * Returns whether another roll is allowed in the current round.
     */
    fun canRoll(): Boolean = currentRound.canRoll()

    /**
     * Records a score for the specified category in the current round.
     *
     * @param category The scoring category selected by the player.
     * @param score The score value assigned for the category.
     */
    fun scoreCategory(category: ScoreOption, score: Int) {
        currentRound.setScore(category, score)
    }

    /**
     * Finalizes the current round and prepares for the next one.
     * Adds the current round to the list of completed rounds,
     * creates a new Round instance, and increments the round number.
     */
    fun resetRound() {
        rounds.add(currentRound)
        currentRound = Round()
        currentRoundNumber++
    }
}
