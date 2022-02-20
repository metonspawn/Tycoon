package me.metonspawn.tycoon.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.component.DeckComponent
import me.metonspawn.tycoon.component.LockMenu
import me.metonspawn.tycoon.component.PileComponent
import me.metonspawn.tycoon.component.PlayerComponent
import me.metonspawn.tycoon.core.Game
import me.metonspawn.tycoon.util.I18n
import tornadofx.*

class GameView: TycoonView() {
    lateinit var game: Game
    private var deckBox: HBox by singleAssign()
    private var endButton: Button by singleAssign()
    private var pileBox: HBox by singleAssign()
    private var playerBox: HBox by singleAssign()
    private var checkBox: HBox by singleAssign()
    private var playerName = SimpleStringProperty("")
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
            setPrefSize(800.0,500.0)
            top = hbox(4) {
                setPrefSize(800.0,120.0)
                alignment = Pos.BOTTOM_CENTER
                paddingBottom = 10
                checkBox = this
            }
            center = hbox(8) {
                alignment = Pos.CENTER
                pileBox = this
            }
            bottom = hbox {
                setPrefSize(800.0,145.0)
                alignment = Pos.CENTER_RIGHT
                button(I18n.messageBinding("endTurn")) {
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
                    prefWidth = 100.0
                }
            }
        }
        bottom = vbox {
            setPrefSize(800.0,130.0)
            alignment = Pos.BOTTOM_CENTER
            stackpane {
                hbox {
                    label(I18n.messageBinding("playerIndicator").concat(playerName)) {
                        addClass(Styles.heading)
                    }
                }
                hbox {
                    alignment = Pos.CENTER
                    label(I18n.messageBinding("deck")) {
                        addClass(Styles.heading)
                    }
                }
            }
            scrollpane {
                minHeight = 90.0
                alignment = Pos.BOTTOM_CENTER
                addClass(Styles.invisibleScrollpane)
                vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                isFitToHeight = true
                isFitToWidth = true
                hbox {
                    alignment = Pos.CENTER
                    deckBox = this
                }
            }
        }
    }

    private fun updateDeck() {
        deckBox.clear()
        val cards = game.getCurrentPlayer().deck.sortedBy { it.value }
        for (card in cards) {
            deckBox.add(DeckComponent(card))
        }
        playerName.set(game.getCurrentPlayer().name)
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
        setupView.players.setAll(game.getFinishedPlayers())
        setupView.basicRulesModel.item = game.generateBasicRulesHolder()
        setupView.gamerulesModel.item = game.generateGamerulesHolder()
        replaceWith<SetupView>()
    }

    fun isGameRunning(): Boolean {
        return this::game.isInitialized && this.game.isGameRunning()
    }

    private enum class PlayValidity {
        VALID,
        INVALID,
        FORFEIT
    }
}