package org.networkedassets.kdc

import java.nio.file.Paths

fun CONF_PATH(c: String): String = "./src/test/resources/$c"

class AgeNameConf(map: Map<String, Any?>) : Config() {
    val name: String by map
    val age: Int by map
}

class Test : StringSpecEx({
    "Config.load() should return valid config class" {
        val conf = Config.load<AgeNameConf>(Paths.get(CONF_PATH("conf1.kts")).toFile())
        conf should not equal null
        conf!! // this line asserts not null at the type level
        conf.name shouldEqual "John"
        conf.age shouldEqual 42
    }

    "Config.load() should throw when properties missing" {
        shouldThrow<Exception> {
            Config.load<AgeNameConf>(Paths.get(CONF_PATH("conf2.kts")).toFile())
        }
    }.config(ignored = true) // TODO: right now doesn't throw when loading, it throws when using the created object
})
