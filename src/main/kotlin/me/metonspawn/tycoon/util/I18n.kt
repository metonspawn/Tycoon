package me.metonspawn.tycoon.util

import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import tornadofx.*
import java.util.*

object I18n {
    val supportedLanguages = listOf(Locale.ENGLISH,Locale.JAPANESE)
    var locale by FX.localeProperty()

    init {
        val defaultLocale = Locale.getDefault()
        FX.locale = if (supportedLanguages.contains(defaultLocale)) defaultLocale else Locale.ENGLISH
    }

    fun getString(key: String): String {
        val resourceBundle = ResourceBundle.getBundle("messages",locale)
        if (!resourceBundle.containsKey(key)) return "[${key}]"
        return resourceBundle[key]
    }

    fun messageBinding(key: String): StringBinding {
        return Bindings.createStringBinding({getString(key)},FX.localeProperty())
    }

    fun StringProperty.bindMessage(key: String) {
        this.bind(I18n.messageBinding(key))
    }
}