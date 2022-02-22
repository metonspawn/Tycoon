package me.metonspawn.tycoon.util

import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import tornadofx.*
import java.util.*

object I18n {
    val supportedLanguages = listOf(Locale.ENGLISH, Locale.JAPANESE)
    val localeProperty = SimpleObjectProperty(Locale(Locale.getDefault().language))
    var locale by localeProperty

    init {
        locale = if (supportedLanguages.contains(locale)) this.locale else Locale.ENGLISH
    }

    fun getString(key: String): String {
        val resourceBundle = ResourceBundle.getBundle("Messages",locale)
        return resourceBundle[key]
    }

    fun messageBinding(key: String): StringBinding {
        return Bindings.createStringBinding({getString(key)},localeProperty)
    }

    fun StringProperty.bindMessage(key: String) {
        this.bind(I18n.messageBinding(key))
    }
}