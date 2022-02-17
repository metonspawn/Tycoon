package me.metonspawn.tycoon.view

import tornadofx.*

class MainView: TycoonView() {

    override val content = vbox {
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

    override fun quit() {
        this.close()
    }
}