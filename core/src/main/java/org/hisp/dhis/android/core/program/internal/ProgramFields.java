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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramType;

public final class ProgramFields {

    public static final String VERSION = "version";
    public static final String ONLY_ENROLL_ONCE = "onlyEnrollOnce";
    public static final String ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
    public static final String DISPLAY_INCIDENT_DATE = "displayIncidentDate";
    public static final String INCIDENT_DATE_LABEL = "incidentDateLabel";
    public static final String REGISTRATION = "registration";
    public static final String SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
    public static final String DATA_ENTRY_METHOD = "dataEntryMethod";
    public static final String IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
    public static final String RELATIONSHIP_FROM_A = "relationshipFromA";
    public static final String SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
    public static final String CAPTURE_COORDINATES = "captureCoordinates";
    public static final String USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
    public static final String DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
    public static final String PROGRAM_TYPE = "programType";
    public static final String RELATIONSHIP_TYPE = "relationshipType";
    public static final String RELATIONSHIP_TEXT = "relationshipText";
    public static final String PROGRAM_TRACKED_ENTITY_ATTRIBUTES = "programTrackedEntityAttributes";
    public static final String RELATED_PROGRAM = "relatedProgram";
    public static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
    public static final String CATEGORY_COMBO = "categoryCombo";
    public static final String PROGRAM_INDICATORS = "programIndicators";
    public static final String PROGRAM_STAGES = "programStages";
    public static final String PROGRAM_RULES = "programRules";
    public static final String PROGRAM_RULE_VARIABLES = "programRuleVariables";
    public static final String ACCESS = "access";
    public static final String STYLE = "style";
    public static final String EXPIRY_DAYS = "expiryDays";
    public static final String COMPLETE_EVENTS_EXPIRY_DAYS = "completeEventsExpiryDays";
    public static final String EXPIRY_PERIOD_TYPE = "expiryPeriodType";
    public static final String MIN_ATTRIBUTES_REQUIRED_TO_SEARCH = "minAttributesRequiredToSearch";
    public static final String MAX_TEI_COUNT_TO_RETURN = "maxTeiCountToReturn";
    public static final String PROGRAM_SECTIONS = "programSections";
    public static final String FEATURE_TYPE = "featureType";

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
                    fh.<FeatureType>field(FEATURE_TYPE),
                    fh.<ProgramSection>nestedField(PROGRAM_SECTIONS).with(ProgramSectionFields.allFields)
            ).build();

    private ProgramFields() {
    }
}