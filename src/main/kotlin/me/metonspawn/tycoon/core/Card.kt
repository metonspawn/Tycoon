package me.metonspawn.tycoon.core

enum class Suit {
    SPADE, HEART, DIAMOND, CLOVER, NONE
}

class Card(val value: Int, val suit: Suit) {

    fun play(pile: Pile) {
        pile.card = this
    }

    fun check(pile: Pile): Boolean {
        if (pile.lock && pile.card.suit != this.suit) return false
        return if (!pile.board.revolution) {
            (pile.card.value <= this.value)
        } else {
            (pile.card.value >= this.value)
        }
    }
}