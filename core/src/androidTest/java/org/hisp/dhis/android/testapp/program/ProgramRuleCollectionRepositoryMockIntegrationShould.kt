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
package org.hisp.dhis.android.testapp.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ProgramRuleCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val rules = d2.programModule().programRules().blockingGet()

        assertThat(rules.size).isEqualTo(3)
    }

    @Test
    fun filter_by_priority() {
        val rules = d2.programModule().programRules()
            .byPriority()
            .eq(2)
            .blockingGet()

        assertThat(rules.size).isEqualTo(2)
    }

    @Test
    fun filter_by_condition() {
        val rules = d2.programModule().programRules()
            .byCondition()
            .eq("#{hemoglobin} < 9")
            .blockingGet()

        assertThat(rules.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program() {
        val rules = d2.programModule().programRules()
            .byProgramUid()
            .eq("lxAQ7Zs9VYR")
            .blockingGet()

        assertThat(rules.size).isEqualTo(3)
    }

    @Test
    fun filter_by_program_stage() {
        val rules = d2.programModule().programRules()
            .byProgramStageUid()
            .eq("dBwrot7S420")
            .blockingGet()

        assertThat(rules.size).isEqualTo(1)
    }

    @Test
    fun include_program_rule_actions_as_children() {
        val programRule = d2.programModule().programRules()
            .withProgramRuleActions().one().blockingGet()
        assertThat(programRule!!.programRuleActions()!!.size).isEqualTo(1)
        assertThat(programRule.programRuleActions()!![0].content())
            .isEqualTo("The hemoglobin value cannot be above 99")
    }
}
