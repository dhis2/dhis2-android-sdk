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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleFields;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.period.PeriodType;

final class ProgramFields {

    static final String VERSION = "version";
    static final String ONLY_ENROLL_ONCE = "onlyEnrollOnce";
    static final String ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
    static final String DISPLAY_INCIDENT_DATE = "displayIncidentDate";
    static final String INCIDENT_DATE_LABEL = "incidentDateLabel";
    static final String REGISTRATION = "registration";
    static final String SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
    static final String DATA_ENTRY_METHOD = "dataEntryMethod";
    static final String IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
    static final String RELATIONSHIP_FROM_A = "relationshipFromA";
    static final String SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
    static final String CAPTURE_COORDINATES = "captureCoordinates";
    static final String USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
    static final String DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
    static final String PROGRAM_TYPE = "programType";
    static final String RELATIONSHIP_TYPE = "relationshipType";
    static final String RELATIONSHIP_TEXT = "relationshipText";
    private static final String PROGRAM_TRACKED_ENTITY_ATTRIBUTES = "programTrackedEntityAttributes";
    static final String RELATED_PROGRAM = "relatedProgram";
    static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
    static final String CATEGORY_COMBO = "categoryCombo";
    private static final String PROGRAM_INDICATORS = "programIndicators";
    private static final String PROGRAM_STAGES = "programStages";
    private static final String PROGRAM_RULE_VARIABLES = "programRuleVariables";
    private static final String ACCESS = "access";
    private static final String STYLE = "style";
    static final String EXPIRY_DAYS = "expiryDays";
    static final String COMPLETE_EVENTS_EXPIRY_DAYS = "completeEventsExpiryDays";
    static final String EXPIRY_PERIOD_TYPE = "expiryPeriodType";
    static final String MIN_ATTRIBUTES_REQUIRED_TO_SEARCH = "minAttributesRequiredToSearch";
    static final String MAX_TEI_COUNT_TO_RETURN = "maxTeiCountToReturn";
    private static final String PROGRAM_SECTIONS = "programSections";

    private static FieldsHelper<Program> fh = new FieldsHelper<>();

    static final Fields<Program> allFields = Fields.<Program>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<Integer>field(VERSION),
                    fh.<Boolean>field(ONLY_ENROLL_ONCE),
                    fh.<String>field(ENROLLMENT_DATE_LABEL),
                    fh.<Boolean>field(DISPLAY_INCIDENT_DATE),
                    fh.<String>field(INCIDENT_DATE_LABEL),
                    fh.<Boolean>field(REGISTRATION),
                    fh.<Boolean>field(SELECT_ENROLLMENT_DATES_IN_FUTURE),
                    fh.<Boolean>field(DATA_ENTRY_METHOD),
                    fh.<Boolean>field(IGNORE_OVERDUE_EVENTS),
                    fh.<Boolean>field(RELATIONSHIP_FROM_A),
                    fh.<Boolean>field(SELECT_INCIDENT_DATES_IN_FUTURE),
                    fh.<Boolean>field(CAPTURE_COORDINATES),
                    fh.<Boolean>field(USE_FIRST_STAGE_DURING_REGISTRATION),
                    fh.<Boolean>field(DISPLAY_FRONT_PAGE_LIST),
                    fh.<ProgramType>field(PROGRAM_TYPE),
                    fh.nestedFieldWithUid(RELATIONSHIP_TYPE),
                    fh.<String>field(RELATIONSHIP_TEXT),
                    fh.<ProgramTrackedEntityAttribute>nestedField(PROGRAM_TRACKED_ENTITY_ATTRIBUTES).with(
                            ProgramTrackedEntityAttributeFields.allFields),
                    fh.nestedFieldWithUid(RELATED_PROGRAM),
                    fh.nestedFieldWithUid(TRACKED_ENTITY_TYPE),
                    fh.nestedFieldWithUid(CATEGORY_COMBO),
                    fh.<Access>nestedField(ACCESS).with(Access.data.with(DataAccess.write)),
                    fh.<ProgramIndicator>nestedField(PROGRAM_INDICATORS).with(ProgramIndicatorFields.allFields),
                    fh.nestedFieldWithUid(PROGRAM_STAGES),
                    fh.<ProgramRuleVariable>nestedField(PROGRAM_RULE_VARIABLES)
                            .with(ProgramRuleVariableFields.allFields),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields),
                    fh.<Integer>field(EXPIRY_DAYS),
                    fh.<Integer>field(COMPLETE_EVENTS_EXPIRY_DAYS),
                    fh.<PeriodType>field(EXPIRY_PERIOD_TYPE),
                    fh.<Integer>field(MIN_ATTRIBUTES_REQUIRED_TO_SEARCH),
                    fh.<Integer>field(MAX_TEI_COUNT_TO_RETURN),
                    fh.<ProgramSection>nestedField(PROGRAM_SECTIONS).with(ProgramSectionFields.allFields)
            ).build();

    private ProgramFields() {
    }
}