package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents the visual color state of a die.
 * Used to indicate different die statuses in the UI.
 */
enum class DieColor {
    /** Red indicates the die is currently selected by the player. */
    RED,

    /** Gray indicates the die is inactive or has not been rolled. */
    GRAY,

    /** White indicates the die is active but not selected. */
    WHITE
}