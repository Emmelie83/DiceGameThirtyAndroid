package com.emmeliejohansson.thirtydicegame.models

import kotlin.random.Random

class Die(val id: Int) {
    var value = id
    var isRollable: Boolean = true
    private var hasBeenRolled = false

    fun roll() {
        if (!isRollable) return
        value = Random.nextInt(1, 7)
        hasBeenRolled = true
    }

    fun toggleIsRollable() {
        if (hasBeenRolled) {
            isRollable = !isRollable
        }
    }
}