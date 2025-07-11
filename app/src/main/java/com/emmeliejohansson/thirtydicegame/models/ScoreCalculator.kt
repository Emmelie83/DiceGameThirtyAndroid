package com.emmeliejohansson.thirtydicegame.models

/**
 * Utility object to calculate scores based on selected dice and score category.
 */
object ScoreCalculator {

    fun calculateScore(category: ScoreOption, selectedDice: List<Int>): Result<Int> {
        if (selectedDice.isEmpty()) {
            return Result.failure(IllegalArgumentException("No dice selected."))
        }

        return when (category) {
            ScoreOption.LOW -> {
                if (selectedDice.any { it > 3 }) {
                    Result.failure(IllegalArgumentException("Only dice ≤ 3 allowed in 'Low'."))
                } else {
                    Result.success(selectedDice.sum())
                }
            }

            else -> {
                val target = category.label.toIntOrNull()
                    ?: return Result.failure(IllegalArgumentException("Invalid category."))

                val result = findExactCoverSubsets(selectedDice, target)

                if (result == null) {
                    Result.failure(IllegalArgumentException("Selected dice cannot be partitioned into subsets summing to $target."))
                } else {
                    Result.success(result.flatten().sum())
                }
            }
        }
    }

    private fun findExactCoverSubsets(dice: List<Int>, target: Int): List<List<Int>>? {
        val allSubsets = mutableListOf<Pair<List<Int>, Set<Int>>>()

        // Generate all subsets that sum to target, with their indices
        fun backtrack(start: Int, current: MutableList<Int>, indices: MutableSet<Int>, sum: Int) {
            if (sum == target) {
                allSubsets.add(current.toList() to indices.toSet())
                return
            }
            if (sum > target) return

            for (i in start until dice.size) {
                if (i in indices) continue
                current.add(dice[i])
                indices.add(i)
                backtrack(i + 1, current, indices, sum + dice[i])
                current.removeAt(current.lastIndex)
                indices.remove(i)
            }
        }

        backtrack(0, mutableListOf(), mutableSetOf(), 0)

        // Try combinations of non-overlapping subsets that cover all dice
        fun search(
            usedIndices: Set<Int>,
            selectedSubsets: List<List<Int>>,
            remaining: List<Pair<List<Int>, Set<Int>>>
        ): List<List<Int>>? {
            if (usedIndices.size == dice.size) return selectedSubsets
            if (remaining.isEmpty()) return null

            for ((subset, indices) in remaining) {
                if (indices.any { it in usedIndices }) continue
                val newUsed = usedIndices + indices
                val newSubsets = selectedSubsets + listOf(subset)
                val result = search(newUsed, newSubsets, remaining - (subset to indices))
                if (result != null) return result
            }
            return null
        }

        return search(emptySet(), emptyList(), allSubsets)
    }
}


