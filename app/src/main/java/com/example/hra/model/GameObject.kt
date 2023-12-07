package com.example.hra.model

abstract class GameObject {
    open lateinit var name : String
    abstract var position: Position
}

