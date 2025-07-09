package com.emmeliejohansson.thirtydicegame.models

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

                val total = selectedDice.sum()
                if (total % target != 0) {
                    Result.failure(IllegalArgumentException("Sum must be divisible by $target."))
                } else {
                    Result.success(total)
                }
            }
        }
    }
}