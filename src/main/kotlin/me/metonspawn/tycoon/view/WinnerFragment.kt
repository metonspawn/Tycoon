package me.metonspawn.tycoon.view

import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.core.Player
import me.metonspawn.tycoon.util.I18n
import tornadofx.*

class WinnerFragment: Fragment() {
    private val players: ObservableList<Player> by param()

    override val root = borderpane {
        top = label(I18n.messageBinding("winners")) {
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
            button(I18n.messageBinding("replay")) {
                action {
                    find<GameView>().replay()
                    this@WinnerFragment.close()
                }
            }
            button(I18n.messageBinding("quit")) {
                action {
                    find<GameView>().quit()
                    this@WinnerFragment.close()
                }
            }
        }
    }
}