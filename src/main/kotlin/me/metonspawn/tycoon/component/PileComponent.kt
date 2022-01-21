package me.metonspawn.tycoon.component

import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.find

class PileComponent(card: Card, val pileIndex: Int): CardComponent(card) {
    init {
        init()
        setOnMouseClicked {
            val gameView = find(GameView::class)
            if (gameView.selectedCard != null) { //card placement
                if (setCard()) { //if successful then refresh
                    gameView.refresh()
                }
            } else {
                removeCard()
                gameView.refresh()
            }
        }
    }

    private fun setCard(): Boolean {
        if (!checkSettable()) return false
        val gameView = find(GameView::class)
        val game = find(MainView::class).game!!
        val board = game.getBoard()
        val selectedCard = gameView.selectedCard!!.card
        board.tempState[pileIndex].card = selectedCard
        game.getCurrentPlayer().deck.remove(selectedCard)
        gameView.selectedCard = null
        return true
    }

    fun checkSettable(): Boolean {
        val gameView = find(GameView::class)
        val game = find(MainView::class).game!!
        val board = game.getBoard()
        val selectedCard = gameView.selectedCard!!.card

        if (board.tempState[pileIndex].card.value != 0) return false //pile vacancy check

        var otherStatePileValue = 0 //eliminate piles which did not have a card placed on them the previous turn
        for (pile in board.state) { //get the value of placed cards, if there are any
            otherStatePileValue =  if (pile.card.value != 0) {pile.card.value} else {0}
            if (otherStatePileValue != 0) { break }
        }
        if (board.state[pileIndex].card.value != otherStatePileValue) return false

        if (selectedCard.check(board.state[pileIndex])) {
            var otherTempPileValue = 0 //same-value check
            for (pileIndex in 0..3) { //get the value of the other placed cards, if there are any
                otherTempPileValue = if (board.tempState[pileIndex].card.value != 0) {board.tempState[pileIndex].card.value} else {0}
                if (otherTempPileValue != 0) { break }
            }
            if (selectedCard.value == otherTempPileValue || otherTempPileValue == 0) {
                return true
            }
        }; return false
    }

    private fun removeCard() {
        val gameView = find(GameView::class)
        val game = find(MainView::class).game!!
        val board = game.getBoard()
        if (board.tempState[pileIndex].card.value != 0) {
            board.undo(pileIndex)
            game.getCurrentPlayer().deck.add(card)
            gameView.refresh()
        }
    }
}