/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.common;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.LongProperty;
import com.raizlabs.android.dbflow.sql.language.property.Property;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.BaseIdentifiableObjectFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.StateFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.StateFlow_Table;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.models.common.base.Model;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.common.state.State;
import org.hisp.dhis.client.sdk.models.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.core.common.utils.CollectionUtils.isEmpty;
import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class StateStoreImpl extends AbsStore<State, StateFlow> implements StateStore {
    private final Mapper<Event, EventFlow> eventMapper;

    public StateStoreImpl(Mapper<Event, EventFlow> eventMapper) {
        super(StateFlow.MAPPER);
        this.eventMapper = eventMapper;
    }

    @Override
    public <T extends Model> boolean insertActionForModel(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        return insert(state);
    }

    @Override
    public <T extends Model> boolean updateActionForModel(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        return update(state);
    }

    @Override
    public <T extends Model> boolean saveActionForModel(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setAction(action);
        state.setItemType(object.getClass());

        return save(state);
    }

    @Override
    public <T extends Model> boolean deleteActionForModel(T object) {
        isNull(object, "State object must not be null");

        State state = queryStateForModel(object);
        return state != null && delete(state);
    }

    @Override
    public <T extends Model> boolean deleteActionsForModelType(Class<T> modelType) {
        isNull(modelType, "model class must not be null");

        new Delete()
                .from(StateFlow.class)
                .where(StateFlow_Table
                        .itemType.is(getStateMapper().getRelatedModelClass(modelType)))
                .query();

        return true;
    }

    @Override
    public <T extends Model> State queryStateForModel(T object) {
        isNull(object, "State object must not be null");

        StateFlow stateFlow = new Select()
                .from(StateFlow.class)
                .where(StateFlow_Table
                        .itemType.is(getStateMapper().getRelatedModelClass(object.getClass())))
                .and(StateFlow_Table
                        .itemId.is(object.getId()))
                .querySingle();

        return getMapper().mapToModel(stateFlow);
    }

    @Override
    public <T extends Model> Action queryActionForModel(T object) {
        isNull(object, "State object must not be null");

        State state = queryStateForModel(object);
        if (state == null) {
            return Action.SYNCED;
        }

        return state.getAction();
    }

    @Override
    public <T extends Model> List<State> queryStatesForModelClass(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        List<StateFlow> stateFlows = new Select()
                .from(StateFlow.class)
                .where(StateFlow_Table
                        .itemType.is(getStateMapper().getRelatedModelClass(clazz)))
                .queryList();

        return getMapper().mapToModels(stateFlows);
    }

    @Override
    public <T extends Model> Map<Long, Action> queryActionsForModel(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        List<State> states = queryStatesForModelClass(clazz);
        Map<Long, Action> actionMap = new HashMap<>();

        if (states != null && !states.isEmpty()) {
            for (State state : states) {
                actionMap.put(state.getItemId(), state.getAction());
            }
        }

        return actionMap;
    }

    @Override
    public <T extends Model> List<T> queryModelsWithActions(Class<T> aClass, Action... actions) {
        return getObjectsByAction(aClass, null, true, actions);
    }

    @Override
    public <T extends Model> List<T> queryModelsWithActions(
            Class<T> clazz, Set<String> uids, Action... actions) {
        isEmpty(uids, "Set of uids must not be null");

        return getObjectsByAction(clazz, uids, true, actions);
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> List<T> getObjectsByAction(
            Class<T> clazz, Set<String> uids, boolean withAction, Action... actions) {

        if (Event.class.equals(clazz)) {
            List<EventFlow> eventFlows = (List<EventFlow>) queryModels(clazz, uids,
                    EventFlow_Table.id.withTable(), withAction, actions);
            return (List<T>) eventMapper.mapToModels(eventFlows);
        }

        return null;
    }

    private List<? extends com.raizlabs.android.dbflow.structure.Model> queryModels(
            Class<? extends Model> clazz, Set<String> uids, LongProperty column,
            boolean withAction, @Nullable Action... actions) {

        /* Creating left join on State and destination table in order to perform filtering  */
        /* Joining tables based on mime type and then filtering resulting table by action */
        From<? extends com.raizlabs.android.dbflow.structure.Model> from = new Select()
                .from(getStateMapper().getRelatedDatabaseEntityClass(clazz))
                .join(StateFlow.class, Join.JoinType.LEFT_OUTER)
                .on(StateFlow_Table.itemId.eq(column));

        Where<? extends com.raizlabs.android.dbflow.structure.Model> where = from.where(StateFlow_Table.itemType
                .is(getStateMapper().getRelatedModelClass(clazz)));

        if (uids != null && !uids.isEmpty()) {
            Property<String> uidColumn = new Property<>(getStateMapper()
                    .getRelatedDatabaseEntityClass(clazz), BaseIdentifiableObjectFlow.COLUMN_UID);
            where = where.and(uidColumn.in(uids));
        }

        if (actions != null) {
            for (int i = 0; i < actions.length; i++) {
                Action action = actions[i];
                if (i == 0) {
                    where = where.and(withAction ? StateFlow_Table.action.is(action) :
                            StateFlow_Table.action.isNot(action));
                } else {
                    where = where.or(withAction ? StateFlow_Table.action.is(action) :
                            StateFlow_Table.action.isNot(action));
                }
            }
        }

        System.out.println("QUERY STRING: " + where.toString());
        List<? extends com.raizlabs.android.dbflow.structure.Model> list = where.queryList();
        System.out.println("LIST: " + list.size());

        return list;
    }

    private StateMapper getStateMapper() {
        return (StateMapper) getMapper();
    }
}
