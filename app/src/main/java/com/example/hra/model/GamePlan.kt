package com.example.hra.model

import kotlin.random.Random

data class GamePlan (val width: Int = 20, val height : Int = 10, val numForrest: Int = 4) {
    private var gamePlan = Array(height) { Array(width) { GameField(Terrain.MEADOW) } }

    // blok co se pousti po inicializaci
    init {
        generateGamePlan()
    }

    private fun generateGamePlan() {
        generateBorder()
        generateRiver()
        repeat(numForrest) {
            generateForest()
        }
    }

    private fun generateBorder() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (i == 0 || j == 0 || i == height - 1 || j == width - 1) {
                    gamePlan[i][j] = GameField(Terrain.BORDER)
                }
            }
        }
    }

    private fun generateRandomCoord(size: Int): Int {
        return Random.nextInt(1, size - 1)
    }

    private fun generateRandomPosition(): Position {
        return Position(generateRandomCoord(width), generateRandomCoord(height))
    }

    private fun generateRiver() {
        var bridgePosition = generateRandomPosition()
        if (bridgePosition.x == 1 || bridgePosition.x == width - 2) {
            while (bridgePosition.x == 1 || bridgePosition.x == width - 2) {
                bridgePosition = generateRandomPosition()
            }
        }

        for (i in 1..height - 2) {
            gamePlan[i][bridgePosition.x] = GameField(Terrain.RIVER)
        }
        gamePlan[bridgePosition.y][bridgePosition.x] = GameField(Terrain.BRIDGE)

    }

    fun generateRandomPositionOnMeadow(): Position {
        var position: Position
        do {
            position = generateRandomPosition()
        } while (getGameField(position).terrain != Terrain.MEADOW || !getGameField(position).isWalkable())
        return position
    }

    private fun generateForest() {
        val centerPosition = generateRandomPositionOnMeadow()
        gamePlan[centerPosition.y][centerPosition.x] = GameField(Terrain.FOREST)

        val directions = listOf(Position(0, 1), Position(0, -1), Position(1, 0), Position(-1, 0))
        for (direction in directions) {
            val currentPos =
                Position(centerPosition.x + direction.x, centerPosition.y + direction.y)
            if (currentPos.x in 0 until width && currentPos.y in 0 until height) {
                val currentField = getGameField(currentPos)
                if (currentField.terrain == Terrain.MEADOW && currentField.isWalkable()) {
                    gamePlan[currentPos.y][currentPos.x] = GameField(Terrain.FOREST)
                }
            }
        }
    }

    fun getGameField(position: Position): GameField {
        return gamePlan[position.y][position.x]
    }

    fun map(gameObjects: ArrayList<GameObject>) {
        for (i in 0 until height) {
            for (j in 0 until width) {
                var isHeroOnField = false
                var isEnemyOnField = false
                var isItemOnField = false

                for (gameObject in gameObjects) {
                    if (gameObject.position.x == j && gameObject.position.y == i) {
                        if (gameObject is Hero) {
                            print("H ")
                            isHeroOnField = true
                        } else if (gameObject is Enemy && !gameObject.isDead()) {
                            print("N ")
                            isEnemyOnField = true
                        } else if (gameObject is Item && !gameObject.pickedUp) {
                            print("I ")
                            isItemOnField = true
                        }
                    }
                }

                if (!isHeroOnField && !isEnemyOnField && !isItemOnField) {
                    print(gamePlan[i][j].terrain.terrainChar + " ")
                }
            }
            println()
        }
    }

    fun generateFreeRandomPositionOnMeadow(gameObjects: ArrayList<GameObject>) : Position {
        var randomPosition : Position
        do {
            randomPosition = generateRandomPositionOnMeadow()
        } while (! randomPosition.isFree(gameObjects) )
        return randomPosition
    }
}