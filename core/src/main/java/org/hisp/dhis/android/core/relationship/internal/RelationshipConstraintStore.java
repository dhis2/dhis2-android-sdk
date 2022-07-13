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

package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintTableInfo;

public final class RelationshipConstraintStore {

    private static final StatementBinder<RelationshipConstraint> BINDER = (o, w) -> {
        w.bind(1, UidsHelper.getUidOrNull(o.relationshipType()));
        w.bind(2, o.constraintType());
        w.bind(3, o.relationshipEntity());
        w.bind(4, UidsHelper.getUidOrNull(o.trackedEntityType()));
        w.bind(5, UidsHelper.getUidOrNull(o.program()));
        w.bind(6, UidsHelper.getUidOrNull(o.programStage()));
    };

    private static final WhereStatementBinder<RelationshipConstraint> WHERE_UPDATE_BINDER = (o, w) -> {
        w.bind(7, UidsHelper.getUidOrNull(o.relationshipType()));
        w.bind(8, o.constraintType());
    };

    private static final WhereStatementBinder<RelationshipConstraint> WHERE_DELETE_BINDER = (o, w) -> {
        w.bind(1, UidsHelper.getUidOrNull(o.relationshipType()));
        w.bind(2, o.constraintType());
    };

    private RelationshipConstraintStore() {}

    public static ObjectWithoutUidStore<RelationshipConstraint> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithoutUidStore(databaseAdapter, RelationshipConstraintTableInfo.TABLE_INFO,
                BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, RelationshipConstraint::create);
    }
}