package com.emmeliejohansson.thirtydicegame

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.models.Game
import com.emmeliejohansson.thirtydicegame.services.DiceStore

class MainActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var diceImages: List<ImageView>
    private lateinit var rollButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        rollButton = findViewById(R.id.rollButton)

        // 3. Handle roll button
        rollButton.setOnClickListener {
            game.rollDice()
            updateDiceImages()
        }

        // 4. Toggle dice active/inactive on tap
        diceImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val die = DiceStore.getDieById(index + 1)
                die?.let {
                    it.toggleIsActive()
                    updateDiceImages()
                }
            }
        }

        updateDiceImages()
    }

    private fun updateDiceImages() {
        DiceStore.getAllDice().forEachIndexed { index, die ->
            val imageRes = if (die.isActive) {
                getRedDieImageRes(die.value)
            } else {

                getDieImageRes(die.value)// red dice
            }
            diceImages[index].setImageResource(imageRes)
        }
    }

    private fun getDieImageRes(value: Int): Int {
        return when (value) {
            1 -> R.drawable.die_1
            2 -> R.drawable.die_2
            3 -> R.drawable.die_3
            4 -> R.drawable.die_4
            5 -> R.drawable.die_5
            6 -> R.drawable.die_6
            else -> R.drawable.die_1 // default fallback
        }
    }

    private fun getRedDieImageRes(value: Int): Int {
        return when (value) {
            1 -> R.drawable.red_die_1
            2 -> R.drawable.red_die_2
            3 -> R.drawable.red_die_3
            4 -> R.drawable.red_die_4
            5 -> R.drawable.red_die_5
            6 -> R.drawable.red_die_6
            else -> R.drawable.red_die_1 // fallback
        }
    }
}
