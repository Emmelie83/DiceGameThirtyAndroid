package com.emmeliejohansson.thirtydicegame.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityResultBinding
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.emmeliejohansson.thirtydicegame.ui.utils.populateScoreViews

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rawMap: HashMap<String, Int>? =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("scores", HashMap::class.java) as? HashMap<String, Int>
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("scores") as? HashMap<String, Int>
            }

        val scoreMap: HashMap<ScoreOption, Int> = HashMap()
        rawMap?.forEach { (key, value) ->
            try {
                val scoreOption = ScoreOption.valueOf(key)
                scoreMap[scoreOption] = value
            } catch (_: IllegalArgumentException) {
                // Ignore invalid keys
            }
        }

        val totalScore = intent.getIntExtra("total", 0)
        populateScoreViews(binding, scoreMap, totalScore)

        binding.playAgainButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }
    }
}
