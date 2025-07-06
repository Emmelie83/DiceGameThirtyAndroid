package com.emmeliejohansson.thirtydicegame.models

import kotlin.random.Random

class Die(val id: Int) {
    var value = id
    var isActive: Boolean = false
    var isYetNotRolled: Boolean = true

    fun roll() {
        if (isYetNotRolled || isActive) {
            value = Random.nextInt(1, 7)
            isYetNotRolled = false
            isActive = false
        }
    }

    fun toggleIsActive() {
        if (isYetNotRolled) return
        isActive = !isActive
    }
}