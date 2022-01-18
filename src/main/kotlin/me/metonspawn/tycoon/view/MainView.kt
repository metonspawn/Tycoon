package me.metonspawn.tycoon.view

import me.metonspawn.tycoon.core.Game
import tornadofx.*

class MainView : View("Tycoon") {
    var game: Game? = null

    override val root = vbox {
        setPrefSize(800.0,600.0)
        menubar {
            menu("Game") {
                item("Save")
                item("Load")
            }
            menu("Language") {
                item("English")
            }
        }
        hbox {
            setPrefSize(800.0,300.0)
        }
        vbox {
            hbox {
                vbox {
                    button("Hi") {
                        action {
                            start()
                        }
                    }
                    label("Maybe")
                    label("quit")
                }
            }
        }
    }

    fun start() {
        replaceWith<GameView>()
        game = Game(2)
        game!!.start()
    }
}