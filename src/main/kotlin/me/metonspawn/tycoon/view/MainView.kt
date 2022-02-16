package me.metonspawn.tycoon.view

import tornadofx.*

class MainView : View("Tycoon") {

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

    private fun start() {
        replaceWith<SetupView>()
    }
}