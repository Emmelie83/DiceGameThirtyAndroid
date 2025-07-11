package com.emmeliejohansson.thirtydicegame.models

import java.io.Serializable

/**
 * Enum representing score categories in the game.
 *
 * @property label The user-friendly name shown in the UI.
 * @property value The numeric value associated with the category, or null for special categories like LOW.
 */
enum class ScoreOption(val label: String, val value: Int?) : Serializable {
    LOW("Low", null),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    ELEVEN("11", 11),
    TWELVE("12", 12);

    fun isNumeric(): Boolean = value != null
    override fun toString(): String = label

}
