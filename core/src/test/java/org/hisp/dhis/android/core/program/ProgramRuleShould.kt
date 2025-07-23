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
package org.hisp.dhis.android.core.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.programrule.ProgramRuleDTO
import org.junit.Test

class ProgramRuleShould : CoreObjectShould("program/program_rule.json") {

    @Test
    override fun map_from_json_string() {
        val programRuleDTO = deserialize(ProgramRuleDTO.serializer())
        val programRule = programRuleDTO.toDomain()

        assertThat(programRule.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-09-14T21:17:40.841"))
        assertThat(programRule.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-09-14T22:22:15.383"))
        assertThat(programRule.uid()).isEqualTo("NAgjOfWMXg6")

        assertThat(programRule.name()).isEqualTo("Ask for comment for low apgar")
        assertThat(programRule.displayName()).isEqualTo("Ask for comment for low apgar")

        assertThat(programRule.programStage()).isNull()
        assertThat(programRule.program()!!.uid()).isEqualTo("IpHINAT79UW")
        assertThat(programRule.priority()).isNull()
        assertThat(programRule.condition())
            .isEqualTo("#{apgarscore} >= 0 && #{apgarscore} < 4 && #{apgarcomment} == ''")
        assertThat(programRule.programRuleActions()).isNotNull()
        assertThat(programRule.programRuleActions()).isNotEmpty()
        assertThat(programRule.programRuleActions()!![0].uid()).isEqualTo("v434s5YPDcP")
    }
}
