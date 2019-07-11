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

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.internal.ProgramFields;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<Program, ProgramCollectionRepository> {

    @Inject
    ProgramCollectionRepository(final ProgramStoreInterface store,
                                final Map<String, ChildrenAppender<Program>> childrenAppenders,
                                final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ProgramCollectionRepository(store, childrenAppenders, s)));
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byVersion() {
        return cf.integer(ProgramFields.VERSION);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byOnlyEnrollOnce() {
        return cf.bool(ProgramFields.ONLY_ENROLL_ONCE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byEnrollmentDateLabel() {
        return cf.string(ProgramFields.ENROLLMENT_DATE_LABEL);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byDisplayIncidentDate() {
        return cf.bool(ProgramFields.DISPLAY_INCIDENT_DATE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byIncidentDateLabel() {
        return cf.string(ProgramFields.INCIDENT_DATE_LABEL);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byRegistration() {
        return cf.bool(ProgramFields.REGISTRATION);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> bySelectEnrollmentDatesInFuture() {
        return cf.bool(ProgramFields.SELECT_ENROLLMENT_DATES_IN_FUTURE);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byDataEntryMethod() {
        return cf.bool(ProgramFields.DATA_ENTRY_METHOD);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byIgnoreOverdueEvents() {
        return cf.bool(ProgramFields.IGNORE_OVERDUE_EVENTS);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byRelationshipFromA() {
        return cf.bool(ProgramFields.RELATIONSHIP_FROM_A);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> bySelectIncidentDatesInFuture() {
        return cf.bool(ProgramFields.SELECT_INCIDENT_DATES_IN_FUTURE);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byCaptureCoordinates() {
        return cf.bool(ProgramFields.CAPTURE_COORDINATES);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byUseFirstStageDuringRegistration() {
        return cf.bool(ProgramFields.USE_FIRST_STAGE_DURING_REGISTRATION);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byDisplayFrontPageList() {
        return cf.bool(ProgramFields.DISPLAY_FRONT_PAGE_LIST);
    }

    public EnumFilterConnector<ProgramCollectionRepository, ProgramType> byProgramType() {
        return cf.enumC(ProgramFields.PROGRAM_TYPE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byRelationshipTypeUid() {
        return cf.string(ProgramFields.RELATIONSHIP_TYPE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byRelationshipText() {
        return cf.string(ProgramFields.RELATIONSHIP_TEXT);
    }

    public StringFilterConnector<ProgramCollectionRepository> byRelatedProgramUid() {
        return cf.string(ProgramFields.RELATED_PROGRAM);
    }

    public StringFilterConnector<ProgramCollectionRepository> byTrackedEntityTypeUid() {
        return cf.string(ProgramFields.TRACKED_ENTITY_TYPE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byCategoryComboUid() {
        return cf.string(ProgramFields.CATEGORY_COMBO);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byAccessDataWrite() {
        return cf.bool(ProgramTableInfo.Columns.ACCESS_DATA_WRITE);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byExpiryDays() {
        return cf.integer(ProgramFields.EXPIRY_DAYS);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byCompleteEventsExpiryDays() {
        return cf.integer(ProgramFields.COMPLETE_EVENTS_EXPIRY_DAYS);
    }

    public EnumFilterConnector<ProgramCollectionRepository, PeriodType> byExpiryPeriodType() {
        return cf.enumC(ProgramFields.EXPIRY_PERIOD_TYPE);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byMinAttributesRequiredToSearch() {
        return cf.integer(ProgramFields.MIN_ATTRIBUTES_REQUIRED_TO_SEARCH);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byMaxTeiCountToReturn() {
        return cf.integer(ProgramFields.MAX_TEI_COUNT_TO_RETURN);
    }

    public EnumFilterConnector<ProgramCollectionRepository, FeatureType> byFeatureType() {
        return cf.enumC(ProgramFields.FEATURE_TYPE);
    }

    public ProgramCollectionRepository byOrganisationUnitList(List<String> uids) {
        return cf.subQuery(BaseIdentifiableObjectModel.Columns.UID).inLinkTable(
                OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
                uids);
    }

    public ProgramCollectionRepository withStyle() {
        return cf.withChild(ProgramFields.STYLE);
    }

    public ProgramCollectionRepository withProgramStages() {
        return cf.withChild(ProgramFields.PROGRAM_STAGES);
    }

    public ProgramCollectionRepository withProgramRuleVariables() {
        return cf.withChild(ProgramFields.PROGRAM_RULE_VARIABLES);
    }

    public ProgramCollectionRepository withProgramIndicators() {
        return cf.withChild(ProgramFields.PROGRAM_INDICATORS);
    }

    public ProgramCollectionRepository withProgramRules() {
        return cf.withChild(ProgramFields.PROGRAM_RULES);
    }

    public ProgramCollectionRepository withProgramTrackedEntityAttributes() {
        return cf.withChild(ProgramFields.PROGRAM_TRACKED_ENTITY_ATTRIBUTES);
    }

    public ProgramCollectionRepository withProgramSections() {
        return cf.withChild(ProgramFields.PROGRAM_SECTIONS);
    }

    public ProgramCollectionRepository withCategoryCombo() {
        return cf.withChild(ProgramFields.CATEGORY_COMBO);
    }

    public ProgramCollectionRepository withRelatedProgram() {
        return cf.withChild(ProgramFields.RELATED_PROGRAM);
    }

    public ProgramCollectionRepository withTrackedEntityType() {
        return cf.withChild(ProgramFields.TRACKED_ENTITY_TYPE);
    }
}
