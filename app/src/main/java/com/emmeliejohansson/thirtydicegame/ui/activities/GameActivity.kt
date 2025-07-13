package com.emmeliejohansson.thirtydicegame.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.databinding.ActivityGameBinding
import com.emmeliejohansson.thirtydicegame.managers.ScoreCategoryManager
import com.emmeliejohansson.thirtydicegame.models.Die
import com.emmeliejohansson.thirtydicegame.models.GameViewModel
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.emmeliejohansson.thirtydicegame.ui.utils.DieImageMapper

/**
 * The main activity for the game screen.
 * Handles user interaction, game state updates, and UI rendering.
 */
class GameActivity : AppCompatActivity() {

    /** ViewModel containing game state and logic */
    private val gameViewModel: GameViewModel by viewModels()

    /** ViewBinding for accessing views in activity_game.xml */
    private lateinit var binding: ActivityGameBinding

    /** List of dice ImageViews */
    private lateinit var diceImages: List<ImageView>

    /** Manager for score category selection and button interaction */
    private lateinit var categoryManager: ScoreCategoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initCategoryManager()
        initDiceImages()
        setupUIInteractions()
        updateUI()
    }

    /** Initializes view binding */
    private fun initBinding() {
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /** Initializes the score category manager */
    private fun initCategoryManager() {
        categoryManager = ScoreCategoryManager(
            this,
            binding.categoryToggleGroup,
            binding.nextRoundButton
        )
    }

    /** Initializes references to dice image views */
    private fun initDiceImages() {
        diceImages = listOf(
            binding.die1, binding.die2, binding.die3,
            binding.die4, binding.die5, binding.die6
        )
    }

    /** Sets up UI interactions such as click listeners and category selection */
    private fun setupUIInteractions() {
        setupDiceClickListeners()
        setupButtonListeners()
        setupCategorySelection()
        setupCategorySelectionListener()
    }

    /** Sets click listeners for each die to toggle its selected state */
    private fun setupDiceClickListeners() {
        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                gameViewModel.toggleDieSelected(index)
                updateUI()
            }
        }
    }

    /** Initializes score category buttons with available categories */
    private fun setupCategorySelection() {
        categoryManager.setCategories(gameViewModel.remainingCategories)
        gameViewModel.selectedCategory?.let {
            categoryManager.setSelectedCategory(it)
        }
    }

    /** Listens for category selection changes and updates the ViewModel */
    private fun setupCategorySelectionListener() {
        categoryManager.setOnCategorySelectedListener {
            val selected = categoryManager.getSelectedCategory()
            if (selected != null) {
                gameViewModel.selectCategory(selected)
                updateUI()
            }
        }
    }

    /** Sets up button click listeners for roll, next round, and result actions */
    private fun setupButtonListeners() {
        binding.rollButton.setOnClickListener {
            if (!gameViewModel.isGameOver()) {
                gameViewModel.rollDice()
                gameViewModel.prepareForNextRoll()
                gameViewModel.setScoreButtonsEnabled(true)
                updateUI()
            }
        }

        binding.nextRoundButton.setOnClickListener {
            categoryManager.getSelectedCategory()?.let { selectedCategory ->
                onNextRound(selectedCategory)
            }
        }

    }


    private fun onNextRound(selectedCategory: ScoreOption) {
        gameViewModel.completeRound(
            selectedCategory,
            onSuccess = { isGameOver ->
                updateInstructionText()
                if (isGameOver) navigateToResultScreen()
                else prepareNextRound()
            },
            onFailure = { error -> showScoreError(error) }
        )
    }

    /** Prepares UI and game state for a new round */
    private fun prepareNextRound() {
        gameViewModel.prepareRoundForUI()
        categoryManager.setCategories(gameViewModel.remainingCategories)
        updateUI()
    }

    /** Displays a toast with the provided error message */
    private fun showScoreError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }

    /** Updates all UI components based on current game state */
    private fun updateUI() {
        updateTexts()
        updateAllGameControls()
    }

    /** Updates round, roll count, and instruction text views */
    private fun updateTexts() {
        binding.rollsLeftText.text = getString(R.string.rolls_left, gameViewModel.rollsLeft)
        updateInstructionText()
        binding.roundText.text = getString(R.string.round_text, gameViewModel.roundNumber)
    }

    /** Updates the instruction text based on current game state */
    private fun updateInstructionText() {
        binding.instructionText.text = gameViewModel.getInstructionText(this)
    }

    /** Updates all interactive game controls (dice, buttons, categories) */
    private fun updateAllGameControls() {
        updateDiceImages(gameViewModel.dice)
        updateScoreCategoryButtons()
        updateSelectedCategory()
        updateNextRoundButton()
        updateRollButtonState()
    }

    /** Enables or disables score category buttons based on game state */
    private fun updateScoreCategoryButtons() {
        if (gameViewModel.areScoreButtonsEnabled) {
            categoryManager.enableAllButtons()
        } else {
            categoryManager.disableAllButtons()
        }
    }

    /** Updates the currently selected score category in the UI */
    private fun updateSelectedCategory() {
        gameViewModel.selectedCategory?.let {
            categoryManager.setSelectedCategory(it)
        }
    }

    /** Enables or disables the "Next Round" button */
    private fun updateNextRoundButton() {
        binding.nextRoundButton.isEnabled = gameViewModel.isNextRoundButtonEnabled
    }

    /**
     * Updates the dice images to reflect current values and selection status
     *
     * @param dice List of current dice from the ViewModel
     */
    private fun updateDiceImages(dice: List<Die>) {
        dice.forEachIndexed { index, die ->
            val dieColor = gameViewModel.getDieColor(die)
            val imageRes = DieImageMapper.getDieImageRes(die.value, dieColor)
            diceImages[index].setImageResource(imageRes)
        }
        updateRollButtonState()
    }

    /** Enables or disables the "Roll" button based on allowed rolls */
    private fun updateRollButtonState() {
        binding.rollButton.isEnabled = gameViewModel.isRollButtonEnabled()
    }

    /**
     * Navigates to the result screen and passes score data
     */
    private fun navigateToResultScreen() {
        Log.e("TestLog", "navigateToResultScreen called")
        val scoreMap = gameViewModel.getExportableScores()
        val serializableMap = HashMap<String, Int>().apply {
            for ((key, value) in scoreMap) {
                put(key, value)
            }
        }

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("scores", serializableMap)
            putExtra("total", gameViewModel.getTotalScore())
        }
        startActivity(intent)
    }
}
