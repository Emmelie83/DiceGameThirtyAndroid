package com.emmeliejohansson.thirtydicegame.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityResultBinding
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.emmeliejohansson.thirtydicegame.ui.utils.populateScoreViews

/**
 * Activity shown after the game ends.
 * Displays the user's scores and provides a button to restart the game.
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val scoreMap = extractScoreMapFromIntent()
        val totalScore = intent.getIntExtra("total", 0)

        setupScoreView(scoreMap, totalScore)
        setupPlayAgainButton()
    }

    /**
     * Extracts and converts the score map from the intent extras.
     * Returns a map with ScoreOption keys and integer values.
     */
    private fun extractScoreMapFromIntent(): HashMap<ScoreOption, Int> {
        val rawMap: HashMap<String, Int>? =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("scores", HashMap::class.java) as? HashMap<String, Int>
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("scores") as? HashMap<String, Int>
            }

        val scoreMap = HashMap<ScoreOption, Int>()
        rawMap?.forEach { (key, value) ->
            try {
                val scoreOption = ScoreOption.valueOf(key)
                scoreMap[scoreOption] = value
            } catch (_: IllegalArgumentException) {
                // Ignore keys that don’t match valid ScoreOption values
            }
        }
        return scoreMap
    }

    /**
     * Populates the score UI elements with score data.
     *
     * @param scoreMap Map of score categories and their respective values
     * @param totalScore The total score achieved in the game
     */
    private fun setupScoreView(scoreMap: HashMap<ScoreOption, Int>, totalScore: Int) {
        populateScoreViews(binding, scoreMap, totalScore)
    }

    /**
     * Sets up the Play Again button to start a new game.
     */
    private fun setupPlayAgainButton() {
        binding.playAgainButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }
    }
}
