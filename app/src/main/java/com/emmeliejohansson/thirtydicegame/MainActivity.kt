package com.emmeliejohansson.thirtydicegame

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityMainBinding
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.services.DiceStore
import com.emmeliejohansson.thirtydicegame.managers.CategoryManager
import com.emmeliejohansson.thirtydicegame.models.DieColor

class MainActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var diceImages: List<ImageView>
    private lateinit var categoryManager: CategoryManager
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val COLOR_WHITE = "white"
        private const val COLOR_RED = "red"
        private const val COLOR_GRAY = "gray"
        private const val MAX_ROLLS = 3
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
            binding.die1, binding.die2, binding.die3,
            binding.die4, binding.die5, binding.die6
        )

        binding.rollButton.isEnabled = true
        binding.nextRoundButton.isEnabled = false


        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (!game.isEndOfRound() && (game.rollCount != 0)) {
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
                updateUI()
            } else {
                Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
            }
        }


        binding.nextRoundButton.setOnClickListener {
            val selectedCategory = categoryManager.getSelectedCategory()
            if (selectedCategory != null) {
                onNextRound(selectedCategory)
            }
        }
    }

    private fun updateUI() {
        updateRollsLeftText()
        updateInstructionText()
        updateRoundText()
        updateDiceImages()
    }

    private fun updateRollsLeftText() {
        binding.rollsLeftText.text = getString(R.string.rolls_left, (MAX_ROLLS - game.rollCount))
    }

    private fun updateInstructionText() {
        binding.instructionText.text = when (game.rollCount) {
            0 -> getString(R.string.instruction_text_start)
            in 1..2 -> getString(R.string.instruction_text_roll)
            else -> getString(R.string.instruction_text_end_of_round)
        }
    }

    private fun updateRoundText() {
        binding.roundText.text = getString(R.string.round_text, game.round)
    }

    private fun updateDiceImages() {
        DiceStore.getAllDice().forEachIndexed { index, die ->
            val imageRes = when {
                !die.hasBeenRolled -> getDieImageRes(die.value, DieColor.WHITE)
                game.isEndOfRound() -> getDieImageRes(die.value, DieColor.GRAY)
                die.isSelected -> getDieImageRes(die.value, DieColor.RED)
                else -> getDieImageRes(die.value, DieColor.WHITE)
            }
            diceImages[index].setImageResource(imageRes)
        }

        val isDiceSelected = DiceStore.getSelectedDice().isNotEmpty()
        binding.rollButton.isEnabled = (game.rollCount == 0) || (isDiceSelected && !game.isEndOfRound())
    }

    private fun onNextRound(selectedCategory: String) {
        game.useScoringCategory(selectedCategory)
        categoryManager.removeCategory(selectedCategory)
        categoryManager.disableAllButtons()
        game.resetForNextRound()
        updateUI()
    }

    private fun getDieImageRes(value: Int, color: DieColor): Int {
        return when (color) {
            DieColor.RED -> when (value) {
                1 -> R.drawable.red_die_1
                2 -> R.drawable.red_die_2
                3 -> R.drawable.red_die_3
                4 -> R.drawable.red_die_4
                5 -> R.drawable.red_die_5
                6 -> R.drawable.red_die_6
                else -> error("Invalid die value: $value")
            }
            DieColor.GRAY -> when (value) {
                1 -> R.drawable.gray_die_1
                2 -> R.drawable.gray_die_2
                3 -> R.drawable.gray_die_3
                4 -> R.drawable.gray_die_4
                5 -> R.drawable.gray_die_5
                6 -> R.drawable.gray_die_6
                else -> error("Invalid die value: $value")
            }
            DieColor.WHITE -> when (value) {
                1 -> R.drawable.die_1
                2 -> R.drawable.die_2
                3 -> R.drawable.die_3
                4 -> R.drawable.die_4
                5 -> R.drawable.die_5
                6 -> R.drawable.die_6
                else -> error("Invalid die value: $value")
            }
        }
    }
}
