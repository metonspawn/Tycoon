package me.metonspawn.tycoon.component

import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.find

class PileComponent(card: Card, val pileIndex: Int): CardComponent(card) {
    init {
        init()
        setOnMouseClicked {
            println("Clicked ${pileIndex}th pile")
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
        if (board.tempState[pileIndex].card.value != 0) return false
        if (selectedCard.check(board.state[pileIndex])) {
            var otherPileValue = 0 //same-value check
            for (pile in board.tempState) { //get the value of the other placed cards, if there are any
                otherPileValue = if (pile.card.value != 0) {pile.card.value} else {0}
                if (otherPileValue != 0) { break }
            }
            if (selectedCard.value == otherPileValue || otherPileValue == 0) {
                return true
            }
        }; return false
    }

    private fun removeCard() {
        println("Removal..")
        val gameView = find(GameView::class)
        val game = find(MainView::class).game!!
        val board = game.getBoard()
        if (board.tempState[pileIndex].card.value != 0) {
            println("Pile exists")
            board.undo(pileIndex)
            game.getCurrentPlayer().deck.add(card)
            gameView.refresh()
        }
    }
}