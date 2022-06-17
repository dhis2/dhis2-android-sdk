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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableWithStyleStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo;

import androidx.annotation.NonNull;

public final class TrackedEntityAttributeStore {

    private TrackedEntityAttributeStore() {}

    private static StatementBinder<TrackedEntityAttribute> BINDER =
            new NameableWithStyleStatementBinder<TrackedEntityAttribute>() {

        @Override
        public void bindToStatement(@NonNull TrackedEntityAttribute o, @NonNull StatementWrapper w) {
            super.bindToStatement(o, w);
            w.bind(13, o.pattern());
            w.bind(14, o.sortOrderInListNoProgram());
            w.bind(15, UidsHelper.getUidOrNull(o.optionSet()));
            w.bind(16, o.valueType());
            w.bind(17, o.expression());
            w.bind(18, o.programScope());
            w.bind(19, o.displayInListNoProgram());
            w.bind(20, o.generated());
            w.bind(21, o.displayOnVisitSchedule());
            w.bind(22, o.orgUnitScope());
            w.bind(23, o.unique());
            w.bind(24, o.inherit());
            w.bind(25, o.formName());
            w.bind(26, o.displayFormName());
            w.bind(27, o.fieldMask());
            w.bind(28, o.aggregationType());
        }
    };

    public static IdentifiableObjectStore<TrackedEntityAttribute> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter,
                TrackedEntityAttributeTableInfo.TABLE_INFO, BINDER, TrackedEntityAttribute::create);
    }
}