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
package org.hisp.dhis.android.core.option

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.common.ObjectWithStyleDTO
import org.hisp.dhis.android.network.option.OptionDTO
import org.hisp.dhis.android.network.option.optionDtoToDomainMapper
import org.junit.Test

class OptionShould : BaseObjectKotlinxShould("option/option.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val optionDTO = deserialize(OptionDTO.serializer())
        val option = optionDtoToDomainMapper(optionDTO)

        Truth.assertThat(option.uid()).isEqualTo("Y1ILwhy5VDY")
        Truth.assertThat(option.code()).isEqualTo("0-14 years")
        Truth.assertThat(option.created())
            .isEqualTo(BaseIdentifiableObject.parseDate("2014-08-18T12:39:16.000"))
        Truth.assertThat(option.lastUpdated())
            .isEqualTo(BaseIdentifiableObject.parseDate("2014-08-18T12:39:16.000"))
        Truth.assertThat(option.name()).isEqualTo("0-14 years")
        Truth.assertThat(option.displayName()).isEqualTo("0-14 years")
        Truth.assertThat(option.sortOrder()).isEqualTo(1)
        Truth.assertThat(option.optionSet()?.uid()).isEqualTo("VQ2lai3OfVG")
        Truth.assertThat(option.style()).isEqualTo(ObjectWithStyleDTO("#000", "my-icon-name"))
    }
}
