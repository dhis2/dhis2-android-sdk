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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.attribute.internal.AttributeValuesFields;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.internal.AccessFields;
import org.hisp.dhis.android.core.common.internal.DataAccessFields;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.AccessLevel;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramTableInfo.Columns;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramType;

public final class ProgramFields {
    public static final String PROGRAM_TRACKED_ENTITY_ATTRIBUTES = "programTrackedEntityAttributes";
    private static final String CAPTURE_COORDINATES = "captureCoordinates";
    public static final String PROGRAM_RULE_VARIABLES = "programRuleVariables";
    private static final String ACCESS = "access";
    private static final String STYLE = "style";
    public static final String PROGRAM_SECTIONS = "programSections";
    private static final String ATTRIBUTE_VALUES = "attributeValues";

    private static FieldsHelper<Program> fh = new FieldsHelper<>();

    public static final Field<Program, String> uid = fh.uid();

    static final Fields<Program> allFields = Fields.<Program>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<Integer>field(Columns.VERSION),
                    fh.<Boolean>field(Columns.ONLY_ENROLL_ONCE),
                    fh.<String>field(Columns.ENROLLMENT_DATE_LABEL),
                    fh.<Boolean>field(Columns.DISPLAY_INCIDENT_DATE),
                    fh.<String>field(Columns.INCIDENT_DATE_LABEL),
                    fh.<Boolean>field(Columns.REGISTRATION),
                    fh.<Boolean>field(Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE),
                    fh.<Boolean>field(Columns.DATA_ENTRY_METHOD),
                    fh.<Boolean>field(Columns.IGNORE_OVERDUE_EVENTS),
                    fh.<Boolean>field(Columns.SELECT_INCIDENT_DATES_IN_FUTURE),
                    fh.<Boolean>field(CAPTURE_COORDINATES),
                    fh.<Boolean>field(Columns.USE_FIRST_STAGE_DURING_REGISTRATION),
                    fh.<Boolean>field(Columns.DISPLAY_FRONT_PAGE_LIST),
                    fh.<ProgramType>field(Columns.PROGRAM_TYPE),
                    fh.<ProgramTrackedEntityAttribute>nestedField(PROGRAM_TRACKED_ENTITY_ATTRIBUTES).with(
                            ProgramTrackedEntityAttributeFields.allFields),
                    fh.nestedFieldWithUid(Columns.RELATED_PROGRAM),
                    fh.nestedFieldWithUid(Columns.TRACKED_ENTITY_TYPE),
                    fh.nestedFieldWithUid(Columns.CATEGORY_COMBO),
                    fh.<Access>nestedField(ACCESS).with(AccessFields.data.with(DataAccessFields.write)),
                    fh.<ProgramRuleVariable>nestedField(PROGRAM_RULE_VARIABLES)
                            .with(ProgramRuleVariableFields.allFields),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields),
                    fh.<Integer>field(Columns.EXPIRY_DAYS),
                    fh.<Integer>field(Columns.COMPLETE_EVENTS_EXPIRY_DAYS),
                    fh.<PeriodType>field(Columns.EXPIRY_PERIOD_TYPE),
                    fh.<Integer>field(Columns.MIN_ATTRIBUTES_REQUIRED_TO_SEARCH),
                    fh.<Integer>field(Columns.MAX_TEI_COUNT_TO_RETURN),
                    fh.<FeatureType>field(Columns.FEATURE_TYPE),
                    fh.<AccessLevel>field(Columns.ACCESS_LEVEL),
                    fh.<ProgramSection>nestedField(PROGRAM_SECTIONS).with(ProgramSectionFields.allFields),
                    fh.<AttributeValue>nestedField(ATTRIBUTE_VALUES).with(AttributeValuesFields.allFields)

            ).build();

    private ProgramFields() {
    }
}