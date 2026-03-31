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

package org.hisp.dhis.processors

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import org.hisp.dhis.android.processor.ModelBuilder
import java.io.OutputStream

class ModelBuilderProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(ModelBuilder::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }

        symbols.forEach { symbol ->
            val packageName = symbol.packageName.asString()
            val className = symbol.simpleName.asString()
            val builderName = "${className}Builder"
            val innerBuilderName = "$className.Builder"

            val file = codeGenerator.createNewFile(
                // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
                // Learn more about incremental processing in KSP from the official docs:
                // https://kotlinlang.org/docs/ksp-incremental.html
                dependencies = Dependencies(false, symbol.containingFile!!),
                packageName = packageName,
                fileName = builderName,
            )

            val baseClass = baseClasses.find { baseClass ->
                symbol.getAllSuperTypes().any { it.declaration.simpleName.asString() == baseClass.name }
            }
            val implementationClass = baseClass?.let { ": ${baseClass.builder}.Builder<$builderName>() " } ?: ""
            val overridenFields = baseClass?.fields ?: emptyList()

            val fields = symbol.declarations.filterIsInstance<KSPropertyDeclaration>()

            val typeImports = fields.flatMap { field ->
                val fieldType = field.type.resolve()
                val arguments = fieldType.arguments.mapNotNull {
                    it.type?.resolve()?.declaration?.qualifiedName?.asString()
                }
                arguments + fieldType.declaration.qualifiedName?.asString()
            }.filterNotNull().distinct().sorted()

            file += """
            package $packageName
            
            ${
                typeImports.joinToString("\n            ") { typeImport ->
                    "import $typeImport"
                }
            }
            ${baseClass?.builderImport ?: ""}
               
            open class $builderName $implementationClass{
                ${
                fields.joinToString("\n                ") { field ->
                    when (field.type.resolve().isMarkedNullable) {
                        true -> "private var ${field.simpleName.asString()}: ${field.type.resolve()} = null"
                        false -> "private lateinit var ${field.simpleName.asString()}: ${field.type.resolve()}"
                    }
                }}
                
                ${
                fields.joinToString("\n                ") { field ->
                    val name = field.simpleName.asString()
                    val type = field.type.resolve().toString()
                    val optOverride = if (overridenFields.contains(name)) "override " else ""

                    "${optOverride}fun $name ($name: $type): $innerBuilderName = " +
                        "this.also { this.$name = $name } as $innerBuilderName"
                }
            }
                
                fun build(): $className {
                    return $className(
                        ${
                fields.joinToString("\n                        ") { field ->
                    field.simpleName.asString() + ","
                }
            }
                    )
                }
                
                companion object {
                    fun from(item: $className): $className.Builder {
                        return $className.Builder().apply {
                            ${
                fields.joinToString("\n                            ") { field ->
                    val name = field.simpleName.asString()
                    "$name(item.$name)"
                }
            }
                        }
                    }
                }
            
            }   
            """.trimIndent()

            file.close()
        }

        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        // return symbols.filterNot { it.validate() }.toList()
        return emptyList()
    }

    companion object {
        val identifiable = BaseClass(
            "IdentifiableObject",
            "BaseIdentifiableObject",
            "import org.hisp.dhis.android.core.common.BaseIdentifiableObject",
            listOf("uid", "code", "name", "displayName", "created", "lastUpdated", "deleted"),
        )
        val nameable = BaseClass(
            "NameableObject",
            "BaseNameableObject",
            "import org.hisp.dhis.android.core.common.BaseNameableObject",
            identifiable.fields + listOf("shortName", "displayShortName", "description", "displayDescription"),
        )

        // The order here matters. The first matching base class is used for the builder
        val baseClasses = listOf(
            nameable,
            identifiable,
        )
    }
}

data class BaseClass(
    val name: String,
    val builder: String,
    val builderImport: String,
    val fields: List<String>,
)
