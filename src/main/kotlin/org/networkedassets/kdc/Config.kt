package org.networkedassets.kdc

import mu.KotlinLogging
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileScript
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer.newDisposable
import org.jetbrains.kotlin.config.CommonConfigurationKeys.MODULE_NAME
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys.SCRIPT_DEFINITIONS
import org.jetbrains.kotlin.config.addKotlinSourceRoot
import org.jetbrains.kotlin.utils.PathUtil
import org.slf4j.Logger
import java.io.File
import kotlin.reflect.primaryConstructor

val logger = KotlinLogging.logger {}

abstract class Config {
    companion object {
        inline fun <reified T : Config> load(path: File): T? {
            return withRootDisposable { disposable ->
                val env = getEnvironmentForConfigScript(path, disposable)
                @Suppress("UNCHECKED_CAST")
                val clazz = compileScript(env, Config::class.java.classLoader) as? Class<ConfigScriptTemplate>
                        ?: throw RuntimeException("Couldn't load file $path")
                val configScript = clazz.kotlin.primaryConstructor?.call(ConfigBuilderImpl())
                configScript?.configBuilder?.build<T>()
            }
        }

    }
}

@PublishedApi internal fun getEnvironmentForConfigScript(path: File, disposable: Disposable) =
        KotlinCoreEnvironment.createForProduction(disposable, CompilerConfiguration().apply {
            put(MESSAGE_COLLECTOR_KEY, messageCollectorFor(logger))
            put(MODULE_NAME, "kdc")
            addJvmClasspathRoots(PathUtil.getJdkClassesRoots())
            addKotlinSourceRoot(path.canonicalPath)
            add(SCRIPT_DEFINITIONS, ConfigScriptDefinition)
        }, EnvironmentConfigFiles.JVM_CONFIG_FILES)

@PublishedApi internal fun <R> withRootDisposable(block: (Disposable) -> R): R {
    val disposable = newDisposable()
    try {
        val res = block(disposable)
        return res
    } finally {
        disposable.dispose()
    }
}

@PublishedApi internal fun messageCollectorFor(log: Logger): MessageCollector =
        object : MessageCollector {
            override fun hasErrors(): Boolean = false

            override fun clear() {}

            override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation) {
                fun msg() =
                        if (location == CompilerMessageLocation.NO_LOCATION) message
                        else "$message ($location)"

                when (severity) {
                    in CompilerMessageSeverity.ERRORS -> log.error("Error: " + msg())
                    CompilerMessageSeverity.ERROR -> log.error(msg())
                    CompilerMessageSeverity.WARNING -> log.info("Warning: " + msg())
                    CompilerMessageSeverity.LOGGING -> log.info(msg())
                    CompilerMessageSeverity.INFO -> log.info(msg())
                    else -> {
                    }
                }
            }
        }