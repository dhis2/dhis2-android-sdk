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
package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.internal.ProgramAttributeChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramFields
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityTypeChildrenAppender
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class ProgramCollectionRepository internal constructor(
    store: ProgramStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<Program, ProgramCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byVersion(): IntegerFilterConnector<ProgramCollectionRepository> {
        return cf.integer(ProgramTableInfo.Columns.VERSION)
    }

    fun byOnlyEnrollOnce(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.ONLY_ENROLL_ONCE)
    }

    fun byEnrollmentDateLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.ENROLLMENT_DATE_LABEL)
    }

    fun byDisplayIncidentDate(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.DISPLAY_INCIDENT_DATE)
    }

    fun byIncidentDateLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.INCIDENT_DATE_LABEL)
    }

    fun byRegistration(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.REGISTRATION)
    }

    fun bySelectEnrollmentDatesInFuture(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE)
    }

    fun byDataEntryMethod(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.DATA_ENTRY_METHOD)
    }

    fun byIgnoreOverdueEvents(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.IGNORE_OVERDUE_EVENTS)
    }

    fun bySelectIncidentDatesInFuture(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.SELECT_INCIDENT_DATES_IN_FUTURE)
    }

    fun byUseFirstStageDuringRegistration(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.USE_FIRST_STAGE_DURING_REGISTRATION)
    }

    fun byDisplayFrontPageList(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.DISPLAY_FRONT_PAGE_LIST)
    }

    fun byProgramType(): EnumFilterConnector<ProgramCollectionRepository, ProgramType> {
        return cf.enumC(ProgramTableInfo.Columns.PROGRAM_TYPE)
    }

    fun byRelatedProgramUid(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.RELATED_PROGRAM)
    }

    fun byTrackedEntityTypeUid(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.TRACKED_ENTITY_TYPE)
    }

    fun byCategoryComboUid(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.CATEGORY_COMBO)
    }

    fun byAccessDataWrite(): BooleanFilterConnector<ProgramCollectionRepository> {
        return cf.bool(ProgramTableInfo.Columns.ACCESS_DATA_WRITE)
    }

    fun byExpiryDays(): IntegerFilterConnector<ProgramCollectionRepository> {
        return cf.integer(ProgramTableInfo.Columns.EXPIRY_DAYS)
    }

    fun byCompleteEventsExpiryDays(): IntegerFilterConnector<ProgramCollectionRepository> {
        return cf.integer(ProgramTableInfo.Columns.COMPLETE_EVENTS_EXPIRY_DAYS)
    }

    fun byExpiryPeriodType(): EnumFilterConnector<ProgramCollectionRepository, PeriodType> {
        return cf.enumC(ProgramTableInfo.Columns.EXPIRY_PERIOD_TYPE)
    }

    fun byMinAttributesRequiredToSearch(): IntegerFilterConnector<ProgramCollectionRepository> {
        return cf.integer(ProgramTableInfo.Columns.MIN_ATTRIBUTES_REQUIRED_TO_SEARCH)
    }

    fun byMaxTeiCountToReturn(): IntegerFilterConnector<ProgramCollectionRepository> {
        return cf.integer(ProgramTableInfo.Columns.MAX_TEI_COUNT_TO_RETURN)
    }

    fun byFeatureType(): EnumFilterConnector<ProgramCollectionRepository, FeatureType> {
        return cf.enumC(ProgramTableInfo.Columns.FEATURE_TYPE)
    }

    fun byAccessLevel(): EnumFilterConnector<ProgramCollectionRepository, AccessLevel> {
        return cf.enumC(ProgramTableInfo.Columns.ACCESS_LEVEL)
    }

    fun byEnrollmentLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.ENROLLMENT_LABEL)
    }

    fun byFollowUpLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.FOLLOW_UP_LABEL)
    }

    fun byOrgUnitLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.ORG_UNIT_LABEL)
    }

    fun byRelationshipLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.RELATIONSHIP_LABEL)
    }

    fun byNoteLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.NOTE_LABEL)
    }

    fun byTrackedEntityAttributeLabel(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE_LABEL)
    }

    fun byColor(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<ProgramCollectionRepository> {
        return cf.string(ProgramTableInfo.Columns.ICON)
    }

    fun byOrganisationUnitUid(uid: String): ProgramCollectionRepository {
        return byOrganisationUnitList(listOf(uid))
    }

    fun byOrganisationUnitList(uids: List<String>): ProgramCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
            OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
            OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
            uids,
        )
    }

    fun byOrganisationUnitScope(scope: OrganisationUnit.Scope): ProgramCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inTwoLinkTable(
            OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
            OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
            OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
            listOf(scope.name),
        )
    }

    fun withTrackedEntityType(): ProgramCollectionRepository {
        return cf.withChild(ProgramTableInfo.Columns.TRACKED_ENTITY_TYPE)
    }

    fun withAttributes(): ProgramCollectionRepository {
        return cf.withChild(ProgramFields.ATTRIBUTE_VALUES)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<Program> = mapOf(
            ProgramTableInfo.Columns.TRACKED_ENTITY_TYPE to ::ProgramTrackedEntityTypeChildrenAppender,
            ProgramFields.ATTRIBUTE_VALUES to ProgramAttributeChildrenAppender::create,
        )
    }
}
