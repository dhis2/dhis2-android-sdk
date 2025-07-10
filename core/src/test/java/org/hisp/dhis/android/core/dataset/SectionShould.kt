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
package org.hisp.dhis.android.core.dataset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.dataset.SectionDTO
import org.junit.Test

class SectionShould : CoreObjectShould("dataset/section.json") {
    @Test
    override fun map_from_json_string() {
        val sectionDTO = deserialize(SectionDTO.serializer())
        val section = sectionDTO.toDomain()

        assertThat(section.uid()).isEqualTo("Y2rk0vzgvAx")
        assertThat(section.code()).isEqualTo("Code123")
        assertThat(section.lastUpdated()).isEqualTo(
            DateUtils.DATE_FORMAT.parse("2016-10-12T13:22:42.731"),
        )
        assertThat(section.created()).isEqualTo(
            DateUtils.DATE_FORMAT.parse("2012-04-26T19:26:02.933"),
        )
        assertThat(section.name()).isEqualTo("Immunization")
        assertThat(section.displayName()).isEqualTo("Immunization")

        assertThat(section.description()).isEqualTo("Immunization dose administration")
        assertThat(section.dataSet()!!.uid()).isEqualTo("BfMAe6Itzgt")
        assertThat(section.sortOrder()).isEqualTo(2)
        assertThat(section.showRowTotals()).isFalse()
        assertThat(section.showColumnTotals()).isFalse()
        assertThat(section.disableDataElementAutoGroup()).isTrue()
        assertThat(section.dataElements()!!.size).isEqualTo(15)
        assertThat(section.dataElements()!![0].uid()).isEqualTo("s46m5MS0hxu")
        assertThat(section.greyedFields()!!.size).isEqualTo(1)
        assertThat(section.greyedFields()!![0].uid()).isEqualTo("ca8lfO062zg.Prlt0C1RF0s")

        assertThat(section.displayOptions()!!.afterSectionText()).isEqualTo(null)
        assertThat(section.displayOptions()!!.beforeSectionText()).isEqualTo("Text before section")
        assertThat(section.displayOptions()!!.pivotMode()).isEqualTo(SectionPivotMode.DEFAULT)
        assertThat(section.displayOptions()!!.pivotedCategory()).isEqualTo(null)
    }
}
