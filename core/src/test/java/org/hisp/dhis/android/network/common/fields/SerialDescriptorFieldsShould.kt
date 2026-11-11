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

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class SerialDescriptorFieldsShould {

    @Serializable
    private enum class SampleType { A, B }

    @Serializable
    private data class ReferenceDTO(val id: String)

    @JvmInline
    @Serializable
    private value class DateDTO(val date: String?)

    @Serializable
    private data class ChildDTO(val id: String, val parent: ReferenceDTO?)

    @Serializable
    private data class SampleDTO(
        val id: String,
        @SerialName("displayName") val name: String?,
        val type: SampleType?,
        val count: Int = 0,
        val reference: ReferenceDTO?,
        val children: List<ChildDTO>?,
        val tags: List<String>?,
        val created: DateDTO?,
        val coordinates: JsonElement?,
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    private data class AliasedDTO(
        val id: String,
        @JsonNames("legacyReferences", "oldReferences") val references: List<ReferenceDTO>?,
    )

    @Serializable
    private data class NodeDTO(val id: String, val parent: NodeDTO?)

    @Serializable
    private data class MapDTO(val id: String, val values: Map<String, String>)

    private fun fieldsOf(dto: KSerializer<*>): String = dto.descriptor.toFieldsString()

    @Test
    fun expand_nested_dtos_and_request_value_classes_and_json_whole() {
        assertThat(fieldsOf(SampleDTO.serializer())).isEqualTo(
            "id,displayName,type,count,reference[id],children[id,parent[id]],tags,created,coordinates",
        )
    }

    @Test
    fun request_alternative_json_names_too() {
        assertThat(fieldsOf(AliasedDTO.serializer())).isEqualTo(
            "id,references[id],legacyReferences[id],oldReferences[id]",
        )
    }

    @Test
    fun leave_out_excluded_top_level_properties() {
        assertThat(SampleDTO.serializer().descriptor.toFieldsString(exclude = setOf("children", "tags"))).isEqualTo(
            "id,displayName,type,count,reference[id],created,coordinates",
        )
    }

    @Test
    fun leave_out_alternative_json_names_of_excluded_properties() {
        assertThat(AliasedDTO.serializer().descriptor.toFieldsString(exclude = setOf("references"))).isEqualTo("id")
    }

    @Test
    fun reject_excluded_names_that_are_not_properties() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            SampleDTO.serializer().descriptor.toFieldsString(exclude = setOf("childrens"))
        }
        assertThat(error.message).contains("[childrens]")
    }

    @Test
    fun reject_cyclic_dtos() {
        val error = assertThrows(IllegalArgumentException::class.java) { fieldsOf(NodeDTO.serializer()) }
        assertThat(error.message).contains("Cyclic DTO reference")
    }

    @Test
    fun reject_properties_whose_fields_cannot_be_inferred() {
        val error = assertThrows(IllegalArgumentException::class.java) { fieldsOf(MapDTO.serializer()) }
        assertThat(error.message).contains(".values'")
    }
}
