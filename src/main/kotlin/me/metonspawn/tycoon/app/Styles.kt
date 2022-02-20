package me.metonspawn.tycoon.app

import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val bigText by cssclass()
        val cardComponent by cssclass()
        val selected by cssclass()
        val highlighted by cssclass()
        val endButton by cssclass()
        val buttonLocked by cssclass()
        val buttonHover by cssclass()
        val buttonUnhover by cssclass()
        val invisibleScrollpane by cssclass()
    }

    init {
        label and heading {
            padding = box(5.px)
            fontSize = 15.px
            fontWeight = FontWeight.BOLD
            textFill = Color.WHITE
        }
        label and bigText {
            padding = box(5.px)
            fontSize = 30.px
            fontWeight = FontWeight.BOLD
            textFill = Color.BLACK
        }
        label {
            fontFamily = "meiryo"
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
        buttonHover {
            backgroundColor += Color.MEDIUMAQUAMARINE
        }
        buttonUnhover {
            backgroundColor += Color.WHITE
        }
        scrollPane and invisibleScrollpane {
            backgroundColor += Color.TRANSPARENT
            viewport {
                backgroundColor += Color.TRANSPARENT
            }
        }
    }
}