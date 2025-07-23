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
package org.hisp.dhis.android.core.trackedentity.search

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.trackedentityinstance.SearchGridDTO
import org.junit.Test

class SearchGridMapperShould : CoreObjectShould("trackedentity/search_grid.json") {
    @Test
    override fun map_from_json_string() {
        val searchGrid = deserialize(SearchGridDTO.serializer())
        val teis = searchGrid.toDomain()

        assertThat(teis.size).isEqualTo(2)

        val tei1 = teis[0]
        assertThat(tei1.uid()).isEqualTo("PmslDkqLqeG")
        assertThat(tei1.created()).isEqualTo(DateUtils.SPACE_DATE_FORMAT.parse("2018-04-27 17:34:17.005"))
        assertThat(tei1.lastUpdated()).isEqualTo(DateUtils.SPACE_DATE_FORMAT.parse("2018-04-27 17:34:28.442"))
        assertThat(tei1.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(tei1.trackedEntityType()).isEqualTo("nEenWmSyUEp")

        val attValue1A = tei1.trackedEntityAttributeValues()!![0]
        val attValue1B = tei1.trackedEntityAttributeValues()!![1]
        assertThat(attValue1A.value()).isEqualTo("Firsty")
        assertThat(attValue1B.value()).isEqualTo("Namey")

        val tei2 = teis[1]
        assertThat(tei2.uid()).isEqualTo("Lf9FhXshRnd")
        assertThat(tei2.created()).isEqualTo(DateUtils.SPACE_DATE_FORMAT.parse("2018-04-26 06:10:51.634"))
        assertThat(tei2.lastUpdated()).isEqualTo(DateUtils.SPACE_DATE_FORMAT.parse("2018-04-26 06:10:52.944"))
        assertThat(tei2.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(tei2.trackedEntityType()).isEqualTo("nEenWmSyUEp")

        val attValue2A = tei2.trackedEntityAttributeValues()!![0]
        val attValue2B = tei2.trackedEntityAttributeValues()!![1]
        assertThat(attValue2A.value()).isEqualTo("Jorge")
        assertThat(attValue2B.value()).isEqualTo("Fernandez")
    }
}
