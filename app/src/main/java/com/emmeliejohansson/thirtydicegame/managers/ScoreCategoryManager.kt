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
 * @param context The context used to create UI elements.
 * @param categoryLayout The FlexboxLayout where buttons are added.
 * @param nextRoundButton Button that proceeds to the next round (enabled on category selection).
 */
class ScoreCategoryManager(
    private val context: Context,
    private val categoryLayout: FlexboxLayout,
    private val nextRoundButton: Button
) {

    private var selectedButton: MaterialButton? = null
    private var onCategorySelected: ((ScoreOption) -> Unit)? = null
    private var currentCategories: List<ScoreOption> = emptyList()

    /**
     * Registers a listener that is triggered when a category is selected.
     *
     * @param listener Callback invoked with the selected category.
     */
    fun setOnCategorySelectedListener(listener: (ScoreOption) -> Unit) {
        onCategorySelected = listener
    }

    /**
     * Sets and displays the list of selectable categories as buttons.
     *
     * @param categories List of score categories to display.
     */
    fun setCategories(categories: List<ScoreOption>) {
        currentCategories = categories
        renderCategoryOptions(categories)
    }

    /**
     * Marks a specific category button as selected and disables it.
     * Does NOT trigger the category selection listener.
     *
     * @param category The category to mark as selected.
     */
    fun setSelectedCategory(category: ScoreOption) {
        findButtonByCategory(category)?.let {
            handleSelection(it, notify = false)
            it.isEnabled = false
        }
    }

    /**
     * Returns the currently selected score category, or null if none.
     */
    fun getSelectedCategory(): ScoreOption? =
        selectedButton?.tag as? ScoreOption

    /**
     * Enables all category buttons.
     * Typically called at the beginning of a new round.
     */
    fun enableAllButtons() {
        forEachButton { it.isEnabled = true }
    }

    /**
     * Disables all category buttons.
     * Typically used after a category has been selected and locked in.
     */
    fun disableAllButtons() {
        forEachButton { it.isEnabled = false }
    }

    /**
     * Removes all buttons from the layout and recreates them based on the category list.
     */
    private fun renderCategoryOptions(categories: List<ScoreOption>) {
        categoryLayout.removeAllViews()
        categories.forEach { category ->
            val button = createCategoryButton(category)
            categoryLayout.addView(button)
        }
    }

    /**
     * Creates and returns a MaterialButton for the given score category.
     *
     * @param option The category this button represents.
     */
    private fun createCategoryButton(option: ScoreOption): MaterialButton {
        return MaterialButton(context).apply {
            text = option.label
            tag = option
            layoutParams = createButtonLayoutParams()
            isCheckable = true
            isEnabled = false // Initially disabled until game state allows interaction

            setOnClickListener {
                handleSelection(this)
            }
        }
    }

    /**
     * Handles selection of a category button:
     * - Deselects the previous selection.
     * - Marks the clicked button as selected.
     * - Changes button color.
     * - Enables the "Next Round" button.
     * - Notifies the listener if requested.
     *
     * @param clickedButton The button the user clicked.
     * @param notify Whether to notify the category selection listener.
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

        nextRoundButton.isEnabled = true

        if (notify) {
            getSelectedCategory()?.let { onCategorySelected?.invoke(it) }
        }
    }

    /**
     * Returns layout parameters with spacing for each category button.
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
     * Searches for a button associated with the given score category.
     *
     * @param category The category to search for.
     * @return The matching button, or null if not found.
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
     * Applies an action to all MaterialButtons in the category layout.
     *
     * @param action The operation to apply to each button.
     */
    private fun forEachButton(action: (MaterialButton) -> Unit) {
        for (i in 0 until categoryLayout.childCount) {
            (categoryLayout.getChildAt(i) as? MaterialButton)?.let(action)
        }
    }
}
