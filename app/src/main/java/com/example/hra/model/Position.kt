package com.example.hra.model

data class Position (var x: Int, var y: Int) {
    constructor(position: Position, direction: Direction) : this(position.x, position.y) {
        this.x += direction.relativeX
        this.y += direction.relativeY
    }

    fun isFree (gameObjects: ArrayList<GameObject>): Boolean {
        for (gameObject in gameObjects) {
            if (gameObject.position == this) {
                return false
            }
        }
        return true
    }
}