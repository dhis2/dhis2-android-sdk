/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.network.common.fields

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.JsonNames

/**
 * Builds the `fields` parameter from the DTO: every property except [NotRequested] ones is requested, expanding
 * nested DTOs and list elements recursively. Top-level properties in [exclude] are left out; unknown names fail.
 */
internal fun SerialDescriptor.toFieldsString(exclude: Set<String> = emptySet()): String {
    val unknownNames = exclude - elementNames.toSet()
    require(unknownNames.isEmpty()) { "$unknownNames are not properties of ${nonNullSerialName()}" }
    return fieldsString(path = emptyList(), exclude = exclude)
}

private fun SerialDescriptor.fieldsString(path: List<String>, exclude: Set<String> = emptySet()): String {
    val currentPath = path + nonNullSerialName()
    require(nonNullSerialName() !in path) { "Cyclic DTO reference: ${currentPath.joinToString(" -> ")}" }

    return (0 until elementsCount).filterNot { getElementName(it) in exclude || isNotRequested(it) }.flatMap { index ->
        requestedNames(index).map { name -> getElementDescriptor(index).fieldString(name, currentPath) }
    }.joinToString(",")
}

private fun SerialDescriptor.isNotRequested(index: Int): Boolean {
    return getElementAnnotations(index).any { it is NotRequested }
}

/**
 * Alternative names declared with `@JsonNames` are requested too, so the DTO gets the value whichever name the
 * server version uses.
 */
private fun SerialDescriptor.requestedNames(index: Int): List<String> {
    val alternativeNames = getElementAnnotations(index).filterIsInstance<JsonNames>().flatMap { it.names.toList() }
    return listOf(getElementName(index)) + alternativeNames
}

private fun SerialDescriptor.fieldString(name: String, path: List<String>): String {
    val type = unwrapLists()
    return when {
        type.isLeaf() -> name
        type.kind == StructureKind.CLASS || type.kind == StructureKind.OBJECT ->
            if (type.elementsCount == 0) name else "$name[${type.fieldsString(path)}]"
        else -> throw IllegalArgumentException(
            "Cannot infer the fields of '${path.joinToString(".")}.$name' (${type.serialName}, kind ${type.kind})",
        )
    }
}

private fun SerialDescriptor.unwrapLists(): SerialDescriptor {
    return if (kind == StructureKind.LIST) getElementDescriptor(0).unwrapLists() else this
}

/**
 * Value classes are encoded as their single property and JSON elements are free-form, so both are requested whole.
 */
private fun SerialDescriptor.isLeaf(): Boolean {
    return kind is PrimitiveKind ||
        kind == SerialKind.ENUM ||
        isInline ||
        nonNullSerialName() in jsonElementSerialNames
}

private fun SerialDescriptor.nonNullSerialName(): String = serialName.removeSuffix("?")

private val jsonElementSerialNames = setOf(
    "kotlinx.serialization.json.JsonElement",
    "kotlinx.serialization.json.JsonObject",
    "kotlinx.serialization.json.JsonArray",
    "kotlinx.serialization.json.JsonPrimitive",
)
