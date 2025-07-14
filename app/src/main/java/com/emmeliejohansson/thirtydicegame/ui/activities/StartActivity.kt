package com.emmeliejohansson.thirtydicegame.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityStartBinding

/**
 * StartActivity serves as the entry point to the game.
 * It displays a welcome screen with a "Start" button to begin the game.
 */
class StartActivity : AppCompatActivity() {

    // View binding for accessing layout elements
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize listeners for UI elements
        setupStartButton()
    }

    /**
     * Sets up the Start button to navigate to the GameActivity.
     */
    private fun setupStartButton() {
        binding.startButton.setOnClickListener {
            launchGame()
        }
    }

    /**
     * Launches the main game screen (GameActivity).
     */
    private fun launchGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}
