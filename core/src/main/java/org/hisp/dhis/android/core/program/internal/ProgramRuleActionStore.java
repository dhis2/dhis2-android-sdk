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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionTableInfo;

import androidx.annotation.NonNull;

public final class ProgramRuleActionStore {

    private static StatementBinder<ProgramRuleAction> BINDER = new IdentifiableStatementBinder<ProgramRuleAction>() {
        @Override
        public void bindToStatement(@NonNull ProgramRuleAction o, @NonNull StatementWrapper w) {
            super.bindToStatement(o, w);
            w.bind(7, o.data());
            w.bind(8, o.content());
            w.bind(9, o.location());
            w.bind(10, UidsHelper.getUidOrNull(o.trackedEntityAttribute()));
            w.bind(11, UidsHelper.getUidOrNull(o.programIndicator()));
            w.bind(12, UidsHelper.getUidOrNull(o.programStageSection()));
            w.bind(13, o.programRuleActionType());
            w.bind(14, UidsHelper.getUidOrNull(o.programStage()));
            w.bind(15, UidsHelper.getUidOrNull(o.dataElement()));
            w.bind(16, UidsHelper.getUidOrNull(o.programRule()));
            w.bind(17, UidsHelper.getUidOrNull(o.option()));
            w.bind(18, UidsHelper.getUidOrNull(o.optionGroup()));
        }
    };

    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            ProgramRuleActionTableInfo.TABLE_INFO, ProgramRuleActionTableInfo.Columns.PROGRAM_RULE);

    private ProgramRuleActionStore() {}

    public static IdentifiableObjectStore<ProgramRuleAction> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, ProgramRuleActionTableInfo.TABLE_INFO, BINDER,
                ProgramRuleAction::create);
    }
}