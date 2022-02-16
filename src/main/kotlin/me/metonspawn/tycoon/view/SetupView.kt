package me.metonspawn.tycoon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import me.metonspawn.tycoon.core.Game
import me.metonspawn.tycoon.core.Player
import tornadofx.*

class SetupView(private val players: ObservableList<Player> = observableList(Player("One"),Player("Two"),Player("Three")), basicRulesHolder: BasicRulesHolder = BasicRulesHolder(), gamerulesHolder: GamerulesHolder = GamerulesHolder()): View("Tycoon") {
    private var nameField = SimpleStringProperty()
    private var selectedIndex: Int? = null
    private val gamerulesModel = GamerulesModel(gamerulesHolder)
    private val basicRulesModel = BasicRulesModel(basicRulesHolder)

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        menubar {
            menu("Game") {
                item("Save")
                item("Load")
            }
            menu("Language") {
                item("English")
            }
        }
        hbox(8.0) {
            vbox {
                prefWidth = 400.0
                tableview(players) {
                    readonlyColumn("Player Name", Player::name)
                    readonlyColumn("Test",Player::finish)
                    selectionModel.selectedIndexProperty().onChange {
                        this@SetupView.selectedIndex = it
                        println(it)
                    }
                    smartResize()
                }
                hbox(4.0) {
                    alignment = Pos.CENTER
                    button("Remove") {
                        action {
                            if (selectedIndex == null || selectedIndex == -1) return@action
                            players.removeAt(selectedIndex!!)
                            selectedIndex = null
                        }
                    }
                    button("Remove All") {
                        action {
                            players.clear()
                        }
                    }
                    form {
                        style {
                            paddingAll = 4
                            paddingTop = 20.0
                        }
                        fieldset {
                            field("Name") {
                                textfield(nameField)
                                button("Add") {
                                    enableWhen(nameField.isNotEmpty)
                                    action {
                                        println(nameField)
                                        players.add(Player(nameField.value))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            vbox {
                form {
                    style {
                        setSpacing(8.0)
                    }
                    fieldset("Basic Rules") {
                        flowpane {
                            hgap = 8.0
                            field("Number of Decks") {
                                textfield(basicRulesModel.deckCount) {
                                    filterInput {
                                        if (!it.controlNewText.isInt()) return@filterInput false
                                        return@filterInput (Integer.parseInt(it.controlNewText) > 0)
                                    }
                                }
                            }
                            field("Jokers") {
                                checkbox(property = basicRulesModel.useJokers)
                            }
                            field("Number of Piles") {
                                textfield(basicRulesModel.pileCount) {
                                    filterInput {
                                        if (!it.controlNewText.isInt()) return@filterInput false
                                        return@filterInput (Integer.parseInt(it.controlNewText) > 0)
                                    }
                                }
                            }
                        }
                        button("Reset") {
                            action {
                                println("${basicRulesModel.deckCount.value}, ${basicRulesModel.useJokers.value}")
                                basicRulesModel.rollback()
                            }
                        }
                    }
                    fieldset("Optional Rules") {
                        hbox(8) {
                            field("8-Cutting") {
                                checkbox(property = gamerulesModel.eightCutting)
                            }
                            field("11-Back") {
                                checkbox(property = gamerulesModel.elevenBack)
                            }
                        }
                        button("Reset") {
                            action {
                                println("${gamerulesModel.eightCutting.value}, ${gamerulesModel.elevenBack.value}")
                                gamerulesModel.rollback()
                            }
                        }
                    }
                    button("Start") {
                        action {
                            if (players.size < 2) return@action //Didn't have time to figure out how to work with enableWhen()
                            start()
                        }
                    }
                }
            }
        }
    }

    class BasicRulesHolder(deckCount: Int = 1, useJokers: Boolean = true, pileCount: Int = 4) { //A class to hold the values to be used to initiate the game using FX properties
        val deckCountProperty = SimpleIntegerProperty(this,"deckCount",deckCount)
        var deckCount by deckCountProperty
        val useJokersProperty = SimpleBooleanProperty(this,"joker",useJokers)
        var useJokers by useJokersProperty
        val pileCountProperty = SimpleIntegerProperty(this,"pileCount",pileCount)
        var pileCount by pileCountProperty
    }

    class GamerulesHolder(eightCutting: Boolean = true, elevenBack: Boolean = false) { //Idem
        val eightCuttingProperty = SimpleBooleanProperty(this,"eightCutting",eightCutting)
        var eightCutting by eightCuttingProperty
        val elevenBackProperty = SimpleBooleanProperty(this,"elevenBack",elevenBack)
        var elevenBack by elevenBackProperty

        fun toGamerules(): Game.Gamerules {
            return Game.Gamerules(eightCutting,elevenBack)
        }
    }

    private class GamerulesModel(gamerulesHolder: GamerulesHolder = GamerulesHolder()): ItemViewModel<GamerulesHolder>(gamerulesHolder) { //Model to temporarily hold the data
        val eightCutting = bind(GamerulesHolder::eightCuttingProperty)
        val elevenBack = bind(GamerulesHolder::elevenBackProperty)
    }

    private class BasicRulesModel(basicRulesHolder: BasicRulesHolder = BasicRulesHolder()): ItemViewModel<BasicRulesHolder>(basicRulesHolder) { //Idem
        val deckCount = bind(BasicRulesHolder::deckCountProperty)
        val useJokers = bind(BasicRulesHolder::useJokersProperty)
        val pileCount = bind(BasicRulesHolder::pileCountProperty)
    }

    private fun start() {
        gamerulesModel.commit(); basicRulesModel.commit()
        val game = Game(players,basicRulesModel.item.deckCount,basicRulesModel.item.pileCount,basicRulesModel.item.useJokers,gamerulesModel.item.toGamerules()) //Initiating the game using the inputted data
        replaceWith<GameView>()
        game.start()
    }

    fun test() {
        this.players.clear()
    }
}