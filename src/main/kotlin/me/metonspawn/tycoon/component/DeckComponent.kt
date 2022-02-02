package me.metonspawn.tycoon.component

import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.*


class DeckComponent(card: Card): CardComponent(card) {

    init {
        init()
        setOnMouseClicked {
            val gameView = find(GameView::class)
            if (gameView.selectedCard == null) { //selection-deselection
                gameView.selectCard(this)
            } else {
                gameView.deselectCard()
            }
        }
    }
}