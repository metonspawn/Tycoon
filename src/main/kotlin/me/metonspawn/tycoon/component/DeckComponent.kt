package me.metonspawn.tycoon.component

import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.*


class DeckComponent(card: Card): CardComponent(card) {

    init {
        init()
        //addClass(Styles.selected) //<- wtf it works if i do it here
        setOnMouseClicked {
            println(super.getStyleClass())
            removeClass(Styles.cardComponent)
            println(super.getStyleClass())
            println("Clicked ${card.value}, ${card.suit}")
            val game = find(MainView::class).game!!
            val gameView = find(GameView::class)
            val board = game.getBoard()
            if (gameView.selectedCard == null) { //selection-deselection
                gameView.selectCard(this)
                addClass(Styles.selected) //<- but it doesn't do anything if i do it here????
                println("Selected card is ${gameView.selectedCard!!.card.value}, ${gameView.selectedCard!!.card.suit}")
            } else {
                gameView.deselectCard()
                println("Deselected")
            }
            gameView.refresh()
        }
    }
}