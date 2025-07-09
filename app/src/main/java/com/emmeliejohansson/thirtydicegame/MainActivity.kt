package com.emmeliejohansson.thirtydicegame

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityMainBinding
import com.emmeliejohansson.thirtydicegame.services.DiceStore
import com.emmeliejohansson.thirtydicegame.managers.CategoryManager
import com.emmeliejohansson.thirtydicegame.models.DieColor

class MainActivity : AppCompatActivity() {

    private val thirtyDiceGameViewModel: ThirtyDiceGameViewModel by viewModels()

    private val game get() = thirtyDiceGameViewModel.game
    private lateinit var diceImages: List<ImageView>
    private lateinit var categoryManager: CategoryManager
    private lateinit var binding: ActivityMainBinding


    companion object {
        private const val MAX_ROLLS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryManager = CategoryManager(this, binding.categoryToggleGroup, binding.nextRoundButton)
        diceImages = listOf(
            binding.die1, binding.die2, binding.die3,
            binding.die4, binding.die5, binding.die6
        )

        binding.rollButton.isEnabled = true
        binding.nextRoundButton.isEnabled = false

        // Observe ViewModel
        thirtyDiceGameViewModel.remainingCategories.observe(this) { categories ->
            categoryManager.setCategories(categories)
        }

        // Dice click listeners
        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (game.rollCount >  0) {
                    val die = DiceStore.getDieById(index + 1)
                    die?.toggleIsSelected()
                    updateDiceImages()
                }
            }
        }

        // Roll button
        binding.rollButton.setOnClickListener {
            if (!game.isGameOver()) {
                thirtyDiceGameViewModel.rollDice()
                categoryManager.enableAllButtons()
                updateUI()
            }
        }

        // Next round
        binding.nextRoundButton.setOnClickListener {
            val selectedCategory = categoryManager.getSelectedCategory()
            if (selectedCategory != null) {
                onNextRound(selectedCategory)
            }
        }
        updateUI()
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
                !die.hasBeenRolled -> getDieImageRes(die.value, DieColor.GRAY)
                die.isSelected -> getDieImageRes(die.value, DieColor.RED)
                else -> getDieImageRes(die.value, DieColor.WHITE)
            }
            diceImages[index].setImageResource(imageRes)
        }

        val isDiceSelected = DiceStore.getSelectedDice().isNotEmpty()
        binding.rollButton.isEnabled = (game.rollCount == 0) || (isDiceSelected && !game.isEndOfRound())
    }

    private fun onNextRound(selectedCategory: String) {
        val selectedDice = DiceStore.getSelectedDice().map { it.value }

        if (selectedDice.isEmpty()) {
            Toast.makeText(this, "Please select dice for scoring.", Toast.LENGTH_SHORT).show()
            return
        }

        val score = when (selectedCategory) {
            "Low" -> {
                if (selectedDice.any { it > 3 }) {
                    Toast.makeText(this, "Only dice with value 3 or lower allowed in 'Low'", Toast.LENGTH_SHORT).show()
                    return
                }
                selectedDice.sum()
            }

            else -> {
                val target = selectedCategory.toIntOrNull()
                if (target == null) {
                    Toast.makeText(this, "Invalid category selected.", Toast.LENGTH_SHORT).show()
                    return
                }

                val total = selectedDice.sum()
                if (total % target != 0) {
                    Toast.makeText(this, "Selected dice do not form valid groups of $target.", Toast.LENGTH_SHORT).show()
                    return
                }

                total
            }
        }

        // Register score
        thirtyDiceGameViewModel.registerScore(selectedCategory, score)
        categoryManager.disableAllButtons()

        // Game over check
        if (game.isGameOver()) {
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("scores", HashMap(thirtyDiceGameViewModel.scoreMap))
                putExtra("total", thirtyDiceGameViewModel.getTotalScore())
            }
            startActivity(intent)
            finish() // Optional: prevent going back to this activity
        } else {
            // Prepare next round
            thirtyDiceGameViewModel.resetForNextRound()
            updateUI()
        }
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
