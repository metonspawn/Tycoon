package me.metonspawn.tycoon.view

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.stage.FileChooser.ExtensionFilter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.component.DeckComponent
import me.metonspawn.tycoon.component.LockMenu
import me.metonspawn.tycoon.component.PileComponent
import me.metonspawn.tycoon.component.PlayerComponent
import me.metonspawn.tycoon.core.Game
import tornadofx.*
import java.io.File
import java.io.FileWriter
import javax.swing.filechooser.FileSystemView

class GameView: TycoonView() {
    lateinit var game: Game
    private var deckBox: HBox by singleAssign()
    private var endButton: Button by singleAssign()
    private var pileBox: HBox by singleAssign()
    private var playerBox: HBox by singleAssign()
    private var checkBox: HBox by singleAssign()
    var selectedCard: DeckComponent? = null

    override val content = borderpane {
        style {
            backgroundColor += c("#0c4226")
        }
        top = hbox(8) {
            alignment = Pos.TOP_CENTER
            setPrefSize(800.0,100.0)
            maxWidth = 800.0
            playerBox = this
        }
        center = borderpane {
            setPrefSize(800.0,400.0)
            top = hbox(4) {
                setPrefSize(800.0,150.0)
                alignment = Pos.BOTTOM_CENTER
                paddingBottom = 10
                checkBox = this
            }
            center = hbox(8) {
                alignment = Pos.CENTER
                pileBox = this
            }
            bottom = hbox {
                setPrefSize(800.0,150.0)
                alignment = Pos.CENTER_RIGHT
                button("End Turn") {
                    addClass(Styles.endButton)
                    setPrefSize(120.0,50.0)
                    action {
                        when (validatePlay()) {
                            PlayValidity.INVALID -> return@action
                            PlayValidity.FORFEIT -> game.forfeitTurn()
                            PlayValidity.VALID -> game.turn()
                        }
                    }
                    endButton = this
                }
                vbox { //using this to position the button because I'm a bad developer
                    setPrefSize(100.0,150.0)
                }
            }
        }
        bottom = vbox {
            setPrefSize(800.0,100.0)
            alignment = Pos.TOP_CENTER
            label("Deck") {
                addClass(Styles.heading)
            }
            hbox {
                alignment = Pos.CENTER
                deckBox = this
            }
        }
    }

    private fun updateDeck() {
        deckBox.clear()
        val cards = game.getCurrentPlayer().deck.sortedBy { it.value }
        for (card in cards) {
            deckBox.add(DeckComponent(card))
        }
    }

    private fun updateBoard() {
        pileBox.clear()
        checkBox.clear()
        val board = game.getBoard()
        for (i in 0 until game.pileCount) {
            val stateCard = board.state[i]
            val tempCard = board.tempState[i]
            lateinit var card: PileComponent
            if (tempCard.card.value != 0) { //if there is no tempState card, it means that it has been removed, and as such the state card should be shown
                card = PileComponent(tempCard.card, i)
                pileBox.add(card)
            } else {
                card = PileComponent(stateCard.card, i)
                pileBox.add(card)
            }
            checkBox.add(LockMenu(card)) //adding here to pass around the instance
        }
    }

    private fun updatePlayers() {
        playerBox.clear()
        val players = game.players
        for (i in players.indices) {
            if (i == game.getCurrentPlayerIndex()) continue
            playerBox.add(PlayerComponent(players[i], 792.0/players.size))
        }
    }

    fun update() {
        updateBoard(); updateDeck(); updatePlayers()
        if (validatePlay() != PlayValidity.INVALID) {endButton.removeClass(Styles.buttonLocked)} else {endButton.addClass(Styles.buttonLocked)}
    }

    fun selectCard(component: DeckComponent) {
        this.selectedCard = component
        selectedCard!!.addClass(Styles.selected)
        highlightPiles()
    }

    fun deselectCard() {
        if (selectedCard == null) return
        selectedCard!!.removeClass(Styles.selected)
        ceaseHighlightPiles()
        this.selectedCard = null
    }

    private fun highlightPiles() { //highlight all piles where the selected card may be placed
        for (pileComponent in pileBox.children) {
            assert(pileComponent is PileComponent) //this is kinda hurting my soul
            if ((pileComponent as PileComponent).checkSettable()) {
                pileComponent.addClass(Styles.highlighted)
            }
        }
    }

    private fun ceaseHighlightPiles() {
        for (pileComponent in pileBox.children) {
            assert(pileComponent is PileComponent) //this is kinda hurting my soul
            if ((pileComponent as PileComponent).checkSettable()) {
                pileComponent.removeClass(Styles.highlighted)
            }
        }
    }

    private fun validatePlay(): PlayValidity {
        val board = game.getBoard()
        var isCardPlaced = false //check if a card has been placed
        for (pileIndex in 0 until game.pileCount) {
            isCardPlaced = board.tempState[pileIndex].card.value != 0
            if (isCardPlaced) break
        }
        if (!isCardPlaced) return PlayValidity.FORFEIT //to forfeit the trick
        for (i in 0 until game.pileCount) {
            if (board.state[i].card.value != 0 && board.tempState[i].card.value == 0) return PlayValidity.INVALID
        }
        return PlayValidity.VALID
    }

    fun replay() {
        val setupView = find(SetupView::class)
        game.players.forEach{ println(it) }
        setupView.players.setAll(game.getFinishedPlayers())
        setupView.players.forEach { println(it) }
        setupView.basicRulesModel.item = game.generateBasicRulesHolder()
        setupView.gamerulesModel.item = game.generateGamerulesHolder()
        replaceWith<SetupView>()
    }

    fun save() {
        if (!this::game.isInitialized) return
        try {
            val string = Json.encodeToString(game)
            println(string)
            val file = chooseFile("Save game file", filters = arrayOf(ExtensionFilter("Tycoon game save","*.dfg"), ExtensionFilter("All files", "*.*")), mode = FileChooserMode.Save) {
                initialDirectory = File(FileSystemView.getFileSystemView().defaultDirectory.path) //Documents folder path
            }[0]
            val fileWriter = FileWriter(file)
            fileWriter.write(string)
            fileWriter.close()
            println("Saved game")
        } catch (_: Exception) { println("Failed to write") }
    }

    fun load(viewToReplace: View = this) {
        try {
            val file = chooseFile("Choose a save file", filters = arrayOf(ExtensionFilter("Tycoon game save","*.dfg"), ExtensionFilter("All files", "*.*")), mode = FileChooserMode.Single) {
                initialDirectory = File(FileSystemView.getFileSystemView().defaultDirectory.path)
            }[0]
            val string = file.readText()
            val game = Json.decodeFromString<Game>(string)
            this.game = game
            viewToReplace.replaceWith<GameView>()
            update()
            println("Loaded game")
        } catch (_: Exception) { println("Failed to load") }
    }

    private enum class PlayValidity {
        VALID,
        INVALID,
        FORFEIT
    }
}