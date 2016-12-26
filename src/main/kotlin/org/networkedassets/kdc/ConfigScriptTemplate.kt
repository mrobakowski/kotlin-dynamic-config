package org.networkedassets.kdc

import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate
import org.jetbrains.kotlin.script.ScriptTemplateDefinition
import org.networkedassets.kdc.api.ConfigBuilder

@ScriptTemplateDefinition(resolver = ConfigScriptDependenciesResolver::class)
open class ConfigScriptTemplate(val configBuilder: ConfigBuilderImpl) : ConfigBuilder by configBuilder

object ConfigScriptDefinition : KotlinScriptDefinitionFromAnnotatedTemplate(ConfigScriptTemplate::class)
