package me.metonspawn.tycoon.view

import me.metonspawn.tycoon.component.CardComponent
import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.core.Pile
import tornadofx.find
import kotlin.math.max

class PileComponent(card: Card, val pileIndex: Int): CardComponent(card) {
    init {
        init()
        setOnMouseClicked {
            println("Clicked ${pileIndex}th pile")
            val gameView = find(GameView::class)
            if (gameView.selectedCard != null) { //card placement
                setCard()
            } else {
                removeCard()
            }
            gameView.refresh()
        }
    }

    private fun setCard() {
        println("Setting..")
        val gameView = find(GameView::class)
        val game = find(MainView::class).game!!
        val board = game.getBoard()
        val selectedCard = gameView.selectedCard!!.card
        println("Selected card is ${selectedCard.value}, ${selectedCard.suit}")
        if (board.tempState[pileIndex].card.value != 0) return
        if (selectedCard.check(board.state[pileIndex])) {
            println("Value check passed")
            var otherPileValue = 0
            for (pile in board.tempState) {
                otherPileValue = if (pile.card.value != 0) {pile.card.value} else {0}
                if (otherPileValue != 0) { break }
            }
            if (selectedCard.value == otherPileValue || otherPileValue == 0) { //same-value check
                println("Same-value check passed")
                board.tempState[pileIndex].card = selectedCard
                game.getCurrentPlayer().deck.remove(selectedCard)
                gameView.selectedCard = null
            }
        }
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