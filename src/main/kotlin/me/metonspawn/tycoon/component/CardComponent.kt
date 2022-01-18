package me.metonspawn.tycoon.component

import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.core.Card
import me.metonspawn.tycoon.core.Pile
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.MainView
import tornadofx.*

abstract class CardComponent(val card: Card): BorderPane() {

    protected fun init() {
        setPrefSize(50.0,80.0)
        style {
            addClass(Styles.cardComponent)
            //addClass(Styles.selected)
            if (card.value == 0) { borderStyle += BorderStrokeStyle.DASHED }
        }
        this.top = hbox {
            label("${card.value}, ${card.suit}")
        }
    }
}

