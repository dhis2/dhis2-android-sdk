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

package org.hisp.dhis.android.processor

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
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import org.hisp.dhis.android.annotations.ModelBuilder
import java.io.OutputStream

class ModelBuilderProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    private val processedSymbols = mutableSetOf<String>()

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(ModelBuilder::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .filterNot { processedSymbols.contains(it.qualifiedName?.asString()) }

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
            val implementationClass =
                baseClass?.let { ": ${baseClass.builder}.Builder<$innerBuilderName> " } ?: ""
            val overridenFields = baseClass?.fields ?: emptyList()

            val builderClass = symbol.declarations
                .filterIsInstance<KSClassDeclaration>()
                .find { it.simpleName.asString() == "Builder" }

            val isClassInternal = symbol.modifiers.contains(Modifier.INTERNAL)
            val isBuilderInternal = builderClass?.modifiers?.contains(Modifier.INTERNAL) == true

            val visibilityModifier = if (isClassInternal || isBuilderInternal) "internal " else ""

            // Only primary-constructor properties are model fields
            val constructorParamNames = symbol.primaryConstructor?.parameters
                ?.mapNotNull { it.name?.asString() }
                ?.toSet()
                .orEmpty()

            val fields = symbol.declarations.filterIsInstance<KSPropertyDeclaration>()
                .filter { it.simpleName.asString() in constructorParamNames }
                .map { field ->
                    ClassField(
                        name = getPropertyName(field.simpleName.asString()),
                        type = field.type.resolve(),
                        isInternal = field.modifiers.contains(Modifier.INTERNAL),
                    )
                }

            val typeImports = fields.flatMap { field ->
                collectTypeImports(field.type)
            }.distinct().sorted()

            file += """
            package $packageName
            
            ${
                typeImports.joinToString("\n            ") { typeImport ->
                    "import $typeImport"
                }
            }
            ${baseClass?.builderImport ?: ""}
            import kotlin.properties.Delegates
               
            ${visibilityModifier}open class $builderName $implementationClass{
                ${
                fields.joinToString("\n                ") { field ->
                    getBuilderInternalProperty(field)
                }}
                
                ${
                fields.joinToString("\n                ") { field ->
                    val name = field.name
                    val type = renderType(field.type)
                    val isOverride = overridenFields.contains(name)
                    val optOverride = if (isOverride) "override " else ""
                    val optInternal = if (field.isInternal && !isOverride) "internal " else ""

                    "${optInternal}${optOverride}fun $name ($name: $type): $innerBuilderName = " +
                        "this.also { this.$name = $name } as $innerBuilderName"
                }
            }
                
                open fun build(): $className {
                    return $className(
                        ${
                fields.joinToString("\n                        ") { field ->
                    field.name + ","
                }
            }
                    )
                }
                
                companion object {
                    fun from(item: $className): $className.Builder {
                        return $className.Builder().apply {
                            ${
                fields.joinToString("\n                            ") { field ->
                    val name = field.name
                    "$name(item.$name)"
                }
            }
                        }
                    }
                }
            
            }   
            """.trimIndent()

            file.close()
            processedSymbols.add(symbol.qualifiedName?.asString()!!)
        }

        return emptyList()
    }

    private fun getBuilderInternalProperty(field: ClassField): String {
        val isPrimitive = listOf("Int", "Long", "Short", "Byte", "Char", "Float", "Double", "Boolean")
            .contains(field.type.toString())
        val isNotNull = !field.type.isMarkedNullable
        val type = renderType(field.type)
        // An internal field may reference an internal type. A `protected` backing property in a
        // public builder would leak that internal type, so use `private` visibility instead. It
        // must stay `private` (not `internal`): the backing property is only used by the generated
        // setters and build() within this class, and a module-visible `internal` property would
        // shadow identically-named properties inside callers' `builder().apply { }` blocks.
        val visibility = if (field.isInternal) "private" else "protected"

        return when {
            isNotNull && isPrimitive -> "$visibility var ${field.name} by Delegates.notNull<$type>()"
            isNotNull -> "$visibility lateinit var ${field.name}: $type"
            else -> "$visibility var ${field.name}: $type = null"
        }
    }

    /**
     * Renders a [KSType] as Kotlin source. [KSType.toString] wraps typealiases (e.g. the
     * `kotlin.Exception` alias of `java.lang.Exception`) as `[typealias Exception]`, which is not
     * valid source. This unwraps that form back to the plain type name while leaving every other
     * type rendering untouched.
     */
    private fun renderType(type: KSType): String {
        return type.toString().replace(TYPEALIAS_REGEX) { it.groupValues[1] }
    }

    /**
     * Collects the imports required to render [type], walking type arguments recursively so that types nested
     * more than one level deep (e.g. the enum in `Map<String, Map<String, UploadQuality>>`) are imported too.
     */
    private fun collectTypeImports(type: KSType): List<String> {
        val argumentImports = type.arguments.flatMap { argument ->
            argument.type?.resolve()?.let { collectTypeImports(it) } ?: emptyList()
        }

        return argumentImports + listOfNotNull(type.declaration.qualifiedName?.asString())
    }

    private fun getPropertyName(name: String): String {
        val keyWords = listOf("in", "operator", "object")

        return when {
            keyWords.contains(name) -> "`$name`"
            else -> name
        }
    }

    companion object {
        private val TYPEALIAS_REGEX = Regex("""\[typealias (\w+)]""")

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
        val filterOperators = BaseClass(
            "FilterOperators",
            "FilterOperators",
            "import org.hisp.dhis.android.core.common.FilterOperators",
            listOf("le", "ge", "gt", "lt", "eq", "`in`", "like", "dateFilter", "isEmpty"),
        )
        val queryCriteria = BaseClass(
            "FilterQueryCriteria",
            "FilterQueryCriteria",
            "import org.hisp.dhis.android.core.common.FilterQueryCriteria",
            listOf(
                "followUp",
                "organisationUnit",
                "ouMode",
                "assignedUserMode",
                "order",
                "displayColumnOrder",
                "eventDate",
                "lastUpdatedDate",
            ),
        )

        // The order here matters. The first matching base class is used for the builder
        val baseClasses = listOf(
            nameable,
            identifiable,
            filterOperators,
            queryCriteria,
        )
    }
}

data class BaseClass(
    val name: String,
    val builder: String,
    val builderImport: String,
    val fields: List<String>,
)

data class ClassField(
    val name: String,
    val type: KSType,
    val isInternal: Boolean = false,
)
