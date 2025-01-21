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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.program.ProgramRuleAction
import org.hisp.dhis.android.core.program.ProgramRuleActionType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ProgramRuleActionCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(3)
    }

    @Test
    fun filter_by_data() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byData()
                .eq("data")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_content() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byContent()
                .eq("The hemoglobin value cannot be above 99")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_displayContent() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byDisplayContent()
                .eq("La valeur d'hémoglobine ne peut pas être supérieure à 99")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tracked_entity_attribute() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byTrackedEntityAttributeUid()
                .eq("cejWyOfXge6")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_indicator() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byProgramIndicatorUid()
                .eq("GSae40Fyppf")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_stage_section() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byProgramStageSectionUid()
                .eq("bbjzL5gp0NZ")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_rule_action_type() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byProgramRuleActionType()
                .eq(ProgramRuleActionType.SHOWWARNING)
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_stage() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byProgramStageUid()
                .eq("dBwrot7S420")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_element() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byDataElementUid()
                .eq("Ok9OQpitjQr")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_rule() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byProgramRuleUid()
                .eq("GC4gpdoSD4r")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_option() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byOptionUid()
                .eq("egT1YqFWsVk")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }

    @Test
    fun filter_by_option_group() {
        var programRuleActions: List<ProgramRuleAction?> =
            d2.programModule().programRuleActions()
                .byOptionGroupUid()
                .eq("j3JYGVCIEdz")
                .blockingGet()

        Truth.assertThat(programRuleActions.size).isEqualTo(1)
    }
}
