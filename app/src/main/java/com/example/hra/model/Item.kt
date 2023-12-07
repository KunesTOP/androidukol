package com.example.hra.model

data class Item(
    override var name: String = "",
    override var position: Position,
    var health: Double = 0.0,
    var attack: Double = 0.0,
    var defense: Double = 0.0,
    var healing: Double = 0.0,
    var pickedUp: Boolean = false
) : GameObject()

