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
package org.hisp.dhis.android.core.arch.db.stores.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorExecutor;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.List;

class SingleParentChildStoreImpl<P extends ObjectWithUidInterface, C> implements SingleParentChildStore<P, C> {

    private final SingleParentChildProjection childProjection;

    private final DatabaseAdapter databaseAdapter;
    private final SQLStatementBuilder statementBuilder;

    private final CursorExecutor<C> cursorExecutor;

    SingleParentChildStoreImpl(SingleParentChildProjection childProjection,
                                      DatabaseAdapter databaseAdapter,
                                      SQLStatementBuilder statementBuilder,
                                      CursorExecutor<C> cursorExecutor) {
        this.childProjection = childProjection;
        this.databaseAdapter = databaseAdapter;
        this.statementBuilder = statementBuilder;
        this.cursorExecutor = cursorExecutor;
    }

    @Override
    public List<C> getChildren(P p) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(childProjection.parentColumn, p.uid())
                .build();
        String selectStatement = statementBuilder.selectWhere(whereClause);
        return cursorExecutor.getObjects(databaseAdapter.rawQuery(selectStatement));
    }
}
