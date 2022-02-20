package me.metonspawn.tycoon.core
import javafx.beans.binding.StringBinding
import kotlinx.serialization.Serializable
import me.metonspawn.tycoon.util.I18n

enum class Title(private val out: StringBinding) {
    PRESIDENT(I18n.messageBinding("president")),
    VICE_PRESIDENT(I18n.messageBinding("vicePresident")),
    COMMONER(I18n.messageBinding("commoner")),
    VICE_ASS(I18n.messageBinding("viceAss")),
    ASSHOLE(I18n.messageBinding("asshole"));
    fun messageBinding(): StringBinding {
        return this.out
    }
}

@Serializable
class Player(val name: String, val deck: MutableList<Card> = mutableListOf(), var forfeitTrick: Boolean = false, var finish: Boolean = false, var title: Title = Title.COMMONER) {

    fun compare(to: Player): Boolean { //need to declare something explicitly or it will fail when importing a game through serialization
        return this.name == to.name && this.deck.zip(to.deck).all { (x,y) -> x.value == y.value && x.suit == y.suit} && this.forfeitTrick == to.forfeitTrick && this.finish == to.finish
    }
}