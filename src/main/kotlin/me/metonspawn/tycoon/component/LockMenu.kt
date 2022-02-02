package me.metonspawn.tycoon.component

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.util.Files
import me.metonspawn.tycoon.view.MainView
import tornadofx.*

class LockMenu(val parent: PileComponent): BorderPane() {
    private var image: ImageView? = null
    private var label: Label? = null

    init {
        setMaxSize(55.0,20.0)
        setPrefSize(55.0,20.0)
        this.center = hbox {
            alignment = Pos.CENTER_LEFT
            image = imageview(Files.UNLOCKED.image) {
                fitWidth = 16.0
                fitHeight = 16.0
            }
            onHover {
                update()
            }
            setOnMouseClicked {
                toggleLock()
            }
            label = label("Lock")
        }
        update()
        updateText()
    }


    private fun update() {
        if (!parent.isLockToggleable()) {
            this.addClass(Styles.buttonLocked)
            return
        }
        if (this.isHover) {
            this.removeClass(Styles.buttonUnhover)
            this.addClass(Styles.buttonHover)
            if (parent.getLocked()) {
                image!!.imageProperty().set(Files.UNLOCKED.image)
            } else {
                image!!.imageProperty().set(Files.LOCKED.image)
            }
        } else {
            this.removeClass(Styles.buttonHover)
            this.addClass(Styles.buttonUnhover)
            if (parent.getLocked()) {
                image!!.imageProperty().set(Files.LOCKED.image)
            } else {
                image!!.imageProperty().set(Files.UNLOCKED.image)
            }
        }
    }

    private fun toggleLock() {
        if (!parent.isLockToggleable()) return
        parent.toggleLock()
        updateText()
        println(parent.getLocked())
    }

    private fun updateText() {
        label!!.text = if (parent.getLocked()) { "Unlock" } else { "Lock" }
    }
}