package com.emmeliejohansson.thirtydicegame.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.models.GameViewModel
import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.databinding.ActivityGameBinding
import com.emmeliejohansson.thirtydicegame.managers.ScoreCategoryManager
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.emmeliejohansson.thirtydicegame.ui.utils.DieImageMapper

class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var binding: ActivityGameBinding
    private lateinit var diceImages: List<ImageView>
    private lateinit var categoryManager: ScoreCategoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initCategoryManager()
        initDiceImages()
        setupUIInteractions()
        updateUI()
    }

    private fun initBinding() {
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initCategoryManager() {
        categoryManager = ScoreCategoryManager(
            this,
            binding.categoryToggleGroup,
            binding.nextRoundButton
        )
    }

    private fun initDiceImages() {
        diceImages = listOf(
            binding.die1, binding.die2, binding.die3,
            binding.die4, binding.die5, binding.die6
        )
    }

    private fun setupUIInteractions() {
        setupDiceClickListeners()
        setupButtonListeners()
        setupCategorySelection()
        setupCategorySelectionListener()
    }

    private fun setupDiceClickListeners() {
        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                gameViewModel.toggleDieSelected(index)
                updateUI()
            }
        }
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
        gameViewModel.selectedCategory?.let {
            categoryManager.setSelectedCategory(it)
        }
    }


    private fun setupCategorySelectionListener() {
        categoryManager.setOnCategorySelectedListener {
            val selected = categoryManager.getSelectedCategory()
            if (selected != null) {
                gameViewModel.selectCategory(selected)
                updateUI()
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
        updateAllGameControls()
    }

    private fun updateTexts() {
        updateRollsLeftText()
        updateInstructionText()
        updateRoundText(gameViewModel.roundNumber)
    }

    private fun updateAllGameControls() {
        updateDiceImages(gameViewModel.dice)
        updateScoreCategoryButtons()
        updateSelectedCategory()
        updateNextRoundButton()
        updateRollButtonState()
    }

    private fun updateRollsLeftText() {
        binding.rollsLeftText.text = getString(R.string.rolls_left, gameViewModel.rollsLeft)
    }

    private fun updateInstructionText() {
        binding.instructionText.text = gameViewModel.getInstructionText(this)
    }

    private fun updateSelectedCategory() {
        val selectedCategory = gameViewModel.selectedCategory
        if (selectedCategory != null) {
            categoryManager.setSelectedCategory(selectedCategory)
        }
    }

    private fun updateNextRoundButton() {
        binding.nextRoundButton.isEnabled = gameViewModel.isNextRoundButtonEnabled
    }


    private fun updateRoundText(round: Int) {
        binding.roundText.text = getString(R.string.round_text, round)
    }

    private fun updateDiceImages(dice: List<Die>) {
        dice.forEachIndexed { index, die ->
            val dieColor = gameViewModel.getDieColor(die)
            val imageRes = DieImageMapper.getDieImageRes(die.value, dieColor)
            diceImages[index].setImageResource(imageRes)
        }
        updateRollButtonState()
    }


    private fun updateRollButtonState() {
        binding.rollButton.isEnabled = gameViewModel.isRollButtonEnabled()
    }

    private fun onNextRound(selectedCategory: ScoreOption) {
        gameViewModel.applyScoreAndAdvance(
            selectedCategory,
            onSuccess = { score, isGameOver ->
                updateInstructionText()
                handleGameProgression(isGameOver)
            },
            onFailure = { error ->
                showScoreError(error)
            }
        )
    }

    private fun handleGameProgression(isGameOver: Boolean) {
        if (isGameOver) {
            navigateToResultScreen()
        } else {
            prepareNextRound()
        }
    }

    private fun showScoreError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }

    private fun prepareNextRound() {
        gameViewModel.prepareForNextRound()
        categoryManager.setCategories(gameViewModel.remainingCategories)
        updateUI()
    }

    private fun navigateToResultScreen() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("scores", HashMap(gameViewModel.getExportableScores()))
            putExtra("total", gameViewModel.getTotalScore())
        }
        startActivity(intent)
        finish()
    }

}