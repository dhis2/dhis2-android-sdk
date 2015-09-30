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
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardElement$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItem$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Enrollment$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Enrollment$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Event$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Event$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.State$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.State$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.TrackedEntityInstance$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.IModel;
import org.hisp.dhis.android.sdk.models.common.IdentifiableObject;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;
import org.hisp.dhis.android.sdk.models.state.State;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class StateStore implements IStateStore {


    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(State object) {
        isNull(object, "State object must not be null");

        State$Flow stateFlow = State$Flow.fromModel(object);

        if (stateFlow != null) {
            stateFlow.insert();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(State object) {
        isNull(object, "State object must not be null");

        State$Flow stateFlow = State$Flow.fromModel(object);

        if (stateFlow != null) {
            stateFlow.update();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(State state) {
        isNull(state, "State object must not be null");

        State$Flow stateFlow = State$Flow.fromModel(state);

        if (stateFlow != null) {
            stateFlow.save();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(State object) {
        isNull(object, "State object must not be null");

        State$Flow stateFlow = State$Flow.fromModel(object);

        if (stateFlow != null) {
            stateFlow.delete();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<State> query() {
        List<State$Flow> statesFlows = new Select()
                .from(State$Flow.class)
                .queryList();
        return State$Flow.toModels(statesFlows);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> void insert(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        insert(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> void update(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        update(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> void save(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setAction(action);
        state.setItemType(object.getClass());

        save(state);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> void delete(T object) {
        isNull(object, "State object must not be null");

        State state = query(object);

        if (state == null) {
            return;
        }

        State$Flow state$Flow = State$Flow.fromModel(state);
        state$Flow.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> State query(T object) {
        isNull(object, "State object must not be null");

        State$Flow stateFlow = new Select()
                .from(State$Flow.class)
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(State$Flow.getItemType(object.getClass())))
                .and(Condition.column(State$Flow$Table
                        .ITEMID).is(object.getId()))
                .querySingle();

        return State$Flow.toModel(stateFlow);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> Action queryAction(T object) {
        isNull(object, "State object must not be null");

        State state = query(object);

        if (state == null) {
            return Action.SYNCED;
        }

        return state.getAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> List<T> filterByAction(Class<T> clazz, Action action) {
        return getObjectsByAction(clazz, action, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> List<T> queryWithAction(Class<T> clazz, Action action) {
        return getObjectsByAction(clazz, action, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> List<State> query(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        List<State$Flow> stateFlows = new Select()
                .from(State$Flow.class)
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(State$Flow.getItemType(clazz)))
                .queryList();
        return State$Flow.toModels(stateFlows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IdentifiableObject> Map<Long, Action> queryMap(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        List<State> states = query(clazz);
        Map<Long, Action> actionMap = new HashMap<>();

        if (states != null && !states.isEmpty()) {
            for (State state : states) {
                actionMap.put(state.getItemId(), state.getAction());
            }
        }

        return actionMap;
    }

    @SuppressWarnings("unchecked")
    private <T extends IdentifiableObject> List<T> getObjectsByAction(Class<T> clazz, Action action, boolean withAction) {
        /* Creating left join on State and destination table in order to perform filtering  */
        /* Joining tables based on mime type and then filtering resulting table by action */
        /* From<? extends Model> from = new Select()
                .from(State$Flow.getFlowClass(clazz))
                .join(State$Flow.class, Join.JoinType.LEFT)
                .on(Condition.column(State$Flow$Table.ITEMID)
                        .eq(State$Flow.getItemType(clazz))); */

        /* Join<?, ?> join = new Select()
                .from(State$Flow.getFlowClass(clazz))
                .join(State$Flow.class, Join.JoinType.LEFT); */

        /* Where<? extends Model> where;
        if (withAction) {
            where = from.where(Condition.column(State$Flow$Table
                    .ACTION).is(action.toString()));
        } else {
            where = from.where(Condition.column(State$Flow$Table
                    .ACTION).isNot(action.toString()));
        } */

        // List<? extends Model> objects = where.queryList();
        if (Dashboard.class.equals(clazz)) {
            List<Dashboard$Flow> dashboardFlows = (List<Dashboard$Flow>) queryModels(
                    clazz, action, withAction, Dashboard$Flow$Table.ID);
            return (List<T>) Dashboard$Flow.toModels(dashboardFlows);
        }

        if (DashboardItem.class.equals(clazz)) {
            List<DashboardItem$Flow> dashboardItemFlows = (List<DashboardItem$Flow>) queryModels(
                    clazz, action, withAction, DashboardItem$Flow$Table.ID);
            return (List<T>) DashboardItem$Flow.toModels(dashboardItemFlows);
        }

        if (DashboardElement.class.equals(clazz)) {
            List<DashboardElement$Flow> dashboardElementFlows = (List<DashboardElement$Flow>) queryModels(
                    clazz, action, withAction, DashboardElement$Flow$Table.ID);
            return (List<T>) DashboardElement$Flow.toModels(dashboardElementFlows);
        }

        if (Event.class.equals(clazz)) {
            List<Event$Flow> eventFlows = (List<Event$Flow>) queryModels(clazz, action, withAction, Event$Flow$Table.ID);
            return (List<T>) Event$Flow.toModels(eventFlows);
        }

        if (Enrollment.class.equals(clazz)) {
            List<Enrollment$Flow> enrollmentFlows = (List<Enrollment$Flow>) queryModels(clazz, action, withAction, Enrollment$Flow$Table.ID);
            return (List<T>) Enrollment$Flow.toModels(enrollmentFlows);
        }

        if (TrackedEntityInstance.class.equals(clazz)) {
            List<TrackedEntityInstance$Flow> trackedEntityInstanceFlows = (List<TrackedEntityInstance$Flow>) queryModels(clazz, action, withAction, TrackedEntityInstance$Flow$Table.ID);
            return (List<T>) TrackedEntityInstance$Flow.toModels(trackedEntityInstanceFlows);
        }

        return null;
    }

    private List<? extends Model> queryModels(Class<?> clazz, Action action,
                                              boolean withAction, String columnName) {
        From<? extends Model> from = new Select()
                .from(State$Flow.getFlowClass(clazz))
                .join(State$Flow.class, Join.JoinType.LEFT)
                .on(Condition.column(State$Flow$Table.ITEMID)
                        .eq(columnName));

        Where<? extends Model> where = from
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(State$Flow.getItemType(clazz)));
        if (withAction) {
            where = where.and(Condition.column(State$Flow$Table
                    .ACTION).is(action.toString()));
        } else {
            where = where.and(Condition.column(State$Flow$Table
                    .ACTION).isNot(action.toString()));
        }

        List<? extends Model> list = where.queryList();
        System.out.println("LIST: " + list.size());
        return list;
    }
}
