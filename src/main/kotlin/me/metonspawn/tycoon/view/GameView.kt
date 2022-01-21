package me.metonspawn.tycoon.view

import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import me.metonspawn.tycoon.app.Styles
import me.metonspawn.tycoon.component.DeckComponent
import me.metonspawn.tycoon.component.PileComponent
import me.metonspawn.tycoon.core.Board
import me.metonspawn.tycoon.core.Pile
import tornadofx.*

class GameView: View("Tycoon") {
    private val deckBox = HBox()
    private val endButton = Button("End Turn")
    private val pileBox = HBox(8.0)
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
            top = hbox {
                style {
                    backgroundColor += Color.WHITE
                }
                label("Top")
                setPrefSize(800.0,100.0)
            }
            center = borderpane {
                top = hbox {setPrefSize(800.0,150.0)}
                setPrefSize(800.0,400.0)
                center = pileBox
                bottom =  hbox() {
                    setPrefSize(800.0,150.0)
                    alignment = Pos.CENTER_RIGHT
                    endButton.addClass(Styles.endButton)
                    endButton.setPrefSize(120.0,50.0)
                    endButton.action {
                            if (!validatePlay()) return@action
                            find(MainView::class).game!!.turn()
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
                    style {
                        textFill = Color.WHITE
                    }
                }
                add(deckBox)
            }
        }
    }

    init {
        deckBox.alignment = Pos.CENTER
        pileBox.alignment = Pos.CENTER
    }

    fun updateDeck() {
        deckBox.clear()
        val game = find(MainView::class).game!!
        val cards = game.getCurrentPlayer().deck.sortedBy { it.value }
        for (card in cards) {
            deckBox.add(DeckComponent(card))
        }
    }

    fun removeCard(card: DeckComponent) {
        println("> Removed ${card.card.value}, ${card.card.suit} from Deck")
        deckBox.children.remove(card)
    }

    fun removeCard(card: PileComponent) {
        println("> Removed ${card.card.value}, ${card.card.suit} from Pile")
        pileBox.children.remove(card)
    }

    fun updateBoard() {
        pileBox.clear()
        val board = find(MainView::class).game!!.getBoard()
        for (i in 0..3) {
            val stateCard = board.state[i]
            val tempCard = board.tempState[i]
            println("state[$i]: ${stateCard.card.value}")
            println("temp[$i]: ${tempCard.card.value}")
            if (tempCard.card.value != 0) { //if there is no tempState card, it means that it has been removed, and as such the state card should be shown
                pileBox.add(PileComponent(tempCard.card, i))
            } else {
                pileBox.add(PileComponent(stateCard.card,i))
            }
        }
    }

    fun refresh() {
        updateBoard(); updateDeck()
        if (validatePlay()) {endButton.removeClass(Styles.buttonLocked)} else {endButton.addClass(Styles.buttonLocked)}
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

    fun highlightPiles() { //highlight all piles where the selected card may be placed
        for (pileComponent in pileBox.children) {
            assert(pileComponent is PileComponent) //this is kinda hurting my soul
            if ((pileComponent as PileComponent).checkSettable()) {
                pileComponent.addClass(Styles.highlighted)
            }
        }
    }

    fun ceaseHighlightPiles() {
        for (pileComponent in pileBox.children) {
            assert(pileComponent is PileComponent) //this is kinda hurting my soul
            if ((pileComponent as PileComponent).checkSettable()) {
                pileComponent.removeClass(Styles.highlighted)
            }
        }
    }

    fun validatePlay(): Boolean {
        val game = find(MainView::class).game
        val board = game!!.getBoard()
        for (i in 0..3) {
            if (board.state[i].card.value != 0 && board.tempState[i].card.value == 0) return false
        }
        return true
    }
}