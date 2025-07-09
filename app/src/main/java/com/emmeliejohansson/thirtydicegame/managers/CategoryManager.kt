package com.emmeliejohansson.thirtydicegame.managers

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.emmeliejohansson.thirtydicegame.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton

class CategoryManager(
    private val context: Context,
    private val categoryLayout: FlexboxLayout,
    private val nextRoundButton: Button
) {
    private var selectedButton: MaterialButton? = null
    private val categories = mutableListOf<String>()

    var onCategorySelected: ((String) -> Unit)? = null

    fun setCategories(newCategories: List<String>) {
        categories.clear()
        categories.addAll(newCategories)
        renderCategoryOptions()
        disableAllButtons()
        nextRoundButton.isEnabled = false
    }

    private fun renderCategoryOptions() {
        categoryLayout.removeAllViews()
        selectedButton = null
        nextRoundButton.isEnabled = false

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
                tag = backgroundTintList

                setOnClickListener {
                    handleSelection(this)
                    onCategorySelected?.invoke(category)
                }
            }
            categoryLayout.addView(button)
        }
    }

    fun getSelectedCategory(): String? = selectedButton?.text?.toString()

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

    private fun handleSelection(clickedButton: MaterialButton) {
        selectedButton?.let {
            it.isChecked = false
            it.backgroundTintList = it.tag as? ColorStateList
        }

        selectedButton = clickedButton
        clickedButton.isChecked = true
        clickedButton.setBackgroundTintList(
            ContextCompat.getColorStateList(context, R.color.category_selected)
        )

        nextRoundButton.isEnabled = true
    }
}