package me.metonspawn.tycoon.core

import kotlinx.serialization.Serializable
import me.metonspawn.tycoon.view.GameView
import tornadofx.find

@Serializable
enum class Suit {
    SPADE, HEART, DIAMOND, CLOVER, NONE
}

@Serializable
class Card(val value: Int, val suit: Suit) {
    fun check(pile: Pile): Boolean {
        val board = find(GameView::class).game.getBoard()

        if (pile.card.value == 0) return true
        if (this.value == 16) return true
        if (pile.lock && pile.card.suit != this.suit) return false
        return if (board.revolution xor board.elevenBack) {
            (pile.card.value >= this.value)
        } else {
            (pile.card.value <= this.value)
        }
    }
}