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

package org.hisp.dhis.android.sdk.core.persistence.models.state;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.State$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.State$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.IdentifiableObject;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;
import org.hisp.dhis.android.sdk.models.state.State;

import java.util.List;

public class StateStore implements IStateStore {

    @Override
    public <T extends IdentifiableObject> State query(T object) {
        if (object == null) {
            return null;
        }

        State$Flow stateFlow = new Select()
                .from(State$Flow.class)
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(State$Flow.getItemType(object.getClass())))
                .and(Condition.column(State$Flow$Table
                        .ITEMID).is(object.getId()))
                .querySingle();

        return State$Flow.toModel(stateFlow);
    }

    @Override
    public <T extends IdentifiableObject> List<State> query(Class<T> clazz) {
        List<State$Flow> stateFlows = new Select()
                .from(State$Flow.class)
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(State$Flow.getItemType(clazz)))
                .queryList();
        return State$Flow.toModels(stateFlows);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> List<T> filterByAction(Class<T> clazz, Action action) {
        /* Creating left join on State and destination table in order to perform filtering  */
        List<? extends Model> objects = new Select()
                .from(State$Flow.getFlowClass(clazz))
                .join(State$Flow.class, Join.JoinType.LEFT)
                .using(State$Flow$Table.ITEMTYPE)
                .where(Condition.column(State$Flow$Table
                        .ACTION).isNot(action.toString()))
                .queryList();

        if (Dashboard.class.equals(clazz)) {
            List<Dashboard$Flow> dashboardFlows = (List<Dashboard$Flow>) objects;
            return (List<T>) Dashboard$Flow.toModels(dashboardFlows);
        }

        if (DashboardItem.class.equals(clazz)) {
            List<DashboardItem$Flow> dashboardItemFlows = (List<DashboardItem$Flow>) objects;
            return (List<T>) DashboardItem$Flow.toModels(dashboardItemFlows);
        }

        if (DashboardElement.class.equals(clazz)) {
            List<DashboardElement$Flow> dashboardElementFlows = (List<DashboardElement$Flow>) objects;
            return (List<T>) DashboardElement$Flow.toModels(dashboardElementFlows);
        }

        return null;
    }

    @Override
    public <T extends IdentifiableObject> Action queryAction(T object) {
        State state = query(object);

        if (state == null) {
            return null;
        }

        return state.getAction();
    }

    @Override
    public <T extends IdentifiableObject> void delete(T object) {
        State state = query(object);

        if (state == null) {
            return;
        }

        State$Flow state$Flow = State$Flow.fromModel(state);
        state$Flow.delete();
    }

    @Override
    public <T extends IdentifiableObject> void save(T object, Action action) {
        if (object == null) {
            return;
        }

        State state = new State();
        state.setItemId(object.getId());
        state.setAction(action);
        state.setItemType(object.getClass());

        save(state);
    }

    @Override
    public void insert(State object) {
        State$Flow stateFlow = State$Flow.fromModel(object);

        if (stateFlow != null) {
            stateFlow.insert();
        }
    }

    @Override
    public void update(State object) {
        State$Flow stateFlow = State$Flow.fromModel(object);

        if (stateFlow != null) {
            stateFlow.update();
        }
    }

    @Override
    public void save(State state) {
        State$Flow stateFlow = State$Flow.fromModel(state);

        if (stateFlow != null) {
            stateFlow.save();
        }
    }

    @Override
    public void delete(State object) {
        State$Flow stateFlow = State$Flow.fromModel(object);

        if (stateFlow != null) {
            stateFlow.delete();
        }
    }

    @Override
    public List<State> query() {
        List<State$Flow> statesFlows = new Select()
                .from(State$Flow.class)
                .queryList();
        return State$Flow.toModels(statesFlows);
    }
}
