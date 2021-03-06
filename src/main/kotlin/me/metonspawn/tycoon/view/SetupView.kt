package me.metonspawn.tycoon.view

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import me.metonspawn.tycoon.core.Game
import me.metonspawn.tycoon.core.Player
import me.metonspawn.tycoon.util.I18n
import me.metonspawn.tycoon.util.I18n.bindMessage
import tornadofx.*

class SetupView(var players: ObservableList<Player> = observableListOf<Player>(), basicRulesHolder: BasicRulesHolder = BasicRulesHolder(), gamerulesHolder: GamerulesHolder = GamerulesHolder()): TycoonView() {
    private val nameField = SimpleStringProperty()
    private var selectedIndex: Int? = null
    val gamerulesModel = GamerulesModel(gamerulesHolder)
    val basicRulesModel = BasicRulesModel(basicRulesHolder)

    override val content = hbox(8.0) {
        vbox {
            prefWidth = 400.0
            tableview(players) {
                readonlyColumn("Player Name", Player::name) {
                    textProperty().bindMessage("playerName")
                    isSortable = false
                    remainingWidth()
                }
                column("Title",Player::title) {
                    textProperty().bindMessage("title")
                    isSortable = false
                    cellFormat {
                        alignment = Pos.CENTER
                        textProperty().bind(it.messageBinding())
                    }
                }
                selectionModel.selectedIndexProperty().onChange {
                    this@SetupView.selectedIndex = it
                    println(it)
                }
                smartResize()
            }
            hbox(4.0) {
                alignment = Pos.CENTER
                button(I18n.messageBinding("remove")) {
                    action {
                        if (selectedIndex == null || selectedIndex == -1) return@action
                        players.removeAt(selectedIndex!!)
                        selectedIndex = null
                    }
                }
                button(I18n.messageBinding("removeAll")) {
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
                            textProperty.bindMessage("name")
                            textfield(nameField)
                            button(I18n.messageBinding("add")) {
                                enableWhen(nameField.isNotEmpty)
                                action {
                                    players.add(Player(nameField.value))
                                    nameField.set("")
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
                    textProperty.bindMessage("basicRules")
                    flowpane {
                        hgap = 8.0
                        field("Number of Decks") {
                            textProperty.bindMessage("deckNumber")
                            textfield(basicRulesModel.deckCount) {
                                filterInput { //only allow naturals
                                    if (!it.controlNewText.isInt()) return@filterInput false
                                    return@filterInput (Integer.parseInt(it.controlNewText) > 0)
                                }
                            }
                        }
                        field("Jokers") {
                            textProperty.bindMessage("jokers")
                            checkbox(property = basicRulesModel.useJokers)
                        }
                        field("Number of Piles") {
                            textProperty.bindMessage("pileNumber")
                            textfield(basicRulesModel.pileCount) {
                                filterInput { //only allow naturals
                                    if (!it.controlNewText.isInt()) return@filterInput false
                                    return@filterInput (Integer.parseInt(it.controlNewText) > 0)
                                }
                            }
                        }
                    }
                    button(I18n.messageBinding("reset")) {
                        action {
                            println("${basicRulesModel.deckCount.value}, ${basicRulesModel.useJokers.value}")
                            basicRulesModel.rollback()
                        }
                    }
                }
                fieldset("Optional Rules") {
                    textProperty.bindMessage("gamerules")
                    flowpane {
                        hgap = 8.0
                        field("8-Cutting") {
                            textProperty.bindMessage("eightCutting")
                            checkbox(property = gamerulesModel.eightCutting)
                        }
                        field("11-Back") {
                            textProperty.bindMessage("elevenBack")
                            checkbox(property = gamerulesModel.elevenBack)
                        }
                        field("Allow same-valued cards to be played") {
                            textProperty.bindMessage("allowIdenticalValue")
                            checkbox(property = gamerulesModel.allowIdenticalValue)
                        }
                    }
                    button(I18n.messageBinding("reset")) {
                        action {
                            gamerulesModel.rollback()
                        }
                    }
                }
                button(I18n.messageBinding("start")) {
                    enableWhen {
                        Bindings.greaterThanOrEqual(players.sizeProperty, 2)
                    }
                    action {
                        if (players.size < 2) return@action //Didn't have time to figure out how to work with enableWhen()
                        start()
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

    class GamerulesHolder(eightCutting: Boolean = true, elevenBack: Boolean = false, allowIdenticalValue: Boolean = false) { //Idem
        val eightCuttingProperty = SimpleBooleanProperty(this,"eightCutting",eightCutting)
        var eightCutting by eightCuttingProperty
        val elevenBackProperty = SimpleBooleanProperty(this,"elevenBack",elevenBack)
        var elevenBack by elevenBackProperty
        val allowIdenticalValueProperty = SimpleBooleanProperty(this,"allowIdenticalValue",allowIdenticalValue)
        var allowIdenticalValue by allowIdenticalValueProperty

        fun toGamerules(): Game.Gamerules {
            return Game.Gamerules(eightCutting,elevenBack,allowIdenticalValue)
        }
    }

    class GamerulesModel(gamerulesHolder: GamerulesHolder = GamerulesHolder()): ItemViewModel<GamerulesHolder>(gamerulesHolder) { //Model to temporarily hold the data
        val eightCutting = bind(GamerulesHolder::eightCuttingProperty)
        val elevenBack = bind(GamerulesHolder::elevenBackProperty)
        val allowIdenticalValue = bind(GamerulesHolder::allowIdenticalValueProperty)
    }

    class BasicRulesModel(basicRulesHolder: BasicRulesHolder = BasicRulesHolder()): ItemViewModel<BasicRulesHolder>(basicRulesHolder) { //Idem
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
}