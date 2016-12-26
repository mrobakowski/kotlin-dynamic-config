package org.networkedassets.kdc

import org.networkedassets.kdc.api.ConfigBuilder
import kotlin.reflect.isSupertypeOf

abstract class DUMMY_MAP_TYPE: MutableMap<String, Any?>

class ConfigBuilderImpl : ConfigBuilder {
    override val config = mutableMapOf<String, Any?>()

    inline fun <reified T : Config> build(): T {
        val constrs = T::class.constructors
        val maybeConstr = constrs.asSequence().filter {
            it.parameters.singleOrNull()?.type?.isSupertypeOf(DUMMY_MAP_TYPE::class.supertypes.single()) ?: false
        }.singleOrNull()
        val constr = maybeConstr ?: throw RuntimeException("Only configs with Map constructor for now")
        return constr.call(config)
    }
}