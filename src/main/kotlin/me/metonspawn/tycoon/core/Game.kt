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
        while (running) {
            turn++
            currentPlayer = players[turn % players.size]
            find(GameView::class).refresh()
            //now it's time to wait for the user to click around the cards and you know, actually play
            //this probably means that code execution has to wait until user input is completed, but waits and resumes are probably not a good way to do clean code
            running = false
        }
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