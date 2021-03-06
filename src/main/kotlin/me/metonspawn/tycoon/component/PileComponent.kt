package me.metonspawn.tycoon.component

import javafx.scene.input.MouseButton
import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.core.Suit
import me.metonspawn.tycoon.view.GameView
import tornadofx.*

class PileComponent(card: Card, private val pileIndex: Int): CardComponent(card) {
    private val gameView = find(GameView::class)
    private val game = find(GameView::class).game
    private val board = game.getBoard()

    init {
        init()
        setOnMouseClicked {
            if (it.button == MouseButton.SECONDARY) return@setOnMouseClicked
            if (gameView.selectedCard != null) { //card placement
                if (setCard()) { //if successful then refresh
                    gameView.update()
                }
            } else {
                removeCard()
                gameView.update()
            }
        }
    }

    private fun setCard(): Boolean {
        if (!checkSettable()) return false
        val selectedCard = gameView.selectedCard!!.card
        board.tempState[pileIndex].card = selectedCard
        game.getCurrentPlayer().deck.remove(selectedCard)
        gameView.selectedCard = null
        return true
    }

     fun checkSettable(): Boolean {
        val selectedCard = gameView.selectedCard!!.card
        if (board.tempState[pileIndex].card.value != 0) return false //pile vacancy check

        var otherStatePileValue = 0 //eliminate piles which did not have a card placed on them the previous turn
        for (pile in board.state) { //get the value of cards the previous turn, if there are any
            otherStatePileValue = pile.card.value
            if (otherStatePileValue != 0) { break }
        }
        if (board.state[pileIndex].card.value != otherStatePileValue) return false //cannot simply check for 0, since you wouldn't be able to place cards at first then

        if (selectedCard.check(board.state[pileIndex])) {
            var otherTempPileValue = 0 //same-value check
            for (pileIndex in 0 until game.pileCount) { //get the value of the other placed cards, if there are any, excluding jokers
                if (board.tempState[pileIndex].card.value == 0 || board.tempState[pileIndex].card.value == 16) continue
                otherTempPileValue = board.tempState[pileIndex].card.value
                if (otherTempPileValue != 0) { break }
            }
            if (selectedCard.value == otherTempPileValue || otherTempPileValue == 0 || selectedCard.value == 16) {
                return true
            }
        }; return false
    }

    private fun removeCard() {
        if (board.tempState[pileIndex].card.value != 0) {
            board.undo(pileIndex)
            game.getCurrentPlayer().deck.add(card)
            gameView.update()
        }
    }

    fun getLocked(): Boolean { //the pile has either been locked in the past OR it is being locked in the current turn
        return (board.state[pileIndex].lock || board.tempState[pileIndex].lock)
    }

    fun toggleLock() {
        board.tempState[pileIndex].lock = !board.tempState[pileIndex].lock
    }

    fun isLockToggleable(): Boolean { //if the pile has been locked in the past, it may not be unlocked later; the player must also be placing a card of the same suit as the one played one turn prior
        if (board.tempState[pileIndex].card.suit == Suit.NONE) return false //cannot lock empty/joker piles
        if (board.state[pileIndex].lock) return false
        return (board.state[pileIndex].card.suit == board.tempState[pileIndex].card.suit)
    }
}