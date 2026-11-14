/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.arch.api.filters.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.network.common.filters.Filter
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FilterShould {

    @Serializable
    private data class ReferenceDTO(val id: String)

    @Serializable
    private data class SampleDTO(
        val isDefault: Boolean?,
        val lastUpdated: String?,
        val path: String?,
        val program: ReferenceDTO?,
        val programs: List<ReferenceDTO>?,
    )

    @Test
    fun generate_single_value_filters() {
        assertThat(Filter.eq<SampleDTO>("isDefault", true).generateString()).isEqualTo("isDefault:eq:true")
        assertThat(Filter.gt<SampleDTO>("lastUpdated", "2024-01-01").generateString())
            .isEqualTo("lastUpdated:gt:2024-01-01")
        assertThat(Filter.like<SampleDTO>("path", "uid1").generateString()).isEqualTo("path:like:uid1")
    }

    @Test
    fun generate_in_filters() {
        assertThat(Filter.`in`<SampleDTO>("program.id", listOf("uid1", "uid2")).generateString())
            .isEqualTo("program.id:in:[uid1,uid2]")
        assertThat(Filter.`in`<SampleDTO>("program.id", emptyList()).generateString()).isEqualTo("program.id:in:[]")
        assertThat(Filter.uidIn(listOf("uid1", "uid2")).generateString()).isEqualTo("id:in:[uid1,uid2]")
        assertThat(Filter.inUnchecked("categories.id", listOf("uid1")).generateString())
            .isEqualTo("categories.id:in:[uid1]")
    }

    @Test
    fun accept_paths_through_lists() {
        assertThat(Filter.`in`<SampleDTO>("programs.id", listOf("uid1")).generateString())
            .isEqualTo("programs.id:in:[uid1]")
    }

    @Test
    fun reject_fields_that_are_not_dto_properties() {
        listOf("isdefault", "program.uid", "path.id", "categories.id").forEach { field ->
            val error = assertThrows(IllegalArgumentException::class.java) {
                Filter.`in`<SampleDTO>(field, listOf("uid1"))
            }
            assertThat(error.message).contains("'$field'")
        }
    }
}
