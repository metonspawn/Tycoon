package me.metonspawn.tycoon.core
import kotlinx.serialization.Serializable

enum class Title(private val out: String) {
    PRESIDENT("President"),
    VICE_PRESIDENT("Vice President"),
    COMMONER("Commoner"),
    VICE_ASS("Vice-Ass"),
    ASSHOLE("Asshole");
    override fun toString(): String {
        return this.out
    }
}

@Serializable
class Player(val name: String, val deck: MutableList<Card> = mutableListOf(), var forfeitTrick: Boolean = false, var finish: Boolean = false, var title: Title = Title.COMMONER) {

    fun compare(to: Player): Boolean { //need to declare something explicitly or it will fail when importing a game through serialization
        return this.name == to.name && this.deck.zip(to.deck).all { (x,y) -> x.value == y.value && x.suit == y.suit} && this.forfeitTrick == to.forfeitTrick && this.finish == to.finish
    }
}