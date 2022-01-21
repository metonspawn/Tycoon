package me.metonspawn.tycoon.core

data class Player(val deck: MutableList<Card> = mutableListOf(), var forfeitTrick: Boolean = false)