package org.networkedassets.kdc

import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate
import org.jetbrains.kotlin.script.ScriptTemplateDefinition
import org.networkedassets.kdc.api.ConfigBuilder

@ScriptTemplateDefinition(resolver = ConfigScriptDependenciesResolver::class)
open class ConfigScriptTemplate(val cb: ConfigBuilderImpl) : ConfigBuilder by cb

object ConfigScriptDefinition : KotlinScriptDefinitionFromAnnotatedTemplate(ConfigScriptTemplate::class)
