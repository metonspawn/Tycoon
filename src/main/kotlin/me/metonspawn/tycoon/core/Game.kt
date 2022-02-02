package me.metonspawn.tycoon.core

import me.metonspawn.tycoon.view.GameView
import tornadofx.*
import kotlin.math.max

class Game(playerCount: Int = 2) {
    private val players = mutableListOf<Player>()
    private val gamerules = Gamerules()
    private lateinit var board: Board
    private var playerIterator: Int = 0
    private lateinit var currentPlayer: Player
    val pileCount = 5

    init {
        for (i in 1..playerCount) {
            players.add(Player())
        }
    }

    private fun deal() {
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
        turn()
    }

    fun turn() {
        board.push()
        nextPlayer()
        find(GameView::class).update()
    }

    fun forfeitTurn() {
        currentPlayer.forfeitTrick = true
        nextPlayer()
        find(GameView::class).update()
    }

    private fun nextPlayer() {
        if(specialEffect()) return

        var playersAllForfeited = true
        for (player in players) { //check if there are any players left
            playersAllForfeited = player.forfeitTrick
            if (!playersAllForfeited) break
        }
        if (playersAllForfeited) { endTrick(); return }

        playerIterator++ //repeat until it gets to the next player still in the field
        currentPlayer = players[playerIterator % players.size]
        if (currentPlayer.forfeitTrick) {nextPlayer()}
    }

    private fun endTrick() {
        println("Trick End")
        board.clear()
        for (player in players) {
            player.forfeitTrick = false
        }
        find(GameView::class).update()
    }

    private fun specialEffect(): Boolean { //return true if it should interrupt nextPlayer()
        var highestCardValue = 0 //8-cutting
        for (i in 0..3) {
            highestCardValue = max(highestCardValue, board.state[i].card.value)
        }
        when (highestCardValue) {
            8 -> if (gamerules.eightCutting) { endTrick(); return true}
            11 -> if (gamerules.elevenBack) { board.elevenBack = !board.elevenBack; return false }
        }
        return false
    }

    fun getBoard(): Board {
        return board
    }

    fun getCurrentPlayer(): Player {
        return currentPlayer
    }

    data class Gamerules(var eightCutting: Boolean = true, var elevenBack: Boolean = true)
}