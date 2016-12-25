package org.networkedassets.kdc

import org.jetbrains.kotlin.script.KotlinScriptExternalDependencies
import org.jetbrains.kotlin.script.ScriptContents
import org.jetbrains.kotlin.script.ScriptDependenciesResolver
import org.jetbrains.kotlin.script.asFuture
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.Future

class ConfigScriptDependenciesResolver : ScriptDependenciesResolver {

    override fun resolve(script: ScriptContents,
                         environment: Map<String, Any?>?,
                         report: (ScriptDependenciesResolver.ReportSeverity, String, ScriptContents.Position?) -> Unit,
                         previousDependencies: KotlinScriptExternalDependencies?): Future<KotlinScriptExternalDependencies?> {

        return object : KotlinScriptExternalDependencies {
            override val classpath = // TODO: this is slightly gross
                    (ClassLoader.getSystemClassLoader() as? URLClassLoader)?.urLs?.map { File(it.toURI()) } ?: listOf()
            override val imports = listOf("org.networkedassets.kdc.api.*")
            override val javaHome = System.getProperty("java.home")
            override val sources get() = classpath
        }.asFuture()
    }
}