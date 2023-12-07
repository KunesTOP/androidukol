package com.example.hra.model

data class Hero (override var name: String = "Hrdina",
                 override var position: Position ) : Character() {
    //var healing: Double = 1.0
    var kills: Int = 0
    var items = arrayListOf<Item>()

    fun addItem(item: Item): String {
        items.add(item)
        item.pickedUp = true
        return "Předmět ${item.name} sebrán."
    }

    override var health: Double = 100.0
        set(value) {
            field = if (value > 100.0) {
                100.0
            } else {
                value
            }
        }

    override var attack: Double = 2.5
        get() {
            var a = field
            for (item in items) {
                a += item.attack
            }
            return a
        }

    override var defense: Double = 1.5
        get() {
            var d = field
            for (item in items) {
                d += item.defense
            }
            return d
        }

    var healing: Double = 1.0
        get() {
            var h = field
            for (item in items) {
                h += item.healing
            }
            return h
        }
    constructor(name: String, position: Position, health: Double, attack : Double, defense: Double, healing : Double) : this (name, position) {
        this.health = health
        this.attack = attack
        this.defense = defense
        this.healing = healing
    }

    override fun toString(): String {
        val description = StringBuilder("")
        description.append("Stav hrdiny\n")
        description.append("===========\n")
        description.append("Jméno        $name \n")
        description.append("Zdraví:      "+ "%.2f".format(health) + "\n")
        description.append("Útok:        "+ "%.2f".format(attack) + "\n")
        description.append("Obrana:      "+ "%.2f".format(defense) + "\n")
        description.append("Uzdravování: "+ "%.2f".format(healing) + "\n")
        description.append("Zabití:      $kills \n")
        return description.toString()
    }

    fun go (direction: Direction) : String {
        position.x += direction.relativeX
        position.y += direction.relativeY
        return "Jdu na ${direction.description}"
    }

    override fun attack(enemy: Character): String {
        val result = super.attack(enemy)
        if (enemy.isDead()) kills += 1
        return result
    }

    fun cutDown(direction: Direction, gamePlan: GamePlan): String {
        val newPosition = Position(position, direction)

        // Zkontroluj, zda je na nové pozici les
        val gameField = gamePlan.getGameField(newPosition)
        if (gameField.terrain == Terrain.FOREST) {
            gameField.terrain = Terrain.MEADOW
            return "Kácej les na ${direction.description}."
        } else {
            return "Nemůžeš kácet na ${direction.description}, protože tam není les."
        }
    }


}