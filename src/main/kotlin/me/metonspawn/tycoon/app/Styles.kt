package me.metonspawn.tycoon.app

import javafx.geometry.Pos
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import me.metonspawn.tycoon.component.CardComponent
import tornadofx.*
import tornadofx.Stylesheet.Companion.box

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val cardComponent by cssclass()
        val selected by cssclass()
        val highlighted by cssclass()
        val endButton by cssclass()
        val buttonLocked by cssclass()
    }

    init {
        label and heading {
            padding = box(5.px)
            fontSize = 15.px
            fontWeight = FontWeight.BOLD
        }
        cardComponent {
            backgroundColor += Color.WHITE
            borderColor += box(c("#000000"))
        }
        selected {
            borderColor += box(c("#ff0000"))
        }
        highlighted {
            borderColor += box(Color.BLUE)
            borderWidth += CssBox(2.px,2.px,2.px,2.px)
            borderStyle += BorderStrokeStyle.SOLID
        }
        endButton {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
        buttonLocked {
            backgroundColor += Color.DARKGRAY
        }
    }
}