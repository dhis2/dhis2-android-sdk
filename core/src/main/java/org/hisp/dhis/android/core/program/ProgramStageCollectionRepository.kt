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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ValidationStrategy
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions")
class ProgramStageCollectionRepository @Inject internal constructor(
    store: ProgramStageStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramStage, ProgramStageCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramStageCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byDescription(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.DESCRIPTION)
    }

    fun byDisplayDescription(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.DISPLAY_DESCRIPTION)
    }

    fun byExecutionDateLabel(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.EXECUTION_DATE_LABEL)
    }

    fun byDueDateLabel(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.DUE_DATE_LABEL)
    }

    fun byAllowGenerateNextVisit(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.ALLOW_GENERATE_NEXT_VISIT)
    }

    fun byValidCompleteOnly(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.VALID_COMPLETE_ONLY)
    }

    fun byReportDateToUse(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.REPORT_DATE_TO_USE)
    }

    fun byOpenAfterEnrollment(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.OPEN_AFTER_ENROLLMENT)
    }

    fun byRepeatable(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.REPEATABLE)
    }

    fun byFeatureType(): EnumFilterConnector<ProgramStageCollectionRepository, FeatureType> {
        return cf.enumC(ProgramStageTableInfo.Columns.FEATURE_TYPE)
    }

    fun byFormType(): EnumFilterConnector<ProgramStageCollectionRepository, FormType> {
        return cf.enumC(ProgramStageTableInfo.Columns.FORM_TYPE)
    }

    fun byDisplayGenerateEventBox(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.DISPLAY_GENERATE_EVENT_BOX)
    }

    fun byGeneratedByEnrollmentDate(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.GENERATED_BY_ENROLMENT_DATE)
    }

    fun byAutoGenerateEvent(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.AUTO_GENERATE_EVENT)
    }

    fun bySortOrder(): IntegerFilterConnector<ProgramStageCollectionRepository> {
        return cf.integer(ProgramStageTableInfo.Columns.SORT_ORDER)
    }

    fun byHideDueDate(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.HIDE_DUE_DATE)
    }

    fun byBlockEntryForm(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.BLOCK_ENTRY_FORM)
    }

    fun byMinDaysFromStart(): IntegerFilterConnector<ProgramStageCollectionRepository> {
        return cf.integer(ProgramStageTableInfo.Columns.MIN_DAYS_FROM_START)
    }

    fun byStandardInterval(): IntegerFilterConnector<ProgramStageCollectionRepository> {
        return cf.integer(ProgramStageTableInfo.Columns.STANDARD_INTERVAL)
    }

    fun byEnableUserAssignment(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.ENABLE_USER_ASSIGNMENT)
    }

    fun byPeriodType(): EnumFilterConnector<ProgramStageCollectionRepository, PeriodType> {
        return cf.enumC(ProgramStageTableInfo.Columns.PERIOD_TYPE)
    }

    fun byProgramUid(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.PROGRAM)
    }

    fun byAccessDataWrite(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.ACCESS_DATA_WRITE)
    }

    fun byRemindCompleted(): BooleanFilterConnector<ProgramStageCollectionRepository> {
        return cf.bool(ProgramStageTableInfo.Columns.REMIND_COMPLETED)
    }

    fun byValidationStrategy(): EnumFilterConnector<ProgramStageCollectionRepository, ValidationStrategy> {
        return cf.enumC(ProgramStageTableInfo.Columns.VALIDATION_STRATEGY)
    }

    fun byColor(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<ProgramStageCollectionRepository> {
        return cf.string(ProgramStageTableInfo.Columns.ICON)
    }

    fun orderBySortOrder(direction: OrderByDirection?): ProgramStageCollectionRepository {
        return cf.withOrderBy(ProgramStageTableInfo.Columns.SORT_ORDER, direction)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<ProgramStage> = emptyMap()
    }
}
