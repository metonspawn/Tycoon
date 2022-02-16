package me.metonspawn.tycoon.component

import javafx.scene.layout.BorderPane
import me.metonspawn.tycoon.util.Files
import tornadofx.*

class UnrevealedCard(fitWidth: Double): BorderPane() {
    init {
        center = imageview(Files.BACK.image) {
            preserveRatioProperty().set(false)
            this.fitWidth = fitWidth
            fitHeight = 80.0
        }
        setMaxSize(50.0,80.0)
    }
}