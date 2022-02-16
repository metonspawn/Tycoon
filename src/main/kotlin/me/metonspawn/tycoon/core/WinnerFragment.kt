package me.metonspawn.tycoon.core

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.view.GameView
import me.metonspawn.tycoon.view.SetupView
import tornadofx.*

class WinnerFragment: Fragment() {
    private val players: ObservableList<Player> by param()

    override val root = borderpane {
        top = label("The winning players are:") {
            textAlignment = TextAlignment.CENTER
            addClass(Styles.bigText)
            style { fontSize = 15.px }
        }
        center = vbox {
            alignment = Pos.CENTER
            for (i in 0 until players.size) {
                this.add(label("${i+1}. ${players[i].name}").apply {
                    addClass(Styles.bigText)
                })
            }
        }
        bottom = hbox(8) {
            alignment = Pos.CENTER
            button("Replay") {
                action {
                    find<GameView>().replay()
                    this@WinnerFragment.close()
                }
            }
            button("End") {
                action {
                    find<GameView>().quit()
                    this@WinnerFragment.close()
                }
            }
        }
    }
}