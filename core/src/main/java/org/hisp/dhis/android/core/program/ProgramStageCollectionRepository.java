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

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStageTableInfo.Columns;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramStageCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramStage, ProgramStageCollectionRepository> {

    @Inject
    ProgramStageCollectionRepository(final IdentifiableObjectStore<ProgramStage> store,
                                     final Map<String, ChildrenAppender<ProgramStage>> childrenAppenders,
                                     final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ProgramStageCollectionRepository(store, childrenAppenders, s)));
    }


    public StringFilterConnector<ProgramStageCollectionRepository> byDescription() {
        return cf.string(Columns.DESCRIPTION);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byDisplayDescription() {
        return cf.string(Columns.DISPLAY_DESCRIPTION);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byExecutionDateLabel() {
        return cf.string(Columns.EXECUTION_DATE_LABEL);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byDueDateLabel() {
        return cf.string(Columns.DUE_DATE_LABEL);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byAllowGenerateNextVisit() {
        return cf.bool(Columns.ALLOW_GENERATE_NEXT_VISIT);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byValidCompleteOnly() {
        return cf.bool(Columns.VALID_COMPLETE_ONLY);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byReportDateToUse() {
        return cf.string(Columns.REPORT_DATE_TO_USE);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byOpenAfterEnrollment() {
        return cf.bool(Columns.OPEN_AFTER_ENROLLMENT);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byRepeatable() {
        return cf.bool(Columns.REPEATABLE);
    }

    public EnumFilterConnector<ProgramStageCollectionRepository, FeatureType> byFeatureType() {
        return cf.enumC(Columns.FEATURE_TYPE);
    }

    public EnumFilterConnector<ProgramStageCollectionRepository, FormType> byFormType() {
        return cf.enumC(Columns.FORM_TYPE);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byDisplayGenerateEventBox() {
        return cf.bool(Columns.DISPLAY_GENERATE_EVENT_BOX);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byGeneratedByEnrollmentDate() {
        return cf.bool(Columns.GENERATED_BY_ENROLMENT_DATE);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byAutoGenerateEvent() {
        return cf.bool(Columns.AUTO_GENERATE_EVENT);
    }

    public IntegerFilterConnector<ProgramStageCollectionRepository> bySortOrder() {
        return cf.integer(Columns.SORT_ORDER);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byHideDueDate() {
        return cf.bool(Columns.HIDE_DUE_DATE);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byBlockEntryForm() {
        return cf.bool(Columns.BLOCK_ENTRY_FORM);
    }

    public IntegerFilterConnector<ProgramStageCollectionRepository> byMinDaysFromStart() {
        return cf.integer(Columns.MIN_DAYS_FROM_START);
    }

    public IntegerFilterConnector<ProgramStageCollectionRepository> byStandardInterval() {
        return cf.integer(Columns.STANDARD_INTERVAL);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byEnableUserAssignment() {
        return cf.bool(Columns.ENABLE_USER_ASSIGNMENT);
    }

    public EnumFilterConnector<ProgramStageCollectionRepository, PeriodType> byPeriodType() {
        return cf.enumC(Columns.PERIOD_TYPE);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byProgramUid() {
        return cf.string(Columns.PROGRAM);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byAccessDataWrite() {
        return cf.bool(ProgramStageTableInfo.Columns.ACCESS_DATA_WRITE);
    }

    public BooleanFilterConnector<ProgramStageCollectionRepository> byRemindCompleted() {
        return cf.bool(Columns.REMIND_COMPLETED);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byColor() {
        return cf.string(Columns.COLOR);
    }

    public StringFilterConnector<ProgramStageCollectionRepository> byIcon() {
        return cf.string(Columns.ICON);
    }

    public ProgramStageCollectionRepository orderBySortOrder(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.SORT_ORDER, direction);
    }
}