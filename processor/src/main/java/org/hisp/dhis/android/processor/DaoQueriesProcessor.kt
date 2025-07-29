package org.hisp.dhis.android.processor

import androidx.room.Dao
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

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val daoSymbols = resolver
            .getSymbolsWithAnnotation(Dao::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE }

        if (!daoSymbols.iterator().hasNext()) {
            logger.warn("Processing DAO: No symbols found")
            return emptyList()
        }

        daoSymbols.forEach { symbol ->
            logger.warn("Processing DAO: ${symbol.qualifiedName?.asString()}")

            val generateQueriesAnnotation = symbol.annotations.firstOrNull {
                it.annotationType.resolve().declaration
                    .qualifiedName?.asString() == GenerateDaoQueries::class.qualifiedName
            }

            if (generateQueriesAnnotation == null) {
//                logger.warn(
//                    "DAO ${symbol.qualifiedName?.asString()}" +
//                        " is not annotated with @GenerateDaoQueries. Skipping."
//                )
                return@forEach
            }
            logger.warn("Processing DAO: ${symbol.qualifiedName?.asString()}")

            val tableNameArgument = generateQueriesAnnotation.arguments.firstOrNull {
                it.name?.asString() == "tableName"
            }

            if (tableNameArgument == null) {
                logger.error(
                    "No argument named 'tableName' found in @GenerateDaoQueries for ${symbol.qualifiedName?.asString()}",
                    symbol
                )
                return@forEach
            }

            val rawValue = tableNameArgument.value
            logger.warn(
                "For ${symbol.qualifiedName?.asString()}, 'tableName' argument raw value is: '$rawValue', type: ${rawValue?.let { it::class.qualifiedName }}",
                symbol
            )

            val tableName = rawValue as? String

            if (tableName == null || tableName.isBlank()) {
                logger.error(
                    "Could not extract a valid String from tableName argument in @GenerateDaoQueries for ${symbol.qualifiedName?.asString()}. Raw value was '$tableName'.",
                    symbol
                )
                return@forEach
            }

// Si llegas aquí, tableName es un String no nulo y no vacío.
            logger.warn("Successfully extracted tableName: '$tableName' for ${symbol.qualifiedName?.asString()}")

            val superInterfaceSimpleNames = symbol.superTypes
                .map { it.resolve() } // Resuelve cada KSTypeReference a KSType
                .filterNot { it.isError } // Filtra los que son KSErrorType
                .mapNotNull { ksType -> // Para los restantes (no error)
                    (ksType.declaration as? KSClassDeclaration)?.simpleName?.asString()
                    // Intenta castear la declaración a KSClassDeclaration y luego obtén el simpleName
                    // Si el casteo falla o simpleName es null, mapNotNull lo omitirá
                }
                .toList()

            logger.warn("DAO ${symbol.qualifiedName?.asString()} (resolved) super interface simple names: $superInterfaceSimpleNames")

            // o si hay jerarquías más profundas que necesitas inspeccionar.
            val baseInterfaceType: String? = when {
                superInterfaceSimpleNames.any { it == "ObjectDao" } -> "ObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableDeletableDataObjectStoreDao" } -> "IdentifiableDeletableDataObjectStoreDao"
                superInterfaceSimpleNames.any { it == "IdentifiableDataObjectDao" } -> "IdentifiableDataObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableObjectDao" } -> "IdentifiableObjectDao" // Nota: Impl sugiere clase, asegúrate de que es interfaz
                superInterfaceSimpleNames.any { it == "LinkDao" } -> "LinkDao" // Nota: Impl sugiere clase, asegúrate de que es interfaz
                // TODO: Añadir más casos según tus interfaces base
                else -> null
            }

            logger.warn("DAO ${symbol.qualifiedName?.asString()} interfaceType: $baseInterfaceType")


            logger.warn("${symbol.packageName}")
            val originalPackageName = symbol.packageName.asString()
            val originalDaoName = symbol.simpleName.asString()
            logger.warn("$originalPackageName, $originalDaoName")
            val generatedInterfaceName = "${originalDaoName}QueryDao"

            logger.warn("$originalPackageName, $originalDaoName, $generatedInterfaceName")

            val fileOutputStream = codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, symbol.containingFile!!),
                packageName = originalPackageName,
                fileName = generatedInterfaceName
            )

            fileOutputStream.use { file ->
                file += "package $originalPackageName\n\n"
                file += "import androidx.room.Query\n"

                // Lógica de generación de métodos basada en baseInterfaceType
                when (baseInterfaceType) {
                    "ObjectDao" -> {
                        file += "\n"
                        file += "internal interface $generatedInterfaceName {\n\n"
                        file += buildObjectDaoQueries(tableName)
                    }

                    "IdentifiableObjectDao" -> {
                        file += "\n"
                        file += "internal interface $generatedInterfaceName {\n\n"
                        file += buildIdentifiableObjectDaoQueries(tableName)
                    }

                    "LinkDao" -> {
                        file += "\n"
                        file += "internal interface $generatedInterfaceName {\n\n"
                        file += buildLinkDaoQueries(tableName)
                    }

                    "IdentifiableDataObjectDao" -> {
                        file += "import org.hisp.dhis.android.core.common.DataColumns"
                        file += "import org.hisp.dhis.android.core.common.IdentifiableColumns"
                        file += "import org.hisp.dhis.android.core.common.State"
                        file += "\n"
                        file += "internal interface $generatedInterfaceName {\n\n"
                        file += buildIdentifiableDataObjectDaoQueries(tableName)
                    }

                    "IdentifiableDeletableDataObjectStoreDao" -> {
                        file += "import org.hisp.dhis.android.core.common.DataColumns"
                        file += "import org.hisp.dhis.android.core.common.State"
                        file += "import org.hisp.dhis.android.core.common.DeletableDataColumns"
                        file += "import org.hisp.dhis.android.core.common.IdentifiableColumns"
                        file += "\n"
                        file += "internal interface $generatedInterfaceName {\n\n"
                        file += buildIdentifiableDeletableDataObjectStoreDaoQueries(tableName)

                    }
                }

                file += "}\n"
            }
            logger.warn("Generated interface $generatedInterfaceName in $originalPackageName for $originalDaoName")
        }

        // Retorna los símbolos que fallaron la validación para que KSP los procese de nuevo si es necesario.
        return daoSymbols.filterNot { it.validate() }.toList()
    }

    private fun buildObjectDaoQueries(tableName: String): String {
        return "    @Query(\"DELETE FROM ${'$'}{${tableName}}\")\n" +
            "    suspend fun deleteAllRows(): Int\n\n"
    }

    private fun buildIdentifiableObjectDaoQueries(tableName: String): String {
        return buildObjectDaoQueries(tableName) +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}} WHERE uid = :uid\")\n" +
            "    suspend fun delete(uid: String): Int\n\n"
    }

    private fun buildLinkDaoQueries(tableName: String): String {
        return buildObjectDaoQueries(tableName) +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}} WHERE :parentColumn = :parentUid\")\n" +
            "    suspend fun deleteLinksForMasterUid(parentColumn: String, parentUid: String): Int\n\n" +
            "    @Query(\"DELETE FROM ${'$'}{${tableName}}\")" +
            "    suspend fun deleteLinksForMasterUid(): Int"
    }

    private fun buildIdentifiableDataObjectDaoQueries(tableName: String): String {
        return buildIdentifiableObjectDaoQueries(tableName) +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DataColumns.SYNC_STATE} = :state WHERE " +
            "${'$'}{IdentifiableColumns.UID} = :uid\")\n" +
            "    suspend fun setSyncState(uid: String, state: State)\n\n" +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DataColumns.SYNC_STATE} = :state WHERE " +
            "${'$'}{IdentifiableColumns.UID} IN (:uids)\")\n" +
            "    suspend fun setSyncState(uids: List<String>, state: State)\n\n" +
            "    @Query(\"UPDATE ${'$'}{${tableName}} SET ${'$'}{DataColumns.SYNC_STATE} = :newstate WHERE " +
            "${'$'}{IdentifiableColumns.UID} = :uid AND ${'$'}{DataColumns.SYNC_STATE} = :updateState\")\n" +
            "    suspend fun setSyncStateIfUploading(uid: String, newstate: State, updateState: State)\n\n"
    }

    private fun buildIdentifiableDeletableDataObjectStoreDaoQueries(tableName: String): String {
        return buildIdentifiableDataObjectDaoQueries(tableName) +
            "    @Query(\"UPDATE Constant SET ${'$'}{DeletableDataColumns.DELETED} = 1 " +
            "WHERE ${'$'}{IdentifiableColumns.UID} = :uid\")\n" +
            "    suspend fun setDeleted(uid: String): Int\n\n"
    }

    // Operador de extensión para escribir Strings fácilmente en OutputStream
    private operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }
}
