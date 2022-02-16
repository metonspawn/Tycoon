package me.metonspawn.tycoon.core

import kotlin.math.max

class Board(private val game: Game) {
    var revolution = false
    var elevenBack = false
    val state = mutableListOf<Pile>()
    val tempState = mutableListOf<Pile>()

    fun clear() {
        tempState.clear()
        state.clear()
        for (i in 0 until game.pileCount) {
            tempState.add(Pile(Card(0,Suit.NONE), this))
            state.add(Pile(Card(0,Suit.NONE), this))
        }
        elevenBack = false
    }

    init {
        revolution = false
        clear()
    }

    fun undo(pileIndex: Int) {
        println("from tempState: ${tempState[pileIndex].card.value}, state: ${state[pileIndex].card.value}")
        tempState[pileIndex].card = Card(0,Suit.NONE)
        println("to tempState: ${tempState[pileIndex].card.value}, state: ${state[pileIndex].card.value}")
    }

    fun push() {
        var otherTempStatePileValues = 0
        for (i in 0 until game.pileCount) {
            if (tempState[i].card.value == 16) continue
            otherTempStatePileValues = max(otherTempStatePileValues,tempState[i].card.value)
        }
        for (i in 0 until game.pileCount) {
            val value = if (tempState[i].card.value == 16) {otherTempStatePileValues} else {tempState[i].card.value}
            state[i].card = Card(value, tempState[i].card.suit)
            tempState[i].card = Card(0,Suit.NONE)
            state[i].lock = tempState[i].lock
        }
    }
}

data class Pile(var card: Card, val board: Board, var lock: Boolean  = false)