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

package org.hisp.dhis.android.core.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.LinkModelStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo.Columns;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class UserOrganisationUnitLinkStore extends LinkModelStoreImpl<UserOrganisationUnitLinkModel>
        implements UserOrganisationUnitLinkStoreInterface {

    private UserOrganisationUnitLinkStore(DatabaseAdapter databaseAdapter,
                                          SQLiteStatement insertStatement,
                                          String masterColumn,
                                          SQLStatementBuilder builder,
                                          StatementBinder<UserOrganisationUnitLinkModel> binder) {

        super(databaseAdapter, insertStatement, builder, masterColumn, binder, FACTORY);
    }

    private static final StatementBinder<UserOrganisationUnitLinkModel> BINDER
            = new StatementBinder<UserOrganisationUnitLinkModel>() {
        @Override
        public void bindToStatement(@NonNull UserOrganisationUnitLinkModel o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.user());
            sqLiteBind(sqLiteStatement, 2, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 3, o.organisationUnitScope());
            sqLiteBind(sqLiteStatement, 4, o.root());
        }
    };

    private static final CursorModelFactory<UserOrganisationUnitLinkModel> FACTORY
            = new CursorModelFactory<UserOrganisationUnitLinkModel>() {
        @Override
        public UserOrganisationUnitLinkModel fromCursor(Cursor cursor) {
            return UserOrganisationUnitLinkModel.create(cursor);
        }
    };

    public static UserOrganisationUnitLinkStoreInterface create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(UserOrganisationUnitLinkTableInfo.TABLE_INFO);

        return new UserOrganisationUnitLinkStore(
                databaseAdapter,
                databaseAdapter.compileStatement(statementBuilder.insert()),
                UserOrganisationUnitLinkTableInfo.Columns.USER,
                statementBuilder,
                BINDER);
    }

    @Override
    public List<String> queryRootCaptureOrganisationUnitUids() throws RuntimeException {
        return selectStringColumnsWhereClause(Columns.ORGANISATION_UNIT,
                        Columns.ROOT + " = 1 " + "AND "
                                + Columns.ORGANISATION_UNIT_SCOPE + " = '"
                                + OrganisationUnit.Scope.SCOPE_DATA_CAPTURE + "'");
    }
}