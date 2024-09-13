/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.validation.engine.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.parser.internal.service.ExpressionServiceContext
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.ValidationRuleExpression
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.junit.Test

class ValidationExecutorShould {

    private val dataElementStore = mock<DataElementStore>()
    private val categoryOptionComboStore = mock<CategoryOptionComboStore>()
    private val organisationUnitGroupStore = mock<OrganisationUnitGroupStore>()
    private val programStageStore = mock<ProgramStageStore>()

    private val uidGenerator = UidGeneratorImpl()
    private val samplePeriod = Period.builder().periodId("202405").build()
    private val sampleOrgunit = OrganisationUnit.builder().uid(uidGenerator.generate()).build()

    private val expressionService = ExpressionService(
        dataElementStore,
        categoryOptionComboStore,
        organisationUnitGroupStore,
        programStageStore,
    )
    private val executor = ValidationExecutor(expressionService)

    @Test
    fun should_return_null_if_invalid_expression() {
        val validationRule = ValidationRule.builder()
            .uid(uidGenerator.generate())
            .operator(ValidationRuleOperator.less_than_or_equal_to)
            .leftSide(
                ValidationRuleExpression.builder()
                    .expression("5")
                    .missingValueStrategy(MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING)
                    .description("Valid expression")
                    .build(),
            )
            .rightSide(
                ValidationRuleExpression.builder()
                    .expression("AVG((#{eY5ehpbEsB7})*1.5)")
                    .missingValueStrategy(MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING)
                    .description("Invalid expression")
                    .build(),
            )
            .importance(ValidationRuleImportance.HIGH)
            .periodType(PeriodType.Daily)
            .skipFormValidation(false)
            .organisationUnitLevels(emptyList())
            .build()

        val result = executor.evaluateRule(
            rule = validationRule,
            organisationUnit = sampleOrgunit,
            context = ExpressionServiceContext(),
            period = samplePeriod,
            attributeOptionComboId = uidGenerator.generate(),
        )

        assertThat(result).isNull()
    }
}
