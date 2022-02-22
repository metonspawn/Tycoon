package me.metonspawn.tycoon.view

import javafx.geometry.Pos
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.util.Files
import me.metonspawn.tycoon.util.I18n
import tornadofx.*

class MainView: TycoonView() {

    override val content = vbox {
        hbox {
            setPrefSize(800.0,200.0)
            alignment = Pos.CENTER_LEFT
            label(I18n.messageBinding("tycoon")) {
                style {
                    addClass(Styles.titleText)
                }
            }
        }
        vbox(20) {
            translateX = 50.0
            hbox {
                translateX = -15.0
                imageview(Files.ICON.image)
                label(I18n.messageBinding("start")) {
                    translateX = -15.0
                    addClass(Styles.immenseText)
                    onHover {
                        style {
                            textFill = if (it) javafx.scene.paint.Color.BLUE else javafx.scene.paint.Color.BLACK
                        }
                    }
                    setOnMouseClicked { start() }
                }
            }
            hbox {
                imageview(Files.BACK.image)
                label(I18n.messageBinding("load")) {
                    addClass(Styles.immenseText)
                    onHover {
                        style {
                            textFill = if (it) javafx.scene.paint.Color.BLUE else javafx.scene.paint.Color.BLACK
                        }
                    }
                    setOnMouseClicked { load() }
                }
            }
            hbox {
                imageview(Files.FIVE_ICON.image)
                label(I18n.messageBinding("quit")) {
                    addClass(Styles.immenseText)
                    onHover {
                        style {
                            textFill = if (it) javafx.scene.paint.Color.BLUE else javafx.scene.paint.Color.BLACK
                        }
                    }
                    setOnMouseClicked { close() }
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