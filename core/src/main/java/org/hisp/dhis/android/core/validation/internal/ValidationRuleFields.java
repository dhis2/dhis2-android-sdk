/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.validation.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.validation.ValidationRule;
import org.hisp.dhis.android.core.validation.ValidationRuleExpression;
import org.hisp.dhis.android.core.validation.ValidationRuleImportance;
import org.hisp.dhis.android.core.validation.ValidationRuleOperator;

import static org.hisp.dhis.android.core.validation.ValidationRuleTableInfo.Columns;

public final class ValidationRuleFields {
    private static final String LEFT_SIDE = "leftSide";
    private static final String RIGHT_SIDE = "rightSide";
    private static final String ORGANISATION_UNIT_LEVELS = "organisationUnitLevels";

    private static final FieldsHelper<ValidationRule> fh = new FieldsHelper<>();

    public static final Field<ValidationRule, String> uid = fh.uid();

    public static final Fields<ValidationRule> allFields = Fields.<ValidationRule>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<String>field(Columns.INSTRUCTION),
                    fh.<ValidationRuleImportance>field(Columns.IMPORTANCE),
                    fh.<ValidationRuleOperator>field(Columns.OPERATOR),
                    fh.<PeriodType>field(Columns.PERIOD_TYPE),
                    fh.<Boolean>field(Columns.SKIP_FORM_VALIDATION),
                    fh.<ValidationRuleExpression>nestedField(LEFT_SIDE).with(ValidationRuleExpressionFields.allFields),
                    fh.<ValidationRuleExpression>nestedField(RIGHT_SIDE).with(ValidationRuleExpressionFields.allFields),
                    fh.<ObjectWithUid>nestedField(ORGANISATION_UNIT_LEVELS).with(ObjectWithUid.uid)
            ).build();

    private ValidationRuleFields() {
    }
}