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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.legendset.LegendDTO
import org.hisp.dhis.android.network.legendset.legendDtoToDomainMapper
import org.junit.Test

class LegendShould : BaseObjectKotlinxShould("legendset/legend.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val legendDTO = deserialize(LegendDTO.serializer())
        val legend = legendDtoToDomainMapper(legendDTO, "legendSetUid")

        Truth.assertThat(legend.uid()).isEqualTo("ZUUGJnvX40X")
        Truth.assertThat(legend.name()).isEqualTo("30 - 40")
        Truth.assertThat(legend.displayName()).isEqualTo("30 - 40")
        Truth.assertThat(legend.lastUpdated())
            .isEqualTo(BaseIdentifiableObject.parseDate("2017-06-02T11:40:44.279"))
        Truth.assertThat(legend.created())
            .isEqualTo(BaseIdentifiableObject.parseDate("2017-06-02T11:40:44.279"))
        Truth.assertThat(legend.startValue()).isEqualTo(30.5)
        Truth.assertThat(legend.endValue()).isEqualTo(40)
        Truth.assertThat(legend.color()).isEqualTo("#d9f0a3")
    }
}