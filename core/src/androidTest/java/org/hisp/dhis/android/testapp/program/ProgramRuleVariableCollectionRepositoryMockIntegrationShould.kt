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
package org.hisp.dhis.android.testapp.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ProgramRuleVariableCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val ruleVariables = d2.programModule().programRuleVariables().blockingGet()

        assertThat(ruleVariables.size).isEqualTo(2)
    }

    @Test
    fun filter_by_use_code_for_option_set() {
        val ruleVariables = d2.programModule().programRuleVariables()
            .byUseCodeForOptionSet()
            .isFalse
            .blockingGet()

        assertThat(ruleVariables.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program() {
        val ruleVariables = d2.programModule().programRuleVariables()
            .byProgramUid()
            .eq("lxAQ7Zs9VYR")
            .blockingGet()

        assertThat(ruleVariables.size).isEqualTo(2)
    }

    @Test
    fun filter_by_program_stage() {
        val ruleVariables = d2.programModule().programRuleVariables()
            .byProgramStageUid()
            .eq("dBwrot7S420")
            .blockingGet()

        assertThat(ruleVariables.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_element() {
        val ruleVariables = d2.programModule().programRuleVariables()
            .byDataElementUid()
            .eq("sWoqcoByYmD")
            .blockingGet()

        assertThat(ruleVariables.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tracked_entity_attribute() {
        val ruleVariables = d2.programModule().programRuleVariables()
            .byTrackedEntityAttributeUid()
            .eq("cejWyOfXge6")
            .blockingGet()

        assertThat(ruleVariables.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_rule_variable_source_type() {
        val ruleVariables = d2.programModule().programRuleVariables()
            .byProgramRuleVariableSourceType()
            .eq(ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM)
            .blockingGet()

        assertThat(ruleVariables.size).isEqualTo(2)
    }
}
