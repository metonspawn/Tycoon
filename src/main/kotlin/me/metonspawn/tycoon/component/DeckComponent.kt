package me.metonspawn.tycoon.component

import javafx.scene.paint.Color
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.*


class DeckComponent(card: Card): CardComponent(card) {

    init {
        init()
        setOnMouseClicked {
            println("Clicked ${card.value}, ${card.suit}")
            val game = find(MainView::class).game!!
            val gameView = find(GameView::class)
            val board = game.getBoard()
            if (gameView.selectedCard == null) { //selection-deselection
                gameView.selectCard(this)
                println("Selected card is ${gameView.selectedCard!!.card.value}, ${gameView.selectedCard!!.card.suit}")
            } else {
                gameView.deselectCard()
                println("Deselected")
            }
            //gameView.refresh() //don't need it here, since there is virtually no change to the number of cards in the deck
        }
    }
}