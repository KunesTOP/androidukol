package com.example.hra.controller
import com.example.hra.model.*
import kotlin.random.Random

class Game {
    private var width = 20
    private var height = 10
    private var numForests = 4
    private var gamePlan = GamePlan (width, height, numForests)
    private var possibleCommands = arrayListOf<String>()


    private var hero = Hero (position = gamePlan.generateRandomPositionOnMeadow())
    private var command : String = ""

    private var numEnemies = 5
    var enemies = arrayListOf<GameObject>()
    var gameObjects = arrayListOf<GameObject>()

    var score = 0
    var items = arrayListOf<Item>()

    init {
        hero.name = readHeroName()
        gameObjects.add(hero)
        generateEnemies()
        generateItems()
    }

    fun run() {
        var message = ""
        do {
            possibleCommands.clear()
            setPossibleCommands(hero)
            println ("----------------------------------------------")
            println (getSurroundingDescription())
            println ("Možné příkazy jsou: $possibleCommands")
            command = readCommand()
            println (runCommand(command))
            println (enemyAttack())
            message = isGameFinished()
            if (message.isNotEmpty()) {
                println (message)
                break
            }
            heroHealing()
        } while (true)
    }
    //fix
    fun getSurroundingDescription(): String {
        val description = StringBuilder("")
        enumValues<Direction>().forEach {
            description.append("Na ${it.description} je " +
                    "${gamePlan.getGameField(Position(hero.position, it))
                        .terrain.description}.")
        }

        val enemy = getEnemyOnGameField(hero.position, enemies)
        if (enemy != null && ! enemy!!.isDead()) description.append("\nPozor ${enemy!!.name}.")
        if (enemy != null && enemy!!.isDead()) description.append("\nNa zemi vidíš mrtvolu ${enemy!!.name}.")
        return description.toString()
    }

    fun readCommand(): String {
        var input: String
        do {
            print("Zadej příkaz: ")
            input = readLine().toString()
        } while (!checkCommand(input))
        return input
    }

    fun setPossibleCommands(hero: Hero) {
        val itemsOnField = getItemsOnGameField(hero.position, items)
        for (item in itemsOnField) {
            possibleCommands.add("seber ${item.name}")
        }
        enumValues<Direction>().forEach { direction ->
            val newPosition = Position(hero.position, direction)
            val gameField = gamePlan.getGameField(newPosition)

            if (gameField.isWalkable()) {
                possibleCommands.add(direction.command)
            }

            if (gameField.terrain == Terrain.FOREST) {
                // Přidejte příkazy na kácení, například "kacejs", "kacejz" atd.
                possibleCommands.add("kacej${direction.command}")
            }
        }

        possibleCommands.add("stav")
        possibleCommands.add("mapa")
        possibleCommands.add("konec")

        val enemy = getEnemyOnGameField(hero.position, enemies)
        if (enemy is Enemy && !enemy.isDead()) {
            possibleCommands.add("utok")
        }
    }
    //fix
    fun runCommand(command: String): String {
        score++
        val enemy = getEnemyOnGameField(hero.position, enemies)

        when {
            command == "stav" -> return hero.toString()
            command == "mapa" -> {
                gamePlan.map(gameObjects)
                return ""
            }
            command == "utok" -> {
                return if (enemy != null) {
                    hero.attack(enemy)
                } else {
                    ""
                }
            }
            command.startsWith("kacej") -> {
                val direction = Direction.values().find { it.command == command.substring(5) }
                if (direction != null) {
                    return hero.cutDown(direction, gamePlan)
                } else {
                    return "Neplatný směr pro kácení."
                }
            }
            command in enumValues<Direction>().map { it.command } -> {
                val direction = enumValues<Direction>().first { it.command == command }
                return hero.go(direction)
            }
            command.startsWith("seber") -> {
                val itemName = command.substring(6)
                val itemToPickUp = getItemsOnGameField(hero.position, items).find { it.name == itemName }

                if (itemToPickUp != null && !itemToPickUp.pickedUp) {
                    hero.addItem(itemToPickUp)
                    return "Sebral jsi předmět ${itemToPickUp.name}."
                } else {
                    return "Nelze sebrat předmět $itemName."
                }
            }
            else -> return "Neplatný příkaz. Platné příkazy jsou: ${possibleCommands.joinToString(", ")}"
        }
    }

