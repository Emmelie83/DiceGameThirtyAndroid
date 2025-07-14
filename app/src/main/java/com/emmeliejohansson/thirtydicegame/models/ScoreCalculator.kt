package com.emmeliejohansson.thirtydicegame.models

/**
 * Utility object responsible for calculating scores in the Thirty Dice Game.
 * Handles "LOW" category separately, and for numbered categories (4–12),
 * it finds non-overlapping subsets of dice that sum to the category value.
 */
object ScoreCalculator {

    /**
     * Calculates the score based on the selected scoring category and dice.
     *
     * @param category The selected [ScoreOption] category.
     * @param selectedDice A list of integers representing the face values of selected dice.
     * @return A [Result] wrapping the score or an error if invalid input or configuration.
     */
    fun calculateScore(category: ScoreOption, selectedDice: List<Int>): Result<Int> {
        if (selectedDice.isEmpty()) {
            return Result.failure(IllegalArgumentException("No dice selected."))
        }

        return when (category) {
            ScoreOption.LOW -> calculateLowScore(selectedDice)
            else -> calculateCategoryScore(category, selectedDice)
        }
    }

    /**
     * Calculates score for the "LOW" category.
     * Only dice with values ≤ 3 are allowed.
     */
    private fun calculateLowScore(dice: List<Int>): Result<Int> {
        return if (dice.any { it > 3 }) {
            Result.failure(IllegalArgumentException("Only dice with value ≤ 3 are allowed in 'Low'."))
        } else {
            Result.success(dice.sum())
        }
    }

    /**
     * Calculates score for categories 4–12.
     * Attempts to find non-overlapping subsets of dice that each sum to the category's value.
     */
    private fun calculateCategoryScore(category: ScoreOption, dice: List<Int>): Result<Int> {
        val target = category.label.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Invalid scoring category: ${category.label}"))

        val subsets = findExactCoverSubsets(dice, target)
        return if (subsets == null) {
            Result.failure(IllegalArgumentException("Dice cannot be grouped into non-overlapping sets summing to $target."))
        } else {
            Result.success(subsets.flatten().sum())
        }
    }

    /**
     * Attempts to find a collection of non-overlapping subsets from [dice],
     * where each subset sums exactly to [target] and collectively covers all dice.
     *
     * Uses a recursive backtracking approach.
     *
     * @return A list of such subsets or null if no valid grouping exists.
     */
    private fun findExactCoverSubsets(dice: List<Int>, target: Int): List<List<Int>>? {
        val validSubsets = mutableListOf<Pair<List<Int>, Set<Int>>>()

        // Step 1: Generate all possible subsets that sum to target
        fun generateSubsets(start: Int, current: MutableList<Int>, indices: MutableSet<Int>, currentSum: Int) {
            if (currentSum == target) {
                validSubsets.add(current.toList() to indices.toSet())
                return
            }
            if (currentSum > target) return

            for (i in start until dice.size) {
                if (i in indices) continue // avoid duplicate index usage
                current.add(dice[i])
                indices.add(i)
                generateSubsets(i + 1, current, indices, currentSum + dice[i])
                current.removeAt(current.lastIndex)
                indices.remove(i)
            }
        }

        generateSubsets(0, mutableListOf(), mutableSetOf(), 0)

        // Step 2: Recursively search for non-overlapping subset combinations that cover all dice
        fun search(
            usedIndices: Set<Int>,
            selectedSubsets: List<List<Int>>,
            remaining: List<Pair<List<Int>, Set<Int>>>
        ): List<List<Int>>? {
            if (usedIndices.size == dice.size) return selectedSubsets
            if (remaining.isEmpty()) return null

            for ((subset, indices) in remaining) {
                if (indices.any { it in usedIndices }) continue
                val nextUsed = usedIndices + indices
                val nextSubsets = selectedSubsets + listOf(subset)
                val result = search(nextUsed, nextSubsets, remaining - (subset to indices))
                if (result != null) return result
            }

            return null
        }

        return search(emptySet(), emptyList(), validSubsets)
    }
}
