package com.emmeliejohansson.thirtydicegame.ui.activities

import android.graphics.Typeface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.emmeliejohansson.thirtydicegame.R

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val scoreMap = intent.getSerializableExtra("scores") as? HashMap<String, Int> ?: return
        val totalScore = intent.getIntExtra("total", 0)

        val scoreTable = findViewById<TableLayout>(R.id.scoreTable)
        val playAgainButton = findViewById<Button>(R.id.playAgainButton)

        // Optional: define order for rows
        val orderedCategories = listOf(
            "Low", "4", "5", "6", "7", "8", "9", "10", "11", "12"
        )

        // Add one row per category
        orderedCategories.forEach { category ->
            val score = scoreMap[category] ?: 0
            addScoreRow(scoreTable, category, score.toString())
        }

        // Add final score row
        addScoreRow(scoreTable,
            getString(R.string.final_score_text),
            totalScore.toString(),
            isBold = true
        )

        // Set up Play Again button
        playAgainButton.setOnClickListener {
            finish() // Return to MainActivity
        }
    }

    private fun addScoreRow(table: TableLayout, label: String, score: String, isBold: Boolean = false) {
        val row = TableRow(this).apply {
            setPadding(16, 10, 16, 10)
        }

        val labelText = TextView(this).apply {
            text = label
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            if (isBold) setTypeface(null, Typeface.BOLD)
        }

        val scoreText = TextView(this).apply {
            text = score
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            if (isBold) setTypeface(null, Typeface.BOLD)
        }

        row.addView(labelText)
        row.addView(scoreText)
        table.addView(row)
    }
}
