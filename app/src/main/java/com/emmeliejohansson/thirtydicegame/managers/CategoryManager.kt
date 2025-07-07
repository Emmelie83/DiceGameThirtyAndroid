package com.emmeliejohansson.thirtydicegame.managers

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton

class CategoryManager(
    private val context: Context,
    private val categoryLayout: FlexboxLayout,
    private val nextRoundButton: Button
) {
    private val categories = mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    private var selectedButton: MaterialButton? = null

    init {
        renderCategoryOptions()
        disableAllButtons()
        nextRoundButton.isEnabled = false
    }

    fun getSelectedCategory(): String? = selectedButton?.text?.toString()

    fun removeCategory(category: String) {
        categories.remove(category)
        renderCategoryOptions()
        disableAllButtons()
        nextRoundButton.isEnabled = false
    }

    fun enableAllButtons() {
        for (i in 0 until categoryLayout.childCount) {
            categoryLayout.getChildAt(i).isEnabled = true
        }
    }

    fun disableAllButtons() {
        for (i in 0 until categoryLayout.childCount) {
            categoryLayout.getChildAt(i).isEnabled = false
        }
        selectedButton = null
    }

    private fun renderCategoryOptions() {
        categoryLayout.removeAllViews()
        categories.forEach { category ->
            val button = MaterialButton(context).apply {
                text = category
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 5, 16, 5)
                }
                isEnabled = false

                setOnClickListener {
                    handleSelection(this)
                }
            }
            categoryLayout.addView(button)
        }
    }


    private fun handleSelection(clickedButton: MaterialButton) {
        selectedButton?.isChecked = false

        selectedButton = clickedButton
        clickedButton.isChecked = true

        // Enable "Next Round" button
        nextRoundButton.isEnabled = true
    }
}
