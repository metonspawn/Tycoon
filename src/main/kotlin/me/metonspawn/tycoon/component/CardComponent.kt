package me.metonspawn.tycoon.component

import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.core.Pile
import me.metonspawn.tycoon.core.Suit
import me.metonspawn.tycoon.util.Files
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.*

abstract class CardComponent(val card: Card): BorderPane() {

    protected fun init() {
        val image = when (card.suit) {
            Suit.CLOVER -> Files.CLOVER.image
            Suit.HEART -> Files.HEART.image
            Suit.DIAMOND -> Files.DIAMOND.image
            Suit.SPADE -> Files.SPADE.image
            else -> Files.NONE.image
        }
        setPrefSize(50.0,80.0)
        style {
            addClass(Styles.cardComponent)
            if (card.value == 0) { borderStyle += BorderStrokeStyle.DASHED }
        }
        this.top = borderpane {
            left = label("${card.value}") {
            }
            right = imageview(image) {
                fitHeight = 15.0
                fitWidth = 15.0
            }
        }
        this.bottom = borderpane {
            left = imageview(image) {
                fitHeight = 15.0
                fitWidth = 15.0
            }
            right = label("${card.value}")
        }
    }
}

