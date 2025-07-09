package com.emmeliejohansson.thirtydicegame.managers

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton

class ScoreCategoryManager(
    private val context: Context,
    private val categoryLayout: FlexboxLayout,
    private val nextRoundButton: Button
) {
    private var selectedButton: MaterialButton? = null
    private var onCategorySelected: ((ScoreOption) -> Unit)? = null
    private var currentCategories: List<ScoreOption> = emptyList()


    fun setOnCategorySelectedListener(listener: (ScoreOption) -> Unit) {
        onCategorySelected = listener
    }

    fun setCategories(categories: List<ScoreOption>) {
        currentCategories = categories
        renderCategoryOptions(categories)
    }

    fun setSelectedCategory(category: ScoreOption) {
        val button = findButtonByCategory(category)
        button?.let {
            handleSelection(it, notify = false)
            it.isEnabled = false
        }
    }

    fun getSelectedCategory(): ScoreOption? =
        selectedButton?.tag as? ScoreOption

    fun enableAllButtons() {
        for (i in 0 until categoryLayout.childCount) {
            categoryLayout.getChildAt(i).isEnabled = true
        }
    }

    fun disableAllButtons() {
        for (i in 0 until categoryLayout.childCount) {
            categoryLayout.getChildAt(i).isEnabled = false
        }
    }

    private fun renderCategoryOptions(categories: List<ScoreOption>) {
        clearCategoryLayout()
        categories.forEach { category ->
            val button = createCategoryButton(category)
            categoryLayout.addView(button)
        }
    }

    private fun clearCategoryLayout() {
        categoryLayout.removeAllViews()
        selectedButton = null
        nextRoundButton.isEnabled = false
    }

    private fun createCategoryButton(option: ScoreOption): MaterialButton {
        return MaterialButton(context).apply {
            text = option.label
            tag = option
            layoutParams = createButtonLayoutParams()
            isCheckable = true
            isEnabled = false

            setOnClickListener {
                handleSelection(this)
                val selected = getSelectedCategory()
                if (selected != null) {
                    onCategorySelected?.invoke(selected)
                }
            }
        }
    }

    private fun handleSelection(clickedButton: MaterialButton, notify: Boolean = true) {
        selectedButton?.apply {
            isChecked = false
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
        }

        selectedButton = clickedButton
        clickedButton.isChecked = true
        clickedButton.backgroundTintList =
            ContextCompat.getColorStateList(context, R.color.category_selected)

        nextRoundButton.isEnabled = true

        if (notify) {
            val selected = getSelectedCategory()
            if (selected != null) {
                onCategorySelected?.invoke(selected)
            }
        }
    }

    private fun createButtonLayoutParams(): ViewGroup.MarginLayoutParams {
        return ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 5, 16, 5)
        }
    }

    private fun findButtonByCategory(category: ScoreOption): MaterialButton? {
        for (i in 0 until categoryLayout.childCount) {
            val view = categoryLayout.getChildAt(i)
            if ((view.tag as? ScoreOption) == category) {
                return view as? MaterialButton
            }
        }
        return null
    }
}