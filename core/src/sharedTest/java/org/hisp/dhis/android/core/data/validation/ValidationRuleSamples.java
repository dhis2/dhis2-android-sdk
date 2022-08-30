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

package org.hisp.dhis.android.core.data.validation;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.hisp.dhis.android.core.validation.ValidationRule;
import org.hisp.dhis.android.core.validation.ValidationRuleExpression;
import org.hisp.dhis.android.core.validation.ValidationRuleImportance;
import org.hisp.dhis.android.core.validation.ValidationRuleOperator;

import java.text.ParseException;
import java.util.Date;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;

public class ValidationRuleSamples {

    public static ValidationRule get() {
        ValidationRule.Builder builder = ValidationRule.builder();

        fillNameableProperties(builder);
        return builder
                .id(1L)
                .instruction("instruction")
                .importance(ValidationRuleImportance.HIGH)
                .operator(ValidationRuleOperator.compulsory_pair)
                .periodType(PeriodType.Daily)
                .skipFormValidation(Boolean.FALSE)
                .leftSide(ValidationRuleExpression.builder()
                        .description("left_description")
                        .expression("left_expression")
                        .missingValueStrategy(MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING)
                        .build())
                .rightSide(ValidationRuleExpression.builder()
                        .description("right_description")
                        .expression("right_expression")
                        .missingValueStrategy(MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING)
                        .build())
                .organisationUnitLevels(Lists.newArrayList(2, 3, 4))
                .deleted(false)
                .build();
    }

    private static Date getDate(String dateStr) {
        try {
            return BaseIdentifiableObject.DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}