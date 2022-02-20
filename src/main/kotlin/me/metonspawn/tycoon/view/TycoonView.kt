package me.metonspawn.tycoon.view

import javafx.scene.Parent
import javafx.scene.input.KeyCombination
import javafx.stage.FileChooser
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.metonspawn.tycoon.core.Game
import me.metonspawn.tycoon.util.I18n
import me.metonspawn.tycoon.util.I18n.bindMessage
import tornadofx.*
import java.io.File
import java.io.FileWriter
import java.util.*
import javax.swing.filechooser.FileSystemView

abstract class TycoonView: View() {
    abstract val content: Parent

    init {
        super.titleProperty.bindMessage("tycoon")
    }

    override val root = vbox {
        setPrefSize(800.0,600.0)
        menubar {
            menu("Game") {
                textProperty().bindMessage("game")
                item(I18n.messageBinding("load"), KeyCombination.keyCombination("Ctrl+O")) {
                    action {
                        load(this@TycoonView)
                    }
                }
                item(I18n.messageBinding("save"), KeyCombination.keyCombination("Ctrl+S")) {
                    action {
                        save()
                    }
                }
                item(I18n.messageBinding("quit"), KeyCombination.keyCombination("Esc")) {
                    action {
                        quit()
                    }
                }
            }
            menu("Language") {
                textProperty().bindMessage("language")
                item("English") {
                    action {
                        FX.locale = Locale.ENGLISH
                    }
                }
                item("日本語") {
                    action {
                        FX.locale = Locale.JAPANESE
                    }
                }
            }
        }
        add(content?:vbox())
    }

    fun save() {
        val gameView = find(GameView::class)
        if (!gameView.isGameRunning()) return
        val game = gameView.game
        try {
            val string = Json.encodeToString(game)
            println(string)
            val file = chooseFile("Save as", filters = arrayOf(
                FileChooser.ExtensionFilter(
                    I18n.messageBinding("tycoonGameSave").get(), "*.dfg"
                ), FileChooser.ExtensionFilter(I18n.messageBinding("allFiles").get(), "*.*")
            ), mode = FileChooserMode.Save) {
                initialDirectory = File(FileSystemView.getFileSystemView().defaultDirectory.path) //Documents folder path
                titleProperty().bindMessage("saveGame")
            }[0]
            val fileWriter = FileWriter(file)
            fileWriter.write(string)
            fileWriter.close()
            println("Saved game")
        } catch (_: Exception) { println("Failed to write") }
    }

    fun load(viewToReplace: View = this) {
        try {
            val file = chooseFile("Choose a save file", filters = arrayOf(
                FileChooser.ExtensionFilter(
                    I18n.messageBinding(
                        "tycoonGameSave"
                    ).get(), "*.dfg"
                ), FileChooser.ExtensionFilter(I18n.messageBinding("allFiles").get(), "*.*")
            ), mode = FileChooserMode.Single) {
                initialDirectory = File(FileSystemView.getFileSystemView().defaultDirectory.path)
                titleProperty().bindMessage("loadGame")
            }[0]
            val string = file.readText()
            val game = Json.decodeFromString<Game>(string)
            find(GameView::class).game = game
            viewToReplace.replaceWith<GameView>()
            find(GameView::class).update()
            println("Loaded game")
        } catch (_: Exception) { println("Failed to load") }
    }

    override fun onDock() {
        primaryStage.isResizable = false
    }

    open fun quit() {
        replaceWith<MainView>()
    }
}