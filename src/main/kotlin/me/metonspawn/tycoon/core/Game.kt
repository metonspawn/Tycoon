package me.metonspawn.tycoon.core

import javafx.stage.StageStyle
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.SetupView
import tornadofx.*
import kotlin.math.max
import kotlinx.serialization.Serializable

@Serializable
class Game(val players: List<Player>, private val deckCount: Int = 1, val pileCount: Int = 4, private val useJokers: Boolean = false, private val gamerules: Gamerules = Gamerules()) {
    private lateinit var board: Board
    private var playerIterator: Int = 0
    private var currentPlayerIndex: Int = 0
    private val finishedPlayers = mutableListOf<Player>()

    private fun deal() {
        players.forEach { it.deck.clear() }
        val cards = mutableListOf<Card>()
        for (decks in 1..deckCount) {
            for (suit in Suit.values()) {
                if (suit == Suit.NONE) {
                    continue
                }
                for (value in 3..3) {
                    cards.add(Card(value, suit))
                }
            }
            if (useJokers) {
                cards.addAll(listOf(Card(16, Suit.NONE),Card(16, Suit.NONE)))
            }
        }
        cards.shuffle()
        for (i in 0 until cards.size) {
            players[i % players.size].deck.add(cards[i])
        }
        println("Dealt")
    }
    fun start() {
        finishedPlayers.clear()
        find(GameView::class).game = this
        board = Board()
        deal()
        turn()
    }

    fun turn() {
        board.push()
        nextPlayer()
        find(GameView::class).update()
    }

    fun forfeitTurn() {
        players[currentPlayerIndex].forfeitTrick = true
        nextPlayer()
        find(GameView::class).update()
    }

    private fun nextPlayer() {
        if(specialEffect()) return

        var playersAllForfeited = true
        var playersAllFinished = true
        for (player in players) { //check if there are any players left
            if (player.finish) continue
            playersAllForfeited = player.forfeitTrick
            if (!playersAllForfeited) break
        }
        for (player in players) { //check if there are any players left
            playersAllFinished = player.finish
            if (!playersAllFinished) break
        }
        if (playersAllFinished) { end(); return }
        if (playersAllForfeited) { endTrick(); return }

        if (getCurrentPlayer().deck.size == 0 && !getCurrentPlayer().finish) {
            getCurrentPlayer().finish = true; finishedPlayers.add(getCurrentPlayer()); println("${getCurrentPlayer()} finished")
        }

        playerIterator++ //repeat until it gets to the next player still in the field
        currentPlayerIndex = playerIterator % players.size
        if (getCurrentPlayer().forfeitTrick || getCurrentPlayer().finish) {
            nextPlayer()
        }
    }

    private fun endTrick() {
        println("Trick End")
        board.clear()
        for (player in players) {
            player.forfeitTrick = false
        }
        while(getCurrentPlayer().finish) {
            playerIterator++
            currentPlayerIndex = playerIterator % players.size
        }
        find(GameView::class).update()
    }

    private fun specialEffect(): Boolean { //return true if it should interrupt nextPlayer()
        var highestCardValue = 0 //8-cutting
        var cardCount = 0
        for (i in 0 until pileCount) {
            cardCount += if ( board.state[i].card.value != 0) { 1 } else { 0 }
            highestCardValue = max(highestCardValue, board.state[i].card.value)
        }
        when (highestCardValue) {
            8 -> if (gamerules.eightCutting) { endTrick(); return true}
            11 -> if (gamerules.elevenBack) { board.elevenBack = !board.elevenBack; return false }
        }
        if (cardCount >= 4) { board.revolution = !board.revolution; println("Hi") }
        return false
    }

    private fun end() {
        println(finishedPlayers)
        players.forEach { it.finish = false}
        val rankedPlayers = mutableListOf<Player>()
        val rankingCount = if (players.size > 3) 2 else 1
        for (i in 0 until rankingCount) {
            rankedPlayers.add(finishedPlayers[i])
        }

        finishedPlayers.forEach { it.title = Title.COMMONER }
        finishedPlayers[0].title = Title.PRESIDENT
        finishedPlayers[finishedPlayers.size-1].title = Title.ASSHOLE
        if (finishedPlayers.size > 3) {
            finishedPlayers[1].title = Title.VICE_PRESIDENT
            finishedPlayers[finishedPlayers.size-2].title = Title.VICE_ASS
        }

        val params = "players" to rankedPlayers.observable()
        find<WinnerFragment>(params = arrayOf(params)).openModal(StageStyle.UNDECORATED, escapeClosesWindow = false)
    }

    fun generateBasicRulesHolder(): SetupView.BasicRulesHolder {
        return SetupView.BasicRulesHolder(deckCount, useJokers, pileCount)
    }
    fun generateGamerulesHolder(): SetupView.GamerulesHolder {
        return SetupView.GamerulesHolder(this.gamerules.eightCutting, this.gamerules.elevenBack)
    }


    fun getBoard(): Board {
        return board
    }

    fun getFinishedPlayers(): List<Player> {
        return this.finishedPlayers.toList()
    }

    fun getCurrentPlayerIndex(): Int {
        return currentPlayerIndex
    }

    fun getCurrentPlayer(): Player {
        return players[currentPlayerIndex]
    }

    @Serializable
    data class Gamerules(val eightCutting: Boolean = true, val elevenBack: Boolean = true)
}