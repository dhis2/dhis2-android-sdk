/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.sdk.core.persistence.triggers;

import com.raizlabs.android.dbflow.sql.language.Insert;
import com.raizlabs.android.dbflow.sql.trigger.CompletedTrigger;
import com.raizlabs.android.dbflow.sql.trigger.Trigger;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.State$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.State$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.android.sdk.models.common.state.Action;

public final class TriggersManager {
    private static TriggersManager manager;

    private CompletedTrigger<Dashboard$Flow> insertDashboardState;
    private CompletedTrigger<DashboardItem$Flow> insertDashboardItemState;
    private CompletedTrigger<DashboardElement$Flow> insertDashboardElementState;

    private TriggersManager() {
    }

    private static TriggersManager getInstance() {
        if (manager == null) {
            manager = new TriggersManager();
        }

        return manager;
    }

    public static <T extends Model & IdentifiableObject> CompletedTrigger<T> createInsertTrigger(Class<T> clazz) {
        return Trigger.create("DashboardStateInsertTrigger")
                .after().insert(clazz)
                .begin(createInsertStateQuery(clazz));
    }

    /* public static <T extends Model & IdentifiableObject> CompletedTrigger<T> createDeleteTrigger(Class<T> clazz) {
        return Trigger.create("DashboardStateDeleteTrigger")
                .after().delete(clazz)
                .begin(createDeleteStateQuery(clazz));
    } */

    public void enable() {
        insertDashboardState.enable();
    }

    public void disable() {

    }

    private static <T extends Model & IdentifiableObject> Insert createInsertStateQuery(Class<T> clazz) {
        return Insert.into(State$Flow.class)
                .columns(State$Flow$Table.ITEMID, State$Flow$Table.ITEMTYPE, State$Flow$Table.ACTION)
                .values("new.id", State$Flow.getItemType(clazz), Action.SYNCED.toString());
    }

    /* private static <T extends Model & IdentifiableObject> Delete createDeleteStateQuery(Class<T> clazz) {
        Delete.table(clazz,
                Condition.column(State$Flow$Table.ITEMID).is("old.id"),
                Condition.column(State$Flow$Table.ITEMTYPE).is(State$Flow.getItemType(clazz)));
        return new Delete()
                .from(clazz)
                .where()
                .and()
                .();
    } */
}
