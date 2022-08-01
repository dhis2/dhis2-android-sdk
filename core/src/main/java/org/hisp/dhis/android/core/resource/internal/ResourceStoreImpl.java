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
package org.hisp.dhis.android.core.resource.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.util.List;

final class ResourceStoreImpl extends ObjectWithoutUidStoreImpl<Resource> implements ResourceStore {

    private static final StatementBinder<Resource> BINDER = (resource, w) -> {
        w.bind(1, resource.resourceType());
        w.bind(2, resource.lastSynced());
    };

    private static final WhereStatementBinder<Resource> WHERE_UPDATE_BINDER
            = (resource, w) -> w.bind(3, resource.resourceType());

    private static final WhereStatementBinder<Resource> WHERE_DELETE_BINDER
            = (resource, w) -> w.bind(1, resource.resourceType());

    private ResourceStoreImpl(DatabaseAdapter databaseAdapter,
                             SQLStatementBuilderImpl builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, Resource::create);
    }

    @Override
    public String getLastUpdated(Resource.Type type) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(ResourceTableInfo.Columns.RESOURCE_TYPE, type.name()).build();

        List<Resource> resourceList = selectWhere(whereClause);
        return resourceList.isEmpty() ?
                null : BaseIdentifiableObject.DATE_FORMAT.format(resourceList.get(0).lastSynced());
    }

    @Override
    public boolean deleteResource(Resource.Type type) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(ResourceTableInfo.Columns.RESOURCE_TYPE, type.name()).build();

        return deleteWhere(whereClause);
    }

    static ResourceStore create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(ResourceTableInfo.TABLE_INFO.name(),
                ResourceTableInfo.TABLE_INFO.columns());

        return new ResourceStoreImpl(databaseAdapter, statementBuilder);
    }
}