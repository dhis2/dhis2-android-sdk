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

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.binders.NameableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.AccessHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.IdentifiableObjectStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.SQLStatementWrapper;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class ProgramStore extends IdentifiableObjectStoreImpl<Program> implements ProgramStoreInterface {

    private ProgramStore(DatabaseAdapter databaseAdapter,
                         SQLStatementWrapper statementWrapper,
                         SQLStatementBuilder statementBuilder) {
        super(databaseAdapter, statementWrapper, statementBuilder, BINDER, Program::create);
    }
    
    private static StatementBinder<Program> BINDER = new NameableStatementBinder<Program>() {
        
        @Override
        public void bindToStatement(@NonNull Program o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 11, o.version());
            sqLiteBind(sqLiteStatement, 12, o.onlyEnrollOnce());
            sqLiteBind(sqLiteStatement, 13, o.enrollmentDateLabel());
            sqLiteBind(sqLiteStatement, 14, o.displayIncidentDate());
            sqLiteBind(sqLiteStatement, 15, o.incidentDateLabel());
            sqLiteBind(sqLiteStatement, 16, o.registration());
            sqLiteBind(sqLiteStatement, 17, o.selectEnrollmentDatesInFuture());
            sqLiteBind(sqLiteStatement, 18, o.dataEntryMethod());
            sqLiteBind(sqLiteStatement, 19, o.ignoreOverdueEvents());
            sqLiteBind(sqLiteStatement, 20, o.relationshipFromA());
            sqLiteBind(sqLiteStatement, 21, o.selectIncidentDatesInFuture());
            sqLiteBind(sqLiteStatement, 22, o.captureCoordinates());
            sqLiteBind(sqLiteStatement, 23, o.useFirstStageDuringRegistration());
            sqLiteBind(sqLiteStatement, 24, o.displayFrontPageList());
            sqLiteBind(sqLiteStatement, 25, o.programType());
            sqLiteBind(sqLiteStatement, 26, UidsHelper.getUidOrNull(o.relationshipType()));
            sqLiteBind(sqLiteStatement, 27, o.relationshipText());
            sqLiteBind(sqLiteStatement, 28, UidsHelper.getUidOrNull(o.relatedProgram()));
            sqLiteBind(sqLiteStatement, 29, UidsHelper.getUidOrNull(o.trackedEntityType()));
            sqLiteBind(sqLiteStatement, 30, o.categoryComboUid());
            sqLiteBind(sqLiteStatement, 31, AccessHelper.getAccessDataWrite(o.access()));
            sqLiteBind(sqLiteStatement, 32, o.expiryDays());
            sqLiteBind(sqLiteStatement, 33, o.completeEventsExpiryDays());
            sqLiteBind(sqLiteStatement, 34, o.expiryPeriodType());
            sqLiteBind(sqLiteStatement, 35, o.minAttributesRequiredToSearch());
            sqLiteBind(sqLiteStatement, 36, o.maxTeiCountToReturn());
            sqLiteBind(sqLiteStatement, 37, o.featureType());
        }
    };

    public static ProgramStoreInterface create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(ProgramTableInfo.TABLE_INFO.name(),
                ProgramTableInfo.TABLE_INFO.columns());
        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);

        return new ProgramStore(databaseAdapter, statementWrapper, statementBuilder);
    }

    @Override
    public List<String> queryWithoutRegistrationProgramUids() throws RuntimeException {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(ProgramFields.PROGRAM_TYPE, ProgramType.WITHOUT_REGISTRATION.toString())
                .build();
        return selectStringColumnsWhereClause(BaseIdentifiableObjectModel.Columns.UID, whereClause);
    }
}