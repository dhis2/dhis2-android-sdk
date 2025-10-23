package org.hisp.dhis.android.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStream

class DaoQueriesProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        val symbols = resolver
            .getSymbolsWithAnnotation(GenerateDaoQueries::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE }

        if (!symbols.iterator().hasNext()) {
            logger.warn("Processing DAO: No symbols found")
            return emptyList()
        }

        symbols.forEach { symbol ->
            logger.info("Processing DAO: ${symbol.qualifiedName?.asString()}")

            val tableName = symbol.annotations.firstNotNullOfOrNull { annotation ->
                if (annotation.shortName.asString() == GenerateDaoQueries::class.simpleName &&
                    annotation.annotationType.resolve().declaration.qualifiedName?.asString() == GenerateDaoQueries::class.qualifiedName
                ) {
                    annotation.arguments.find { arg -> arg.name?.asString() == "tableName" }?.value as? String
                } else {
                    null
                }
            }?.takeIf { it.isNotBlank() }

            val parentColumnName = symbol.annotations.firstNotNullOfOrNull { annotation ->
                if (annotation.shortName.asString() == GenerateDaoQueries::class.simpleName &&
                    annotation.annotationType.resolve().declaration.qualifiedName?.asString() == GenerateDaoQueries::class.qualifiedName
                ) {
                    annotation.arguments.find { arg -> arg.name?.asString() == "parentColumnName" }?.value as? String
                } else {
                    null
                }
            }?.takeIf { it.isNotBlank() }

            if (tableName == null) {
                logger.error(
                    "Could not extract a valid, non-blank 'tableName' String from @GenerateDaoQueries for ${symbol.qualifiedName?.asString()}. ",
                    symbol
                )
                return@forEach
            }

            logger.info("Successfully extracted tableName: '$tableName' for ${symbol.qualifiedName?.asString()}")

            val superInterfaceSimpleNames = symbol.superTypes
                .map { it.resolve() } // Resuelve cada KSTypeReference a KSType
                .filterNot { it.isError } // Filtra los que son KSErrorType
                .mapNotNull { ksType -> // Para los restantes (no error)
                    (ksType.declaration as? KSClassDeclaration)?.simpleName?.asString()
                    // Intenta castear la declaración a KSClassDeclaration y luego obtén el simpleName
                    // Si el casteo falla o simpleName es null, mapNotNull lo omitirá
                }
                .toList()

            logger.info("DAO ${symbol.qualifiedName?.asString()} (resolved) super interface simple names: $superInterfaceSimpleNames")

            // o si hay jerarquías más profundas que necesitas inspeccionar.
            val baseInterfaceType: String? = when {
                superInterfaceSimpleNames.any { it == "ObjectDao" } -> "ObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableDeletableDataObjectDao" } -> "IdentifiableDeletableDataObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableDataObjectDao" } -> "IdentifiableDataObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableObjectDao" } -> "IdentifiableObjectDao"
                superInterfaceSimpleNames.any { it == "LinkDao" } -> "LinkDao"
                superInterfaceSimpleNames.any { it == "ReadableDao" } -> "ReadableDao"
                else -> null
            }

            logger.info("DAO ${symbol.qualifiedName?.asString()} interfaceType: $baseInterfaceType")

            val originalPackageName = symbol.packageName.asString()
            val originalDaoName = symbol.simpleName.asString()
            val generatedInterfaceName = "${originalDaoName.removeSuffix("Aux")}"

            val fileOutputStream = codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, symbol.containingFile!!),
                packageName = originalPackageName,
                fileName = generatedInterfaceName
            )

            fileOutputStream.use { file ->
                file += "package $originalPackageName\n\n"
                file += "import androidx.room.Dao\n"
                file += "import androidx.room.Query\n"


                when (baseInterfaceType) {
                    "ObjectDao" -> {
                        file += "\n"
                        file += "@Dao\n"
                        file += getSignature(generatedInterfaceName, originalDaoName)
                        file += buildObjectDaoQueries(tableName)
                    }

                    "IdentifiableObjectDao" -> {
                        file += "\n"
                        file += "@Dao\n"
                        file += getSignature(generatedInterfaceName, originalDaoName)
                        file += buildIdentifiableObjectDaoQueries(tableName)
                    }

                    "LinkDao" -> {
                        file += "\n"
                        file += "@Dao\n"
                        file += getSignature(generatedInterfaceName, originalDaoName)
                        file += buildLinkDaoQueries(tableName, parentColumnName!!)
                    }

                    "IdentifiableDataObjectDao" -> {
                        file += "import org.hisp.dhis.android.core.common.DataColumns\n"
                        file += "import org.hisp.dhis.android.core.common.IdentifiableColumns\n"
                        file += "import org.hisp.dhis.android.core.common.State\n"
                        file += "\n"
                        file += "@Dao\n"
                        file += getSignature(generatedInterfaceName, originalDaoName)
                        file += buildIdentifiableDataObjectDaoQueries(tableName)
                    }

                    "IdentifiableDeletableDataObjectDao" -> {
                        file += "import org.hisp.dhis.android.core.common.DataColumns\n"
                        file += "import org.hisp.dhis.android.core.common.State\n"
                        file += "import org.hisp.dhis.android.core.common.DeletableDataColumns\n"
                        file += "import org.hisp.dhis.android.core.common.IdentifiableColumns\n"
                        file += "\n"
                        file += "@Dao\n"
                        file += getSignature(generatedInterfaceName, originalDaoName)
                        file += buildIdentifiableDeletableDataObjectDaoQueries(tableName)
                    }

                    "ReadableDao" -> {}
                }

                file += "}\n"
            }
            logger.info("Generated interface $generatedInterfaceName in $originalPackageName for $originalDaoName")
        }

        invoked = true
        return symbols.filterNot { it.validate() }.toList()
    }

    private fun getSignature(
        generatedInterfaceName: String,
        originalDaoName: String
    ) = "internal interface $generatedInterfaceName : $originalDaoName {\n\n"

    private fun buildObjectDaoQueries(tableName: String): String {
        return "    @Query(\"DELETE FROM ${'$'}{${tableName}}\")\n" +
            "    override fun deleteAllRows(): Int\n\n"
    }

    private fun buildIdentifiableObjectDaoQueries(tableName: String): String {
        return buildObjectDaoQueries(tableName) +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}} WHERE uid = :uid\")\n" +
            "    override fun delete(uid: String): Int\n\n"
    }

    private fun buildLinkDaoQueries(tableName: String, parentColumnName: String): String {
        return buildObjectDaoQueries(tableName) +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}} WHERE ${'$'}{${parentColumnName}} = :parentUid\")\n" +
            "    override fun deleteLinksForMasterUid(parentUid: String): Int\n\n" +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}}\")" +
            "    override fun deleteLinksForMasterUid(): Int"
    }

    private fun buildIdentifiableDataObjectDaoQueries(tableName: String): String {
        return buildIdentifiableObjectDaoQueries(tableName) +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DataColumns.SYNC_STATE} = :state WHERE " +
            "${'$'}{IdentifiableColumns.UID} = :uid\")\n" +
            "    override fun setSyncState(uid: String, state: State): Int\n\n" +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DataColumns.SYNC_STATE} = :state WHERE " +
            "${'$'}{IdentifiableColumns.UID} IN (:uids)\")\n" +
            "    override fun setSyncState(uids: List<String>, state: State): Int\n\n" +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DataColumns.SYNC_STATE} = :newstate WHERE " +
            "${'$'}{IdentifiableColumns.UID} = :uid AND ${'$'}{DataColumns.SYNC_STATE} = :updateState\")\n" +
            "    override fun setSyncStateIfUploading(uid: String, newstate: State, updateState: State): Int\n\n"
    }

    private fun buildIdentifiableDeletableDataObjectDaoQueries(tableName: String): String {
        return buildIdentifiableDataObjectDaoQueries(tableName) +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DeletableDataColumns.DELETED} = 1 " +
            "WHERE ${'$'}{IdentifiableColumns.UID} = :uid\")\n" +
            "    override fun setDeleted(uid: String): Int\n\n" +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}} WHERE ${'$'}{DataColumns.SYNC_STATE} = :state AND " +
            "${'$'}{IdentifiableColumns.UID} = :uid AND ${'$'}{DeletableDataColumns.DELETED} = :deleted\")\n" +
            "    override fun deleteWhere(uid: String, deleted: Boolean, state: State): Int\n\n"
    }

    // Operador de extensión para escribir Strings fácilmente en OutputStream
    private operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }
}
