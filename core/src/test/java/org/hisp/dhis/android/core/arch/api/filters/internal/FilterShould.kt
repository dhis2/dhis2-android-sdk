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

package org.hisp.dhis.android.core.arch.api.filters.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.network.common.filters.Filter
import org.hisp.dhis.android.network.common.filters.FilterPath
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FilterShould {

    @Test
    fun generate_single_value_filters() {
        assertThat(Filter.eq(FilterPath.CATEGORY_COMBO_IS_DEFAULT, true).generateString())
            .isEqualTo("isDefault:eq:true")
        assertThat(Filter.gt(FilterPath.RELATIONSHIP_TYPE_LAST_UPDATED, "2024-01-01").generateString())
            .isEqualTo("lastUpdated:gt:2024-01-01")
        assertThat(Filter.like(FilterPath.ORGANISATION_UNIT_PATH, "uid1").generateString())
            .isEqualTo("path:like:uid1")
    }

    @Test
    fun generate_in_filters() {
        assertThat(Filter.`in`(FilterPath.PROGRAM_STAGE_PROGRAM, listOf("uid1", "uid2")).generateString())
            .isEqualTo("program.id:in:[uid1,uid2]")
        assertThat(Filter.`in`(FilterPath.PROGRAM_STAGE_PROGRAM, emptyList()).generateString())
            .isEqualTo("program.id:in:[]")
        assertThat(Filter.uidIn(listOf("uid1", "uid2")).generateString()).isEqualTo("id:in:[uid1,uid2]")
    }
}
