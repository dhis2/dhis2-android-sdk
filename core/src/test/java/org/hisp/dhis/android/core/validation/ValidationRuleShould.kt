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

package org.hisp.dhis.android.core.validation;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class ValidationRuleShould extends BaseObjectShould implements ObjectShould {

    public ValidationRuleShould() {
        super("validation/validation_rule.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ValidationRule validationRule = objectMapper.readValue(jsonStream, ValidationRule.class);

        assertThat(validationRule.code()).isEqualTo("Malaria outbreak");
        assertThat(validationRule.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2017-05-29T16:43:31.137"));
        assertThat(validationRule.uid()).isEqualTo("kgh54Xb9LSE");
        assertThat(validationRule.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2017-01-26T19:16:58.712"));
        assertThat(validationRule.name()).isEqualTo("Malaria outbreak");
        assertThat(validationRule.description()).isEqualTo("Malaria outbreak");
        assertThat(validationRule.deleted()).isNull();

        assertThat(validationRule.instruction()).isEqualTo("Instruction");
        assertThat(validationRule.importance()).isEqualTo(ValidationRuleImportance.MEDIUM);
        assertThat(validationRule.operator()).isEqualTo(ValidationRuleOperator.greater_than);
        assertThat(validationRule.periodType()).isEqualTo(PeriodType.Monthly);
        assertThat(validationRule.skipFormValidation()).isFalse();
        assertThat(validationRule.leftSide().expression()).isEqualTo("10");
        assertThat(validationRule.leftSideExpression()).isEqualTo("10");
        assertThat(validationRule.leftSide().description()).isEqualTo("Malaria threshold");
        assertThat(validationRule.leftSide().missingValueStrategy()).isEqualTo(MissingValueStrategy.NEVER_SKIP);
        assertThat(validationRule.rightSide().expression()).isEqualTo("I{nFICjJluo74}");
        assertThat(validationRule.rightSide().description()).isEqualTo("Malaria case count");
        assertThat(validationRule.rightSide().missingValueStrategy()).isEqualTo(MissingValueStrategy.NEVER_SKIP);
        assertThat(validationRule.organisationUnitLevels() == null).isFalse();
        assertThat(validationRule.organisationUnitLevels().get(0)).isEqualTo(2);
        assertThat(validationRule.organisationUnitLevels().get(1)).isEqualTo(3);
        assertThat(validationRule.organisationUnitLevels().get(2)).isEqualTo(4);
    }
}