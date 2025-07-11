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
package org.hisp.dhis.android.testapp.validation

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.junit.Test

class ValidationRuleRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val validationRule = d2.validationModule().validationRules().blockingGet()

        assertThat(validationRule.size).isEqualTo(2)
    }

    @Test
    fun filter_by_instruction() {
        val validationRule = d2.validationModule().validationRules()
            .byInstruction().eq("PCV 2 cannot be higher than PCV 1 doses given")
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_importance() {
        val validationRule = d2.validationModule().validationRules()
            .byImportance().eq(ValidationRuleImportance.LOW)
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_operator() {
        val validationRule = d2.validationModule().validationRules()
            .byOperator().eq(ValidationRuleOperator.less_than)
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_period_type() {
        val validationRule = d2.validationModule().validationRules()
            .byPeriodType().eq(PeriodType.Monthly)
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(2)
    }

    @Test
    fun filter_by_skip_form_validation() {
        val validationRule = d2.validationModule().validationRules()
            .bySkipFormValidation().isTrue
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_left_side_expression() {
        val validationRule = d2.validationModule().validationRules()
            .byLeftSideExpression().eq("#{GCGfEY82Wz6.psbwp3CQEhs}")
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_left_side_description() {
        val validationRule = d2.validationModule().validationRules()
            .byLeftSideDescription().eq("At Measles, Slept under LLITN last night, >=1 year Fixed")
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_left_side_missing_value_strategy() {
        val validationRule = d2.validationModule().validationRules()
            .byLeftSideMissingValueStrategy().eq(MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING)
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_right_side_expression() {
        val validationRule = d2.validationModule().validationRules()
            .byRightSideExpression().eq("#{YtbsuPPo010.psbwp3CQEhs}")
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_right_side_description() {
        val validationRule = d2.validationModule().validationRules()
            .byRightSideDescription().eq("Measles, >=1 year Fixed[34.291]")
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_right_side_missing_value_strategy() {
        val validationRule = d2.validationModule().validationRules()
            .byRightSideMissingValueStrategy().eq(MissingValueStrategy.NEVER_SKIP)
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit_levels() {
        val validationRule = d2.validationModule().validationRules()
            .byOrganisationUnitLevels().like("3")
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_set_uids() {
        val validationRule = d2.validationModule().validationRules()
            .byDataSetUids(listOf("BfMAe6Itzgt"))
            .blockingGet()

        assertThat(validationRule.size).isEqualTo(2)
    }
}
