package me.metonspawn.tycoon.app

import me.metonspawn.tycoon.util.Files
import me.metonspawn.tycoon.view.MainView
import tornadofx.*

class Tycoon: App(MainView::class, Styles::class) {
    override fun start(stage: javafx.stage.Stage) {
        super.start(stage)
        setStageIcon(Files.ICON.image)
    }
}