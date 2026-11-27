package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * 0003 §2, raiz de composição por papel, com membros fechados: o pacote raiz, o pacote
 * `core.di` (só pacotes `.di` dos módulos) e a classe `@Database` (só classes `@Dao` dos módulos).
 * Os membros ficam fora da [ContextBoundaryRule].
 */
object CompositionRootRule {
    private const val ROOT = "com.fstengineering.leasilycrm"
    private const val CORE = "core"
    private const val CORE_DI = "$ROOT.core.di"
    private val rootPackageFiles = setOf(
        "DestinationHandling.kt",
        "Destinations.kt",
        "LeasilyCrmApplication.kt",
        "MainActivity.kt",
    )
    private val rootPackageImports = setOf("Destinations", "R", "BuildConfig")

    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra
     * respeitada.
     */
    fun violations(scope: KoScope): List<String> {
        val daos = (scope.classes(includeNested = true) + scope.interfaces(includeNested = true))
            .filter { it.hasAnnotationWithName("Dao") }
            .mapNotNull { it.fullyQualifiedName }
            .toSet()
        val databaseFiles = scope.files.filter { declaresDatabase(it) }

        val rootPackage = scope.files
            .filter { isProductionRootPackage(it) && it.nameWithExtension !in rootPackageFiles }
            .map {
                "${it.nameWithExtension}: o pacote raiz deve ter só a raiz de composição " +
                    "(${rootPackageFiles.sorted().joinToString()})"
            }
        val coreDi = scope.files
            .filter { isCoreDi(it) }
            .flatMap { file ->
                moduleImports(file)
                    .filter { it.removePrefix("$ROOT.").split('.').getOrNull(1) != "di" }
                    .map {
                        "${file.nameWithExtension}: `core/di` deve importar dos módulos só " +
                            "pacotes `.di`, não `$it`"
                    }
            }
        val database = databaseFiles.flatMap { file ->
            moduleImports(file)
                .filter { it !in daos }
                .map {
                    "${file.nameWithExtension}: a classe @Database deve importar dos módulos só " +
                        "classes @Dao, não `$it`"
                }
        }
        val uniqueDatabase = if (databaseFiles.size > 1) {
            listOf(
                "A classe @Database deve ser única: " +
                    databaseFiles.map { it.nameWithExtension }.sorted().joinToString(),
            )
        } else {
            emptyList()
        }

        return (rootPackage + coreDi + database + uniqueDatabase).sorted()
    }

    /**
     * Membro da raiz de composição: pode importar qualquer módulo, nos limites do seu papel. Só
     * código de produção: o teste de um membro segue a fronteira de contexto.
     */
    fun isMember(file: KoFileDeclaration): Boolean =
        (isProductionRootPackage(file) && file.nameWithExtension in rootPackageFiles) ||
            isCoreDi(file) ||
            declaresDatabase(file)

    private fun isProduction(file: KoFileDeclaration) = "/src/main/" in file.path

    private fun isProductionRootPackage(file: KoFileDeclaration) =
        isProduction(file) && file.packagee?.name == ROOT

    private fun isCoreDi(file: KoFileDeclaration): Boolean {
        val pkg = file.packagee?.name.orEmpty()
        return isProduction(file) && (pkg == CORE_DI || pkg.startsWith("$CORE_DI."))
    }

    private fun declaresDatabase(file: KoFileDeclaration) = isProduction(file) &&
        file.classes(includeNested = true).any { it.hasAnnotationWithName("Database") }

    /** Imports de módulos: abaixo da raiz, fora do `core` e das classes do pacote raiz. */
    private fun moduleImports(file: KoFileDeclaration) = file.imports
        .map { it.name }
        .filter { it.startsWith("$ROOT.") }
        .filter { it.removePrefix("$ROOT.").substringBefore('.') !in rootPackageImports + CORE }
}
