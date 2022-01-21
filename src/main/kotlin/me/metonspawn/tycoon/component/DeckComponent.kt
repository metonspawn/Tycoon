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
            val game = find(MainView::class).game!!
            val gameView = find(GameView::class)
            val board = game.getBoard()
            if (gameView.selectedCard == null) { //selection-deselection
                gameView.selectCard(this)
            } else {
                gameView.deselectCard()
            }
            //gameView.refresh() //don't need it here, since there is virtually no change to the number of cards in the deck
        }
    }
}