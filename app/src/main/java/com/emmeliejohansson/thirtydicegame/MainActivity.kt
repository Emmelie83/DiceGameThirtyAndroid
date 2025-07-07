package com.emmeliejohansson.thirtydicegame

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityMainBinding
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.services.DiceStore
import com.emmeliejohansson.thirtydicegame.managers.CategoryManager

class MainActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var diceImages: List<ImageView>
    private lateinit var categoryManager: CategoryManager
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val COLOR_WHITE = "white"
        private const val COLOR_RED = "red"
        private const val COLOR_GRAY = "gray"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryManager = CategoryManager(this, binding.categoryToggleGroup, binding.nextRoundButton)

        game = Game()
        game.fillDiceStore()
        updateRoundText()
        updateRollsLeftText()

        diceImages = listOf(
            findViewById(R.id.die1),
            findViewById(R.id.die2),
            findViewById(R.id.die3),
            findViewById(R.id.die4),
            findViewById(R.id.die5),
            findViewById(R.id.die6)
        )

        binding.nextRoundButton.isEnabled = false

        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (!game.isEndOfRound()) {
                    val die = DiceStore.getDieById(index + 1)
                    die?.toggleIsSelected()
                    updateDiceImages()
                }
            }
        }

        binding.rollButton.setOnClickListener {
            if (!game.isGameOver()) {
                game.rollDice()
                categoryManager.enableAllButtons()
                updateDiceImages()
                updateRollsLeftText()
                updateInstructionText()
            } else {
                Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
            }
        }


        binding.nextRoundButton.setOnClickListener {
            val selectedCategory = categoryManager.getSelectedCategory()
            if (selectedCategory != null) {
                game.useScoringCategory(selectedCategory)
                categoryManager.removeCategory(selectedCategory)
                categoryManager.disableAllButtons()
                game.resetForNextRound()
                updateDiceImages()
                updateRoundText()
                updateRollsLeftText()
                updateInstructionText()
            }
        }
    }

    private fun updateRollsLeftText() {
        binding.rollsLeftText.text = getString(R.string.rolls_left, (3 - game.rollCount))
    }

    private fun updateInstructionText() {
        binding.instructionText.text = when {
            game.rollCount == 0 -> getString(R.string.instruction_text_start)
            game.rollCount == 1 -> getString(R.string.instruction_text_roll)
            game.rollCount == 2 -> getString(R.string.instruction_text_roll)
            else -> getString(R.string.instruction_text_end_of_round)
        }
    }

    private fun updateRoundText() {
        binding.roundText.text = getString(R.string.round_text, game.round)
    }

    private fun updateDiceImages() {
        DiceStore.getAllDice().forEachIndexed { index, die ->
            val imageRes = when {
                !die.hasBeenRolled -> getDieImageRes(die.value, COLOR_WHITE)
                game.isEndOfRound() -> getDieImageRes(die.value, COLOR_GRAY)
                die.isSelected -> getDieImageRes(die.value, COLOR_RED)
                else -> getDieImageRes(die.value, COLOR_WHITE)
            }
            diceImages[index].setImageResource(imageRes)
        }
    }

    private fun getDieImageRes(value: Int, color: String = "white"): Int {
        val name = when (color) {
            "red" -> "red_die_$value"
            "gray" -> "gray_die_$value"
            else -> "die_$value"
        }
        return resources.getIdentifier(name, "drawable", packageName)
    }
}
