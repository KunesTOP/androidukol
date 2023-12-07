package com.example.hra.model

data class GameField (var terrain: Terrain) {
    fun isWalkable () : Boolean {
        return when (this.terrain) {
            Terrain.BRIDGE, Terrain.MEADOW -> true
            Terrain.BORDER, Terrain.FOREST, Terrain.RIVER -> false
        }
    }
}