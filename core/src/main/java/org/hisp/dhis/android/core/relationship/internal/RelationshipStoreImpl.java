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

package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStoreImpl;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipTableInfo;

import java.util.ArrayList;
import java.util.List;

public final class RelationshipStoreImpl extends IdentifiableDeletableDataObjectStoreImpl<Relationship>
        implements RelationshipStore {

    private static StatementBinder<Relationship> BINDER = (o, w) -> {
        w.bind(1, o.uid());
        w.bind(2, o.name());
        w.bind(3, o.created());
        w.bind(4, o.lastUpdated());
        w.bind(5, o.relationshipType());
        w.bind(6, o.state());
        w.bind(7, o.deleted());
    };

    private RelationshipStoreImpl(DatabaseAdapter databaseAdapter,
                                  SQLStatementBuilderImpl statementBuilder) {
        super(databaseAdapter, statementBuilder, BINDER, Relationship::create);
    }

    @Override
    public List<Relationship> getRelationshipsByItem(RelationshipItem relationshipItem) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue("RelationshipItem." + relationshipItem.elementType(),
                        relationshipItem.elementUid())
                .build();

        String queryStatement =
                "SELECT DISTINCT Relationship.* " +
                "FROM (Relationship INNER JOIN RelationshipItem " +
                "ON Relationship.uid = RelationshipItem.relationship) " +
                "WHERE " + whereClause + ";";

        List<Relationship> relationships = new ArrayList<>();
        addObjectsToCollection(databaseAdapter.rawQuery(queryStatement), relationships);

        return relationships;
    }

    public static RelationshipStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(RelationshipTableInfo.TABLE_INFO);
        return new RelationshipStoreImpl(databaseAdapter, statementBuilder);
    }
}