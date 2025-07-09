package com.emmeliejohansson.thirtydicegame

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.databinding.ActivityMainBinding
import com.emmeliejohansson.thirtydicegame.managers.ScoreCategoryManager
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.DieColor
import com.emmeliejohansson.thirtydicegame.models.ScoreOption

class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var diceImages: List<ImageView>
    private lateinit var categoryManager: ScoreCategoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryManager = ScoreCategoryManager(this, binding.categoryToggleGroup, binding.nextRoundButton)

        diceImages = listOf(
            binding.die1, binding.die2, binding.die3,
            binding.die4, binding.die5, binding.die6
        )

        updateScoreCategoryButtons()
        setupCategorySelection()
        setupDiceClickListeners()
        setupButtonListeners()
        updateUI()
    }

    private fun updateScoreCategoryButtons() {
        if (gameViewModel.areScoreButtonsEnabled) {
            categoryManager.enableAllButtons()
        } else {
            categoryManager.disableAllButtons()
        }
    }

    private fun setupCategorySelection() {
        categoryManager.setCategories(gameViewModel.remainingCategories)
        setupCategorySelectedListener()
        gameViewModel.selectedCategory?.let {
            categoryManager.setSelectedCategory(it)
        }
    }


    private fun setupCategorySelectedListener() {
        categoryManager.setOnCategorySelectedListener {
            val selected = categoryManager.getSelectedCategory()
            if (selected != null) {
                gameViewModel.selectCategory(selected)
                updateUI()
            }
        }
    }


    private fun setupDiceClickListeners() {
        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (gameViewModel.rollCount > 0) {
                    gameViewModel.toggleDieSelected(index)
                    updateUI()
                }
            }
        }
    }

    private fun setupButtonListeners() {
        binding.rollButton.setOnClickListener {
            if (!gameViewModel.isGameOver()) {
                gameViewModel.rollDice()
                gameViewModel.setScoreButtonsEnabled(true)
                updateUI()
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
        updateTexts()
        updateClickables()
    }

    private fun updateTexts() {
        updateRollsLeftText()
        updateInstructionText()
        updateRoundText(gameViewModel.roundNumber)
    }

    private fun updateClickables() {
        updateDiceImages(gameViewModel.dice)
        updateScoreCategoryButtons()
        updateSelectedCategory()
        updateNextRoundButton()
    }

    private fun updateRollsLeftText() {
        val rollsLeft = gameViewModel.maxRolls - gameViewModel.rollCount
        binding.rollsLeftText.text = getString(R.string.rolls_left, rollsLeft)
    }

    private fun updateInstructionText() {
        binding.instructionText.text = getInstructionText()
    }

    private fun getInstructionText(): String {
        val rollCount = gameViewModel.rollCount
        val maxRolls = gameViewModel.maxRolls

        return when {
            rollCount == 0 -> getStartInstruction()
            gameViewModel.isScoreCategoryChosen -> getScoringInstruction()
            rollCount >= maxRolls -> getRoundOverInstruction()
            else -> getSelectDiceOrScoreInstruction()
        }
    }

    private fun getStartInstruction(): String {
        return getString(R.string.instruction_text_start)
    }

    private fun getScoringInstruction(): String {
        val categoryName = gameViewModel.selectedCategory?.label ?: "?"
        return getString(R.string.instruction_text_scoring_in_category, categoryName)
    }

    private fun getRoundOverInstruction(): String {
        return getString(R.string.instruction_text_round_over)
    }

    private fun getSelectDiceOrScoreInstruction(): String {
        return getString(R.string.instruction_text_select_dice_or_score)
    }

    private fun updateSelectedCategory() {
        val selectedCategory = gameViewModel.selectedCategory
        if (selectedCategory != null) {
            categoryManager.setSelectedCategory(selectedCategory)
        }
    }

    private fun updateNextRoundButton() {
        binding.nextRoundButton.isEnabled =
            gameViewModel.isScoreCategoryChosen && gameViewModel.isDiceSelected
    }


    private fun updateRoundText(round: Int) {
        binding.roundText.text = getString(R.string.round_text, round)
    }

    private fun updateDiceImages(dice: List<Die>) {
        val noMoreRolls = hasNoMoreRolls()

        dice.forEachIndexed { index, die ->
            val dieColor = getDieColor(die, noMoreRolls)
            val imageRes = getDieImageRes(die.value, dieColor)
            diceImages[index].setImageResource(imageRes)
        }

        updateRollButtonState()
    }

    private fun hasNoMoreRolls(): Boolean {
        return gameViewModel.rollCount >= gameViewModel.maxRolls
    }

    private fun getDieColor(die: Die, noMoreRolls: Boolean): DieColor {
        return when {
            !die.hasBeenRolled -> DieColor.GRAY
            die.isSelected -> DieColor.RED
            noMoreRolls -> DieColor.GRAY
            else -> DieColor.WHITE
        }
    }

    private fun updateRollButtonState() {
        binding.rollButton.isEnabled = ((gameViewModel.rollCount == 0 ||
                gameViewModel.isDiceSelected) && !gameViewModel.isEndOfRound())
    }

    private fun onNextRound(selectedCategory: ScoreOption) {
        val selectedDiceValues = gameViewModel.getSelectedDice().map { it.value }


        val result = gameViewModel.calculateScoreForCategory(selectedCategory, selectedDiceValues)
        result.fold(
            onSuccess = { score ->
                gameViewModel.registerScore(selectedCategory, score)

                updateInstructionText()

                if (gameViewModel.isGameOver()) {
                    navigateToResultScreen()
                } else {
                    gameViewModel.resetForNextRound()
                    categoryManager.setCategories(gameViewModel.remainingCategories)
                    gameViewModel.setScoreButtonsEnabled(false)
                    updateUI()
                }
            },
            onFailure = { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToResultScreen() {
        val scores = HashMap<String, Int>().apply {
            gameViewModel.scoreMap.forEach { (cat, score) -> put(cat.name, score) }
        }

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("scores", scores)
            putExtra("total", gameViewModel.getTotalScore())
        }
        startActivity(intent)
        finish()
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