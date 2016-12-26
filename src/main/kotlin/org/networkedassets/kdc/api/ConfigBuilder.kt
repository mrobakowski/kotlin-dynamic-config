@file:Suppress("unused")

package org.networkedassets.kdc.api

object property

interface ConfigBuilder {
    val config: MutableMap<String, Any?>

    infix fun property.named(name: String) = PropertyInitializer(name)
    class PropertyInitializer(val name: String)

    infix fun PropertyInitializer.equals(v: Any?) {
        config[name] = v
    }
}