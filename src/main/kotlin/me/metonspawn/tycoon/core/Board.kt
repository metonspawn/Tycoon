package me.metonspawn.tycoon.core

class Board(val game: Game) {
    var revolution = false
    val state = mutableListOf<Pile>()
    val tempState = mutableListOf<Pile>()

    fun clear() {
        state.clear()
    }

    init {
        revolution = false
        for (i in 0..3) {
            tempState.add(Pile(Card(0,Suit.NONE), this))
            state.add(Pile(Card(0,Suit.NONE), this))
        }
    }

    fun undo(pileIndex: Int) {
        println("from tempState: ${tempState[pileIndex].card.value}, state: ${state[pileIndex].card.value}")
        tempState[pileIndex].card = state[pileIndex].card
        println("to tempState: ${tempState[pileIndex].card.value}, state: ${state[pileIndex].card.value}")
    }

    private fun push() {
        for (i in 0..3) {
            state[i] = tempState[i]
            tempState[i].card = Card(0,Suit.NONE)
        }
    }
}

data class Pile(var card: Card, val board: Board, var lock: Boolean  = false)