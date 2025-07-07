package com.emmeliejohansson.thirtydicegame

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.services.DiceStore
import com.emmeliejohansson.thirtydicegame.managers.CategoryManager
import com.google.android.flexbox.FlexboxLayout

class MainActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var diceImages: List<ImageView>
    private lateinit var rollButton: Button
    private lateinit var nextRoundButton: Button
    private lateinit var categoryManager: CategoryManager
    private lateinit var categoryToggleGroup: FlexboxLayout
    private lateinit var categorySection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categoryToggleGroup = findViewById(R.id.categoryToggleGroup)
        nextRoundButton = findViewById(R.id.nextRoundButton)
        rollButton = findViewById(R.id.rollButton)
        categorySection = findViewById(R.id.categorySection)
        categorySection.visibility = View.GONE

        categoryManager = CategoryManager(this, categoryToggleGroup, nextRoundButton)

        game = Game()
        game.fillDiceStore()

        diceImages = listOf(
            findViewById(R.id.die1),
            findViewById(R.id.die2),
            findViewById(R.id.die3),
            findViewById(R.id.die4),
            findViewById(R.id.die5),
            findViewById(R.id.die6)
        )

        nextRoundButton.isEnabled = false

        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (!game.isEndOfRound()) {
                    val die = DiceStore.getDieById(index + 1)
                    die?.toggleIsRollable()
                    updateDiceImages()
                }
            }
        }

        updateDiceImages()

        rollButton.setOnClickListener {
            if (!game.isGameOver()) {
                game.rollDice()
                updateDiceImages()

                if (game.rollCount == 1) {
                    categoryManager.enableAllButtons()
                    categorySection.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
            }
        }


        nextRoundButton.setOnClickListener {
            val selectedCategory = categoryManager.getSelectedCategory()
            if (selectedCategory != null) {
                game.useScoringCategory(selectedCategory)
                categoryManager.removeCategory(selectedCategory)

                game.resetForNextRound()
                updateDiceImages()
            }
        }
    }

    private fun updateDiceImages() {
        DiceStore.getAllDice().forEachIndexed { index, die ->
            val imageRes = when {
                die.isRollable -> getWhiteDieImageRes(die.value)
                game.isEndOfRound() -> getGrayDieImageRes(die.value)
                else -> getRedDieImageRes(die.value)
            }
            diceImages[index].setImageResource(imageRes)
        }
    }

    private fun getWhiteDieImageRes(value: Int): Int = when (value) {
        1 -> R.drawable.die_1
        2 -> R.drawable.die_2
        3 -> R.drawable.die_3
        4 -> R.drawable.die_4
        5 -> R.drawable.die_5
        6 -> R.drawable.die_6
        else -> R.drawable.die_1
    }

    private fun getRedDieImageRes(value: Int): Int = when (value) {
        1 -> R.drawable.red_die_1
        2 -> R.drawable.red_die_2
        3 -> R.drawable.red_die_3
        4 -> R.drawable.red_die_4
        5 -> R.drawable.red_die_5
        6 -> R.drawable.red_die_6
        else -> R.drawable.red_die_1
    }

    private fun getGrayDieImageRes(value: Int): Int = when (value) {
        1 -> R.drawable.gray_die_1
        2 -> R.drawable.gray_die_2
        3 -> R.drawable.gray_die_3
        4 -> R.drawable.gray_die_4
        5 -> R.drawable.gray_die_5
        6 -> R.drawable.gray_die_6
        else -> R.drawable.gray_die_1
    }
}
