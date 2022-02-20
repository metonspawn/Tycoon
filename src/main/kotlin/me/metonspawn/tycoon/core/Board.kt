package me.metonspawn.tycoon.core

import kotlin.math.max
import kotlinx.serialization.Serializable
import me.metonspawn.tycoon.view.GameView
import tornadofx.*

@Serializable
class Board {
    var revolution = false
    var elevenBack = false
    val state = mutableListOf<Pile>()
    val tempState = mutableListOf<Pile>()

    fun clear() {
        val game = find(GameView::class).game
        tempState.clear()
        state.clear()
        for (i in 0 until game.pileCount) {
            tempState.add(Pile(Card(0,Suit.NONE)))
            state.add(Pile(Card(0,Suit.NONE)))
        }
        elevenBack = false
    }

    init {
        revolution = false
        if (state.isEmpty()) { clear() }
    }

    fun undo(pileIndex: Int) {
        println("from tempState: ${tempState[pileIndex].card.value}, state: ${state[pileIndex].card.value}")
        tempState[pileIndex].card = Card(0,Suit.NONE)
        println("to tempState: ${tempState[pileIndex].card.value}, state: ${state[pileIndex].card.value}")
    }

    fun push() {
        val game = find(GameView::class).game
        var otherTempStatePileValues = 0
        for (i in 0 until game.pileCount) {
            if (tempState[i].card.value == 16) continue
            otherTempStatePileValues = max(otherTempStatePileValues,tempState[i].card.value)
        }
        for (i in 0 until game.pileCount) {
            val value: Int = if (tempState[i].card.value == 16) { if (otherTempStatePileValues == 0) {16} else {otherTempStatePileValues}} else {tempState[i].card.value} //joker should act as a joker when used standalone, but should mute to the other cards when used as a complement
            state[i].card = Card(value, tempState[i].card.suit)
            tempState[i].card = Card(0,Suit.NONE)
            state[i].lock = tempState[i].lock
        }
    }
}

@Serializable
data class Pile(var card: Card, var lock: Boolean  = false)