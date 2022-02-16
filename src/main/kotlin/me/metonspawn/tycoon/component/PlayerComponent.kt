package me.metonspawn.tycoon.component

import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.core.Player
import tornadofx.*
import kotlin.math.min

class PlayerComponent(player: Player, fitWidth: Double): BorderPane() {

    init {
        top = hbox {
            alignment = Pos.CENTER
            label(player.name) {
                textAlignment = TextAlignment.CENTER
                addClass(Styles.heading)
            }
        }
        center = hbox{
            maxWidth = fitWidth
            this.prefWidth = fitWidth
            alignment = Pos.CENTER
            style {
                backgroundColor += Color.TRANSPARENT
            }
            for (i in player.deck) {
                this.add(UnrevealedCard(min(fitWidth/player.deck.size,50.0)))
                println(fitWidth/player.deck.size)
            }
        }
    }
}