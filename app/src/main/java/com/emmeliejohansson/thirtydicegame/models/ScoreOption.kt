package com.emmeliejohansson.thirtydicegame.models

enum class ScoreOption(val label: String) {
    LOW("Low"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    ELEVEN("11"),
    TWELVE("12");

    override fun toString(): String = label

    companion object {
        fun fromLabel(label: String): ScoreOption? =
            entries.firstOrNull { it.label == label }
    }
}