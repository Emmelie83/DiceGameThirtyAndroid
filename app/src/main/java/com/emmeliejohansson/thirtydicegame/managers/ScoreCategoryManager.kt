package com.emmeliejohansson.thirtydicegame.managers

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.emmeliejohansson.thirtydicegame.R
import com.emmeliejohansson.thirtydicegame.models.ScoreOption
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton

/**
 * Handles UI logic for score category buttons.
 *
 * This class:
 * - Dynamically creates category buttons based on available options.
 * - Tracks and updates selected category.
 * - Notifies listeners when a category is selected.
 * - Manages enabled/disabled states for category buttons.
 */
class ScoreCategoryManager(
    private val context: Context,
    private val categoryLayout: FlexboxLayout,
    private val nextRoundButton: Button
) {

    private var selectedButton: MaterialButton? = null
    private var onCategorySelected: ((ScoreOption) -> Unit)? = null
    private var currentCategories: List<ScoreOption> = emptyList()

    // ----------------------------
    // Public Interface
    // ----------------------------

    /**
     * Registers a listener for when a category is selected.
     */
    fun setOnCategorySelectedListener(listener: (ScoreOption) -> Unit) {
        onCategorySelected = listener
    }

    /**
     * Renders the given list of categories as buttons.
     */
    fun setCategories(categories: List<ScoreOption>) {
        currentCategories = categories
        renderCategoryOptions(categories)
    }

    /**
     * Visually selects a category button and disables it.
     * Does NOT notify listeners.
     */
    fun setSelectedCategory(category: ScoreOption) {
        findButtonByCategory(category)?.let {
            handleSelection(it, notify = false)
            it.isEnabled = false
        }
    }

    /**
     * Returns the currently selected score category, if any.
     */
    fun getSelectedCategory(): ScoreOption? =
        selectedButton?.tag as? ScoreOption

    /**
     * Enables all category buttons (e.g., at start of turn).
     */
    fun enableAllButtons() {
        forEachButton { it.isEnabled = true }
    }

    /**
     * Disables all category buttons (e.g., after scoring).
     */
    fun disableAllButtons() {
        forEachButton { it.isEnabled = false }
    }

    // ----------------------------
    // Private Helpers
    // ----------------------------

    /**
     * Clears and re-renders buttons for the provided categories.
     */
    private fun renderCategoryOptions(categories: List<ScoreOption>) {
        categoryLayout.removeAllViews()
        categories.forEach { category ->
            val button = createCategoryButton(category)
            categoryLayout.addView(button)
        }
    }

    /**
     * Creates a MaterialButton for a specific score category.
     */
    private fun createCategoryButton(option: ScoreOption): MaterialButton {
        return MaterialButton(context).apply {
            text = option.label
            tag = option
            layoutParams = createButtonLayoutParams()
            isCheckable = true
            isEnabled = false

            setOnClickListener {
                handleSelection(this)
            }
        }
    }

    /**
     * Handles visual and logical changes when a button is selected.
     */
    private fun handleSelection(clickedButton: MaterialButton, notify: Boolean = true) {
        selectedButton?.apply {
            isChecked = false
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.primary)
        }

        selectedButton = clickedButton
        clickedButton.isChecked = true
        clickedButton.backgroundTintList =
            ContextCompat.getColorStateList(context, R.color.category_selected)

        // Enable "Next Round" button
        nextRoundButton.isEnabled = true

        if (notify) {
            getSelectedCategory()?.let { onCategorySelected?.invoke(it) }
        }
    }

    /**
     * Returns layout parameters with margins for category buttons.
     */
    private fun createButtonLayoutParams(): ViewGroup.MarginLayoutParams {
        return ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 5, 16, 5)
        }
    }

    /**
     * Finds a button in the layout that matches the given score category.
     */
    private fun findButtonByCategory(category: ScoreOption): MaterialButton? {
        for (i in 0 until categoryLayout.childCount) {
            val view = categoryLayout.getChildAt(i)
            if ((view.tag as? ScoreOption) == category) {
                return view as? MaterialButton
            }
        }
        return null
    }

    /**
     * Iterates over all buttons in the layout and applies the given action.
     */
    private fun forEachButton(action: (MaterialButton) -> Unit) {
        for (i in 0 until categoryLayout.childCount) {
            (categoryLayout.getChildAt(i) as? MaterialButton)?.let(action)
        }
    }
}
