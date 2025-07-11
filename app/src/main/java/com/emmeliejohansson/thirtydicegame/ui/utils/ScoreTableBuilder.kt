package com.emmeliejohansson.thirtydicegame.ui.utils
import com.emmeliejohansson.thirtydicegame.databinding.ActivityResultBinding
import com.emmeliejohansson.thirtydicegame.models.ScoreOption

/**
 * Utility object to build and populate the score table UI.
 */
fun populateScoreViews(binding: ActivityResultBinding, scoreMap: Map<ScoreOption, Int>, total: Int) {
    binding.scoreLow.text = scoreMap[ScoreOption.LOW]?.toString() ?: "0"
    binding.score4.text = scoreMap[ScoreOption.FOUR]?.toString() ?: "0"
    binding.score5.text = scoreMap[ScoreOption.FIVE]?.toString() ?: "0"
    binding.score6.text = scoreMap[ScoreOption.SIX]?.toString() ?: "0"
    binding.score7.text = scoreMap[ScoreOption.SEVEN]?.toString() ?: "0"
    binding.score8.text = scoreMap[ScoreOption.EIGHT]?.toString() ?: "0"
    binding.score9.text = scoreMap[ScoreOption.NINE]?.toString() ?: "0"
    binding.score10.text = scoreMap[ScoreOption.TEN]?.toString() ?: "0"
    binding.score11.text = scoreMap[ScoreOption.ELEVEN]?.toString() ?: "0"
    binding.score12.text = scoreMap[ScoreOption.TWELVE]?.toString() ?: "0"
    binding.scoreTotal.text = total.toString()

}
