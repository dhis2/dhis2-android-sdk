package org.hisp.dhis.android.processor

import androidx.room.Dao
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStream

import

class DaoQueriesProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val daoSymbols = resolver.getSymbolsWithAnnotation(Dao::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE } // Procesamos solo interfaces DAO

        if (!daoSymbols.iterator().hasNext()) {
            return emptyList() // No hay DAOs para procesar
        }

        daoSymbols.forEach { daoDeclaration ->
            logger.info("Processing DAO: ${daoDeclaration.qualifiedName?.asString()}")

            val generateQueriesAnnotation = daoDeclaration.annotations.firstOrNull {
                // Compara usando el nombre completamente cualificado de tu anotación
                it.annotationType.resolve().declaration.qualifiedName?.asString() == GenerateDaoQueries::class.qualifiedName
            }

            if (generateQueriesAnnotation == null) {
                logger.info("DAO ${daoDeclaration.qualifiedName?.asString()} is not annotated with @GenerateDaoQueries. Skipping.")
                return@forEach
            }

            val tableNameArgument = generateQueriesAnnotation.arguments
                .firstOrNull { it.name?.asString() == "tableName" }?.value as? String

            if (tableNameArgument == null || tableNameArgument.isBlank()) {
                logger.error(
                    "Could not find a valid tableName argument in @GenerateDaoQueries for ${daoDeclaration.qualifiedName?.asString()}",
                    daoDeclaration
                )
                return@forEach
            }
            val tableName = tableNameArgument
            logger.info("Table name for ${daoDeclaration.qualifiedName?.asString()} is '$tableName'")

            val superInterfaceSimpleNames = daoDeclaration.superTypes
                .mapNotNull { it.resolve().declaration.simpleName.asString() }
                .toList()
            logger.info("DAO ${daoDeclaration.qualifiedName?.asString()} super interface simple names: $superInterfaceSimpleNames")

            // Determina el tipo de interfaz base para la lógica de generación de queries
            // TODO: Refinar esta lógica de detección si las interfaces base no están directamente en los nombres simples
            // o si hay jerarquías más profundas que necesitas inspeccionar.
            val baseInterfaceType: String? = when {
                superInterfaceSimpleNames.any { it == "ObjectDao" } -> "ObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableDeletableDataObjectStoreDao" } -> "IdentifiableDeletableDataObjectStoreDao"
                superInterfaceSimpleNames.any { it == "IdentifiableDataObjectDao" } -> "IdentifiableDataObjectDao"
                superInterfaceSimpleNames.any { it == "IdentifiableObjectStoreImpl" } -> "IdentifiableObjectStoreImpl" // Nota: Impl sugiere clase, asegúrate de que es interfaz
                superInterfaceSimpleNames.any { it == "LinkStoreImpl" } -> "LinkStoreImpl" // Nota: Impl sugiere clase, asegúrate de que es interfaz
                // TODO: Añadir más casos según tus interfaces base
                else -> null
            }

            if (baseInterfaceType == null) {
                logger.warn("DAO ${daoDeclaration.qualifiedName?.asString()} does not extend a recognized base interface for query generation. Skipping specific query generation.")
                // Podrías decidir generar una interfaz vacía o no generar nada.
                // Por ahora, saltamos la generación de la interfaz si no se reconoce el tipo base.
                return@forEach
            }
            logger.info("Base interface type for ${daoDeclaration.qualifiedName?.asString()} is $baseInterfaceType")

            val originalPackageName = daoDeclaration.packageName.asString()
            val originalDaoName = daoDeclaration.simpleName.asString()
            // Convención de nombres para la interfaz generada
            val generatedInterfaceName = "${originalDaoName}GeneratedQueries"

            val fileOutputStream = codeGenerator.createNewFile(
                dependencies = androidx.baselineprofile.gradle.utils.Dependencies(
                    true,
                    daoDeclaration.containingFile!!
                ),
                packageName = originalPackageName,
                fileName = generatedInterfaceName
            )

            fileOutputStream.use { file ->
                file += "// Generated by DaoQueryGeneratorProcessor for $originalDaoName\n"
                file += "package $originalPackageName\n\n"
                file += "import androidx.room.Query\n"
                // TODO: Importar cualquier otra anotación o clase común necesaria (ej. IdentifiableColumns, DeletableDataColumns)
                // Ejemplo:
                // file += "import org.hisp.dhis.android.core.common.IdentifiableColumns\n"
                // file += "import org.hisp.dhis.android.core.arch.db.stores.internal.DeletableDataColumns\n"
                file += "\n"
                file += "internal interface $generatedInterfaceName {\n\n"

                // Lógica de generación de métodos basada en baseInterfaceType
                when (baseInterfaceType) {
                    "ObjectDao" -> {
                        // TODO: Implementar la generación de queries para ObjectDao usando 'tableName'
                        // Ejemplo:
                        // file += "    @Query(\"DELETE FROM $tableName WHERE \${org.hisp.dhis.android.core.common.IdentifiableColumns.UID} = :uid\")\n"
                        // file += "    suspend fun delete(uid: String): Int\n\n"
                        // file += "    @Query(\"DELETE FROM $tableName\")\n"
                        // file += "    suspend fun deleteAllRows(): Int\n"
                    }

                    "IdentifiableDeletableDataObjectStoreDao" -> {
                        // TODO: Implementar la generación de queries para IdentifiableDeletableDataObjectStoreDao usando 'tableName'
                        // Ejemplo:
                        // file += "    @Query(\"UPDATE $tableName SET \${org.hisp.dhis.android.core.arch.db.stores.internal.DeletableDataColumns.DELETED} = 1 WHERE \${org.hisp.dhis.android.core.common.IdentifiableColumns.UID} = :uid\")\n"
                        // file += "    suspend fun setDeleted(uid: String): Int\n"
                    }

                    "IdentifiableDataObjectDao" -> {
                        // TODO: Implementar la generación de queries para IdentifiableDataObjectDao usando 'tableName'
                    }

                    "IdentifiableObjectStoreImpl" -> {
                        // TODO: Implementar la generación de queries para IdentifiableObjectStoreImpl usando 'tableName'
                        //       (Asegúrate de que esta lógica tiene sentido para lo que representa esta interfaz/clase base)
                    }

                    "LinkStoreImpl" -> {
                        // TODO: Implementar la generación de queries para LinkStoreImpl usando 'tableName'
                        //       (Asegúrate de que esta lógica tiene sentido para lo que representa esta interfaz/clase base)
                    }
                    // TODO: Añadir más casos para otras interfaces base
                }

                file += "}\n"
            }
            logger.info("Generated interface $generatedInterfaceName in $originalPackageName for $originalDaoName")
        }

        // Retorna los símbolos que fallaron la validación para que KSP los procese de nuevo si es necesario.
        return daoSymbols.filterNot { it.validate() }.toList()
    }

    // Operador de extensión para escribir Strings fácilmente en OutputStream
    private operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }
}
