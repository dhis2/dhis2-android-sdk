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
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.trackedentityinstance.SearchGridDTO
import org.junit.Test

class SearchGridShould : CoreObjectShould("trackedentity/search_grid.json") {
    @Test
    override fun map_from_json_string() {
        val searchGrid = deserialize(SearchGridDTO.serializer())

        assertThat(searchGrid.headers.size).isEqualTo(9)

        val firstHeader = searchGrid.headers[0]
        assertThat(firstHeader.name).isEqualTo("instance")
        assertThat(firstHeader.column).isEqualTo("Instance")
        assertThat(firstHeader.type).isEqualTo("java.lang.String")
        assertThat(firstHeader.hidden).isFalse()
        assertThat(firstHeader.meta).isFalse()

        assertThat(searchGrid.width).isEqualTo(9)
        assertThat(searchGrid.height).isEqualTo(2)

        val metaData = searchGrid.metaData
        assertThat(metaData.names.size).isEqualTo(1)
        assertThat(metaData.names["nEenWmSyUEp"]).isEqualTo("Person")

        assertThat(searchGrid.rows.size).isEqualTo(2)

        val firstRow = searchGrid.rows[0]
        assertThat(firstRow[0]).isEqualTo("PmslDkqLqeG")
        assertThat(firstRow[1]).isEqualTo("2018-04-27 17:34:17.005")
        assertThat(firstRow[2]).isEqualTo("2018-04-27 17:34:28.442")
        assertThat(firstRow[3]).isEqualTo("DiszpKrYNg8")
        assertThat(firstRow[4]).isEqualTo("Ngelehun CHC")
        assertThat(firstRow[5]).isEqualTo("nEenWmSyUEp")
        assertThat(firstRow[6]).isEqualTo("")
        assertThat(firstRow[7]).isEqualTo("Firsty")
        assertThat(firstRow[8]).isEqualTo("Namey")

        val secondRow = searchGrid.rows[1]
        assertThat(secondRow[0]).isEqualTo("Lf9FhXshRnd")
        assertThat(secondRow[1]).isEqualTo("2018-04-26 06:10:51.634")
        assertThat(secondRow[2]).isEqualTo("2018-04-26 06:10:52.944")
        assertThat(secondRow[3]).isEqualTo("DiszpKrYNg8")
        assertThat(secondRow[4]).isEqualTo("Ngelehun CHC")
        assertThat(secondRow[5]).isEqualTo("nEenWmSyUEp")
        assertThat(secondRow[6]).isEqualTo("false")
        assertThat(secondRow[7]).isEqualTo("Jorge")
        assertThat(secondRow[8]).isEqualTo("Fernandez")
    }
}
