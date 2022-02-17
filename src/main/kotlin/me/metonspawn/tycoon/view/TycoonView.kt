package me.metonspawn.tycoon.view

import javafx.scene.Parent
import tornadofx.*

abstract class TycoonView: View("Tycoon") {
    abstract val content: Parent

    override val root = vbox {
        setPrefSize(800.0,600.0)
        menubar {
            menu("Game") {
                item("Load","Ctrl+O") {
                    action {
                        find(GameView::class).load(this@TycoonView)
                    }
                }
                item("Save","Ctrl+S") {
                    action {
                        find(GameView::class).save()
                    }
                }
                item("Quit") {
                    action {
                        quit()
                    }
                }
            }
            menu("Language") {
                item("English")
            }
        }
        add(content?:vbox())
    }

    override fun onDock() {
        primaryStage.isResizable = false
    }

    open fun quit() {
        replaceWith<MainView>()
    }
}