    fun checkCommand(command: String): Boolean {
        val validCommands = possibleCommands.joinToString(", ")
        if (command in possibleCommands) {
            return true
        } else {
            println("Neplatný příkaz. Platné příkazy jsou: $validCommands")
            return false
        }
    }

    private fun generateEnemies() {
        var enemy : Enemy
        repeat (numEnemies) {
            enemy = generateEnemy()
            enemies.add(enemy)
            gameObjects.add(enemy)
        }
    }

    private fun generateEnemy(): Enemy {
        val randEnemy = Random.nextInt(1, 4) // Náhodný výběr nepřítele z 1 do 3

        val enemy = when (randEnemy) {
            1 -> {
                Enemy(
                    name = "Skeleton",
                    position = gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
                    health = 5.0,
                    attack = 5.0,
                    defense = 1.0
                )
            }
            2 -> {
                Enemy(
                    name = "Zombie",
                    position = gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
                    health = 7.5,
                    attack = 3.5,
                    defense = 1.5
                )
            }
            3 -> {
                Enemy(
                    name = "Brute",
                    position = gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
                    health = 10.0,
                    attack = 5.0,
                    defense = 1.5
                )
            }
            else -> {
                // V případě, že by náhodný výběr nepřítele neodpovídal žádnému z těchto typů,
                Enemy(
                    name = "Slime",
                    position = gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
                    health = 1.0,
                    attack = 1.0,
                    defense = 1.0
                )
            }
        }

        return enemy
    }

    fun readHeroName(): String {
        print("Zadej jméno svého hrdiny: ")
        return readLine().toString()
    }

    fun heroHealing() {
        hero.health += hero.healing

        if (hero.health > 100.0) {
            hero.health = 100.0
        }
    }

    fun getEnemyOnGameField (position: Position, gameObjects: ArrayList<GameObject>) : Enemy? {
        for (obj in gameObjects ) {
            if (obj is Enemy) {
                if (obj.position == position) {
                    return obj
                }
            }
        }
        return null
    }

    fun enemyAttack() : String {
        val enemy = getEnemyOnGameField(hero.position, enemies)

        if (enemy is Enemy) {
            if (!enemy.isDead()) {
                return(enemy.attack(hero))
            }
        }
        return ""
    }

    fun allEnemiesDead() : Boolean {
        for (enemy in enemies) {
            if (enemy is Enemy && ! enemy.isDead()) return false
        }
        return true
    }

    fun isGameFinished(): String {
        if (hero.isDead()) return "Jsi mrtvý."
        if (allEnemiesDead()) return "Všichni nepřátelé jsou mrtví. Vyhrál jsi. Potřeboval jsi $score tahů."
        if (command == "konec") return "Konec hry."
        return ""
    }

    private fun generateItems() {
        var item = Item(
            "Mec",
            gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
            0.0,
            4.0,
            1.0,
            0.0,
            false
        )
        items.add(item)
        gameObjects.add(item)
        item = Item(
            "Dyka",
            gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
            0.0,
            2.0,
            0.5,
            0.0,
            false
        )
        items.add(item)
        gameObjects.add(item)
        item = Item(
            "Stit",
            gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
            0.0,
            0.0,
            2.0,
            0.0,
            false
        )
        items.add(item)
        gameObjects.add(item)
        item = Item(
            "Helma",
            gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
            0.0,
            0.0,
            2.0,
            0.0,
            false
        )
        items.add(item)
        gameObjects.add(item)
        item = Item(
            "Brneni",
            gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
            0.0,
            0.0,
            3.5,
            0.0,
            false
        )
        items.add(item)
        gameObjects.add(item)
        item = Item(
            "Lekarna",
            gamePlan.generateFreeRandomPositionOnMeadow(gameObjects),
            0.0,
            0.0,
            0.0,
            2.5,
            false
        )
        items.add(item)
        gameObjects.add(item)
    }

    fun getItemsOnGameField(position: Position, items: ArrayList<Item>): List<Item> {
        return items.filter { it.position == position && !it.pickedUp }
    }
}