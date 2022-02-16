package me.metonspawn.tycoon.core

data class Player(val name: String, val deck: MutableList<Card> = mutableListOf(), var forfeitTrick: Boolean = false, var finish: Boolean = false)