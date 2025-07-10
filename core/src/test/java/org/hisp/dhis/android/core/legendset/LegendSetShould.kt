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
package org.hisp.dhis.android.core.legendset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.legendset.LegendSetDTO
import org.junit.Test

class LegendSetShould : CoreObjectShould("legendset/legend_set.json") {

    @Test
    override fun map_from_json_string() {
        val legendSetDTO = deserialize(LegendSetDTO.serializer())
        val legendSet = legendSetDTO.toDomain()

        assertThat(legendSet.uid()).isEqualTo("TiOkbpGEud4")
        assertThat(legendSet.name()).isEqualTo("Age 15y interval")
        assertThat(legendSet.displayName()).isEqualTo("Age 15y interval")
        assertThat(legendSet.code()).isEqualTo("AGE15YINT")
        assertThat(legendSet.lastUpdated())
            .isEqualTo(BaseIdentifiableObject.parseDate("2017-06-02T11:41:01.999"))
        assertThat(legendSet.created())
            .isEqualTo(BaseIdentifiableObject.parseDate("2017-06-02T11:40:33.452"))
        assertThat(legendSet.symbolizer()).isEqualTo("color")

        val legends = legendSet.legends()

        assertThat(legends?.getOrNull(0)?.uid()).isEqualTo("BzQkRWHS7lu")
        assertThat(legends?.getOrNull(0)?.name()).isEqualTo("45 - 60")
        assertThat(legends?.getOrNull(1)?.uid()).isEqualTo("kEf6QhFVMab")
        assertThat(legends?.getOrNull(1)?.name()).isEqualTo("15 - 30")
    }
}
