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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramTableInfo.Columns;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;

import java.util.Collections;
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
        return cf.integer(Columns.VERSION);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byOnlyEnrollOnce() {
        return cf.bool(Columns.ONLY_ENROLL_ONCE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byEnrollmentDateLabel() {
        return cf.string(Columns.ENROLLMENT_DATE_LABEL);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byDisplayIncidentDate() {
        return cf.bool(Columns.DISPLAY_INCIDENT_DATE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byIncidentDateLabel() {
        return cf.string(Columns.INCIDENT_DATE_LABEL);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byRegistration() {
        return cf.bool(Columns.REGISTRATION);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> bySelectEnrollmentDatesInFuture() {
        return cf.bool(Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byDataEntryMethod() {
        return cf.bool(Columns.DATA_ENTRY_METHOD);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byIgnoreOverdueEvents() {
        return cf.bool(Columns.IGNORE_OVERDUE_EVENTS);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> bySelectIncidentDatesInFuture() {
        return cf.bool(Columns.SELECT_INCIDENT_DATES_IN_FUTURE);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byUseFirstStageDuringRegistration() {
        return cf.bool(Columns.USE_FIRST_STAGE_DURING_REGISTRATION);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byDisplayFrontPageList() {
        return cf.bool(Columns.DISPLAY_FRONT_PAGE_LIST);
    }

    public EnumFilterConnector<ProgramCollectionRepository, ProgramType> byProgramType() {
        return cf.enumC(Columns.PROGRAM_TYPE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byRelatedProgramUid() {
        return cf.string(Columns.RELATED_PROGRAM);
    }

    public StringFilterConnector<ProgramCollectionRepository> byTrackedEntityTypeUid() {
        return cf.string(Columns.TRACKED_ENTITY_TYPE);
    }

    public StringFilterConnector<ProgramCollectionRepository> byCategoryComboUid() {
        return cf.string(Columns.CATEGORY_COMBO);
    }

    public BooleanFilterConnector<ProgramCollectionRepository> byAccessDataWrite() {
        return cf.bool(ProgramTableInfo.Columns.ACCESS_DATA_WRITE);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byExpiryDays() {
        return cf.integer(Columns.EXPIRY_DAYS);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byCompleteEventsExpiryDays() {
        return cf.integer(Columns.COMPLETE_EVENTS_EXPIRY_DAYS);
    }

    public EnumFilterConnector<ProgramCollectionRepository, PeriodType> byExpiryPeriodType() {
        return cf.enumC(Columns.EXPIRY_PERIOD_TYPE);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byMinAttributesRequiredToSearch() {
        return cf.integer(Columns.MIN_ATTRIBUTES_REQUIRED_TO_SEARCH);
    }

    public IntegerFilterConnector<ProgramCollectionRepository> byMaxTeiCountToReturn() {
        return cf.integer(Columns.MAX_TEI_COUNT_TO_RETURN);
    }

    public EnumFilterConnector<ProgramCollectionRepository, FeatureType> byFeatureType() {
        return cf.enumC(Columns.FEATURE_TYPE);
    }

    public EnumFilterConnector<ProgramCollectionRepository, AccessLevel> byAccessLevel() {
        return cf.enumC(Columns.ACCESS_LEVEL);
    }

    public StringFilterConnector<ProgramCollectionRepository> byColor() {
        return cf.string(Columns.COLOR);
    }

    public StringFilterConnector<ProgramCollectionRepository> byIcon() {
        return cf.string(Columns.ICON);
    }

    public ProgramCollectionRepository byOrganisationUnitUid(String uid) {
        return byOrganisationUnitList(Collections.singletonList(uid));
    }

    public ProgramCollectionRepository byOrganisationUnitList(List<String> uids) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
                uids);
    }

    public ProgramCollectionRepository byOrganisationUnitScope(OrganisationUnit.Scope scope) {
        return cf.subQuery(IdentifiableColumns.UID).inTwoLinkTable(
                OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                Collections.singletonList(scope.name())
        );
    }

    public ProgramCollectionRepository withTrackedEntityType() {
        return cf.withChild(Columns.TRACKED_ENTITY_TYPE);
    }
}
