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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.tableinfos.SingleParentChildProjection;
import org.hisp.dhis.android.core.common.AccessHelper;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class ProgramStageStore {

    private ProgramStageStore() {}

    private static StatementBinder<ProgramStage> BINDER = new IdentifiableStatementBinder<ProgramStage>() {

        @Override
        public void bindToStatement(@NonNull ProgramStage o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 7, o.description());
            sqLiteBind(sqLiteStatement, 8, o.displayDescription());
            sqLiteBind(sqLiteStatement, 9, o.executionDateLabel());
            sqLiteBind(sqLiteStatement, 10, o.allowGenerateNextVisit());
            sqLiteBind(sqLiteStatement, 11, o.validCompleteOnly());
            sqLiteBind(sqLiteStatement, 12, o.reportDateToUse());
            sqLiteBind(sqLiteStatement, 13, o.openAfterEnrollment());
            sqLiteBind(sqLiteStatement, 14, o.repeatable());
            sqLiteBind(sqLiteStatement, 15, o.captureCoordinates());
            sqLiteBind(sqLiteStatement, 16, o.formType().name());
            sqLiteBind(sqLiteStatement, 17, o.displayGenerateEventBox());
            sqLiteBind(sqLiteStatement, 18, o.generatedByEnrollmentDate());
            sqLiteBind(sqLiteStatement, 19, o.autoGenerateEvent());
            sqLiteBind(sqLiteStatement, 20, o.sortOrder());
            sqLiteBind(sqLiteStatement, 21, o.hideDueDate());
            sqLiteBind(sqLiteStatement, 22, o.blockEntryForm());
            sqLiteBind(sqLiteStatement, 23, o.minDaysFromStart());
            sqLiteBind(sqLiteStatement, 24, o.standardInterval());
            sqLiteBind(sqLiteStatement, 25, UidsHelper.getUidOrNull(o.program()));
            sqLiteBind(sqLiteStatement, 26, o.periodType());
            sqLiteBind(sqLiteStatement, 27, AccessHelper.getAccessDataWrite(o.access()));
            sqLiteBind(sqLiteStatement, 28, o.remindCompleted());
            sqLiteBind(sqLiteStatement, 29, o.featureType());
        }
    };


    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            ProgramStageTableInfo.TABLE_INFO, ProgramStageFields.PROGRAM);

    public static IdentifiableObjectStore<ProgramStage> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, ProgramStageTableInfo.TABLE_INFO,
                BINDER, ProgramStage::create);
    }
}