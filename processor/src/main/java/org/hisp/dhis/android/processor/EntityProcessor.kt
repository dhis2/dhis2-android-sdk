/*
 *  Copyright (c) 2004-2025, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis2.processors

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStream

class EntityProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(Entity::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()

        logger.info("Number of Entity classes processed: ${symbols.toList().size} classes")

        symbols.forEach { symbol ->
            val packageName = symbol.packageName.asString()
            val tableName = getTableName(symbol)
            val tableInfoClass = tableName + "TableInfo"

            val columnNames = getColumns(symbol)

            val file = codeGenerator.createNewFile(
                // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
                // Learn more about incremental processing in KSP from the official docs:
                // https://kotlinlang.org/docs/ksp-incremental.html
                dependencies = Dependencies(false, symbol.containingFile!!),
                packageName = packageName,
                fileName = tableInfoClass,
            )

            file += """
            package $packageName
            
            import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
            import org.hisp.dhis.android.core.common.CoreColumns
               
            object $tableInfoClass {
                
                @JvmField
                val TABLE_INFO: TableInfo = object : TableInfo() {
                    override fun name(): String {
                        return "$tableName"
                    }
    
                    override fun columns(): CoreColumns {
                        return Columns()
                    }
                }
                
                class Columns : CoreColumns() {
                    companion object {
                        ${columnNames.joinToString(
                separator = "\n                        ",
            ) { name ->
                val upperName = camelToUpperSnakeCase(name)
                "const val $upperName = \"$name\""
            }}
                    }
                }
            }
            """.trimIndent()

            file.close()
        }

        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        return unableToProcess
    }

    private fun getTableName(symbol: KSClassDeclaration): String? {
        val entityAnnotation = symbol.annotations.find { a -> a.shortName.getShortName() == Entity::class.simpleName }

        return entityAnnotation?.let {
            entityAnnotation.arguments.find { a -> a.name?.getShortName() == "tableName" }?.value.toString()
        }
    }

    private fun camelToUpperSnakeCase(string: String): String {
        val pattern = "(?<=.)[A-Z]".toRegex()
        return string.replace(pattern, "_$0").uppercase()
    }

    private fun getColumns(symbol: KSClassDeclaration): List<String> {
        return symbol.getDeclaredProperties().map { getColumnName(it) }.toList()
    }

    private fun getColumnName(property: KSPropertyDeclaration): String {
        val serialNameValue = property.annotations
            .firstOrNull { it.shortName.getShortName() == ColumnInfo::class.simpleName }
            ?.arguments?.firstOrNull { it.name?.getShortName() == "name" }
            ?.value?.toString()

        return serialNameValue ?: property.simpleName.getShortName()
    }
}
