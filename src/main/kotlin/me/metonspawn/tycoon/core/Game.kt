package me.metonspawn.tycoon.core

import me.metonspawn.tycoon.view.GameView
import tornadofx.*

class Game(playerCount: Int = 2) {
    val players = mutableListOf<Player>()
    private var board: Board? = null
    private var turn: Int = 0
    private var currentPlayer: Player? = null

    init {
        for (i in 1..playerCount) {
            players.add(Player())
        }
    }

    fun deal() {
        val cards = mutableListOf<Card>()
        for (suit in Suit.values()) {
            if (suit == Suit.NONE) { continue }
            for (value in 3..15) {
                cards.add(Card(value,suit))
            }
        }
        cards.shuffle()
        for (i in 0 until cards.size) {
            players[i % players.size].deck.add(cards[i])
        }
        println("Dealt")
    }
    fun start() {
        board = Board(this)
        deal()
        var running = true
        turn()
    }

    fun turn() {
        turn++
        currentPlayer = players[turn % players.size]
        board!!.push()
        find(GameView::class).refresh()
    }

    fun getBoard(): Board {
        return board!!
    }

    fun getTurn(): Int {
        return turn
    }

    fun getCurrentPlayer(): Player {
        return currentPlayer!!
    }
}