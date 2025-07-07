package com.emmeliejohansson.thirtydicegame.models

import kotlin.random.Random

class Die(val id: Int) {
    var value = id
    var isSelected: Boolean = false
    var hasBeenRolled = false

    fun roll() {
        if (hasBeenRolled && !isSelected) return
        value = Random.nextInt(1, 7)
        hasBeenRolled = true
    }

    fun toggleIsSelected() {
        if (hasBeenRolled) {
            isSelected = !isSelected
        }
    }
}