package com.emmeliejohansson.thirtydicegame.models

/**
 * Represents the visual color state of a die.
 * Used to indicate different die statuses in the UI.
 */
enum class DieColor {
    /** Red indicates the die is currently selected by the player. */
    RED,

    /** Gray indicates the die is inactive - has yet not been rolled or cannot be rolled any more in the current round. */
    GRAY,

    /** White indicates the die is active but not selected. */
    WHITE
}