package me.metonspawn.tycoon.view

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.component.DeckComponent
import me.metonspawn.tycoon.component.LockMenu
import me.metonspawn.tycoon.component.PileComponent
import me.metonspawn.tycoon.component.PlayerComponent
import me.metonspawn.tycoon.core.Game
import me.metonspawn.tycoon.core.Player
import tornadofx.*

class GameView: View("Tycoon") {
    lateinit var game: Game
    private val deckBox = HBox()
    private val endButton = Button("End Turn")
    private val pileBox = HBox(8.0)
    private val playerBox = HBox(8.0)
    private val checkBox = HBox(4.0)
    var selectedCard: DeckComponent? = null

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
        borderpane {
            style {
                backgroundColor += c("#0c4226")
            }
            playerBox.apply {
                alignment = Pos.TOP_CENTER
                setPrefSize(800.0,100.0)
                maxWidth = 800.0
            }
            top = playerBox
            center = borderpane {
                checkBox.apply {
                    setPrefSize(800.0,150.0)
                    alignment = Pos.BOTTOM_CENTER
                    paddingBottom = 10
                }
                top = checkBox
                setPrefSize(800.0,400.0)
                center = pileBox
                bottom =  hbox {
                    setPrefSize(800.0,150.0)
                    alignment = Pos.CENTER_RIGHT
                    endButton.addClass(Styles.endButton)
                    endButton.setPrefSize(120.0,50.0)
                    endButton.action {
                        when (validatePlay()) {
                            PlayValidity.INVALID -> return@action
                            PlayValidity.FORFEIT -> game.forfeitTurn()
                            PlayValidity.VALID -> game.turn()
                        }
                    }
                    add(endButton)
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
                add(deckBox)
            }
        }
    }

    init {
        deckBox.alignment = Pos.CENTER
        pileBox.alignment = Pos.CENTER
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
        val players = game.players.toMutableList()
        players.remove(game.getCurrentPlayer())
        for (player in players) {
            playerBox.add(PlayerComponent(player, 792.0/(players.size) - 0 * players.size))
            println(792.0/(players.size) - 0 * players.size)
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
        println("No")
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

    fun quit() {
        replaceWith<MainView>()
    }

    fun replay() {
        replaceWith<SetupView>()
        find(SetupView::class,"basicRulesHolder" to game.generateBasicRulesHolder(),"gamerulesHolder" to game.generateGamerulesHolder())
    }

    private enum class PlayValidity {
        VALID,
        INVALID,
        FORFEIT
    }
}