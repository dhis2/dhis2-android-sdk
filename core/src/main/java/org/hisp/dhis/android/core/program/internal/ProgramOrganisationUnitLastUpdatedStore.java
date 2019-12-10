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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;

final class ProgramOrganisationUnitLastUpdatedStore
        extends ObjectWithoutUidStoreImpl<ProgramOrganisationUnitLastUpdated> {

    private static final StatementBinder<ProgramOrganisationUnitLastUpdated> BINDER = (o, w) -> {
                w.bind(1, o.program());
                w.bind(2, o.organisationUnit());
                w.bind(3, o.lastSynced());
            };

    private static final WhereStatementBinder<ProgramOrganisationUnitLastUpdated>
            WHERE_UPDATE_BINDER = (o, w) -> {
        w.bind(4, o.program());
        w.bind(5, o.organisationUnit());
    };

    private static final WhereStatementBinder<ProgramOrganisationUnitLastUpdated>
            WHERE_DELETE_BINDER = (o, w) -> {
        w.bind(1, o.program());
        w.bind(2, o.organisationUnit());
    };

    private ProgramOrganisationUnitLastUpdatedStore(DatabaseAdapter databaseAdapter,
                                                    SQLStatementBuilderImpl builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER,
                ProgramOrganisationUnitLastUpdated::create);
    }

    static ProgramOrganisationUnitLastUpdatedStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(
                ProgramOrganisationUnitLastUpdatedTableInfo.TABLE_INFO.name(),
                ProgramOrganisationUnitLastUpdatedTableInfo.TABLE_INFO.columns());

        return new ProgramOrganisationUnitLastUpdatedStore(databaseAdapter, statementBuilder);
    }
}