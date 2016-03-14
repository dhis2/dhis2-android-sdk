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

package org.hisp.dhis.client.sdk.android.common.state;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.property.LongProperty;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.client.sdk.android.common.base.AbsStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.DashboardElementFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardElementFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.DashboardFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.DashboardItemFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardItemFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.EnrollmentFlow;
import org.hisp.dhis.client.sdk.android.flow.EnrollmentFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.InterpretationCommentFlow;
import org.hisp.dhis.client.sdk.android.flow.InterpretationCommentFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.InterpretationElementFlow;
import org.hisp.dhis.client.sdk.android.flow.InterpretationElementFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.InterpretationFlow;
import org.hisp.dhis.client.sdk.android.flow.InterpretationFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.StateFlow;
import org.hisp.dhis.client.sdk.android.flow.StateFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityInstanceFlow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityInstanceFlow_Table;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.models.common.base.IModel;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.common.state.State;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class StateStore extends AbsStore<State, StateFlow> implements IStateStore {
    private final IMapper<Dashboard, DashboardFlow> dashboardMapper;
    private final IMapper<DashboardItem, DashboardItemFlow> dashboardItemMapper;
    private final IMapper<DashboardElement, DashboardElementFlow> dashboardElementMapper;
    private final IMapper<Event, EventFlow> eventMapper;
    private final IMapper<Enrollment, EnrollmentFlow> enrollmentMapper;
    private final IMapper<TrackedEntityInstance, TrackedEntityInstanceFlow>
            trackedEntityInstanceMapper;

    public StateStore(IStateMapper mapper,
                      IMapper<Dashboard, DashboardFlow> dashboardMapper,
                      IMapper<DashboardItem, DashboardItemFlow> dashboardItemMapper,
                      IMapper<DashboardElement, DashboardElementFlow> dashboardElementMapper,
                      IMapper<Event, EventFlow> eventMapper,
                      IMapper<Enrollment, EnrollmentFlow> enrollmentMapper,
                      IMapper<TrackedEntityInstance, TrackedEntityInstanceFlow>
                              trackedEntityInstanceMapper) {
        super(mapper);
        this.dashboardMapper = dashboardMapper;
        this.dashboardItemMapper = dashboardItemMapper;
        this.dashboardElementMapper = dashboardElementMapper;
        this.eventMapper = eventMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.trackedEntityInstanceMapper = trackedEntityInstanceMapper;
    }

    @Override
    public <T extends IModel> boolean insertActionForModel(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        return insert(state);
    }

    @Override
    public <T extends IModel> boolean updateActionForModel(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        return update(state);
    }

    @Override
    public <T extends IModel> boolean saveActionForModel(T object, Action action) {
        isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setAction(action);
        state.setItemType(object.getClass());

        return save(state);
    }

    @Override
    public <T extends IModel> boolean deleteActionForModel(T object) {
        isNull(object, "State object must not be null");

        State state = queryStateForModel(object);
        return state != null && delete(state);
    }

    @Override
    public <T extends IModel> State queryStateForModel(T object) {
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
    public <T extends IModel> Action queryActionForModel(T object) {
        isNull(object, "State object must not be null");

        State state = queryStateForModel(object);
        if (state == null) {
            return Action.SYNCED;
        }

        return state.getAction();
    }

    @Override
    public <T extends IModel> List<State> queryStatesForModelClass(Class<T> clazz) {
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
    public <T extends IModel> Map<Long, Action> queryActionsForModel(Class<T> clazz) {
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
    public <T extends IModel> List<T> queryModelsWithActions(Class<T> aClass, Action... actions) {
        return getObjectsByAction(aClass, true, actions);
    }

    @SuppressWarnings("unchecked")
    private <T extends IModel> List<T> getObjectsByAction(Class<T> clazz, boolean withAction,
                                                          Action... actions) {
        if (Dashboard.class.equals(clazz)) {
            List<DashboardFlow> dashboardFlows = (List<DashboardFlow>) queryModels(
                    clazz, DashboardFlow_Table.id, withAction, actions);
            return (List<T>) dashboardMapper.mapToModels(dashboardFlows);
        }

        if (DashboardItem.class.equals(clazz)) {
            List<DashboardItemFlow> dashboardItemFlows = (List<DashboardItemFlow>) queryModels(
                    clazz, DashboardItemFlow_Table.id, withAction, actions);
            return (List<T>) dashboardItemMapper.mapToModels(dashboardItemFlows);
        }

        if (DashboardElement.class.equals(clazz)) {
            List<DashboardElementFlow> dashboardElementFlows = (List<DashboardElementFlow>)
                    queryModels(
                            clazz, DashboardElementFlow_Table.id, withAction, actions);
            return (List<T>) dashboardElementMapper.mapToModels(dashboardElementFlows);
        }

        if (Interpretation.class.equals(clazz)) {
            List<InterpretationFlow> interpretationFlows = (List<InterpretationFlow>) queryModels(
                    clazz, InterpretationFlow_Table.id, withAction, actions);
            return null;//(List<T>) Interpretation_Flow.toModels(interpretationFlows);
        }

        if (InterpretationElement.class.equals(clazz)) {
            List<InterpretationElementFlow> interpretationElementFlows =
                    (List<InterpretationElementFlow>) queryModels(clazz,
                            InterpretationElementFlow_Table.id, withAction, actions);
            return (List<T>) InterpretationElementFlow.toModels(interpretationElementFlows);
        }

        if (InterpretationComment.class.equals(clazz)) {
            List<InterpretationCommentFlow> interpretationCommentFlows =
                    (List<InterpretationCommentFlow>) queryModels(clazz,
                            InterpretationCommentFlow_Table.id, withAction, actions);
            return null;//(List<T>) InterpretationComment_Flow.toModels(interpretationCommentFlows);
        }

        if (Event.class.equals(clazz)) {
            List<EventFlow> eventFlows = (List<EventFlow>) queryModels(clazz,
                    EventFlow_Table.id, withAction, actions);
            return (List<T>) eventMapper.mapToModels(eventFlows);
        }

        if (Enrollment.class.equals(clazz)) {
            List<EnrollmentFlow> enrollmentFlows = (List<EnrollmentFlow>) queryModels(clazz,
                    EnrollmentFlow_Table.id, withAction, actions);
            return (List<T>) enrollmentMapper.mapToModels(enrollmentFlows);
        }

        if (TrackedEntityInstance.class.equals(clazz)) {
            List<TrackedEntityInstanceFlow> trackedEntityInstanceFlows =
                    (List<TrackedEntityInstanceFlow>) queryModels(clazz,
                            TrackedEntityInstanceFlow_Table.id, withAction, actions);
            return (List<T>) trackedEntityInstanceMapper.mapToModels(trackedEntityInstanceFlows);
        }

        return null;
    }

    private List<? extends Model> queryModels(Class<? extends IModel> clazz, LongProperty
            columnName,
                                              boolean withAction, @NotNull Action... actions) {

        /* Creating left join on State and destination table in order to perform filtering  */
        /* Joining tables based on mime type and then filtering resulting table by action */
//        From<? extends Model> from = new Select()
//                .from(getStateMapper().getRelatedDatabaseEntityClass(clazz))
//                .join(State_Flow.class, Join.JoinType.LEFT_OUTER)
//                .on(State_Flow_Table.itemId.eq(getStateMapper().getRelatedDatabaseEntityClass(
//                        clazz).getSimpleName() + "." + columnName));
//
//        Where<? extends Model> where = from
//                .where(State_Flow_Table
//                        .itemType.is(getStateMapper().getRelatedModelClass(clazz)));
//        if (withAction) {
//            int i = 0;
//            for (Action action : actions) {
//                if (i > 0) {
//                    where = where.or(State_Flow_Table.action.is(action));
//                } else {
//                    where = where.and(State_Flow_Table.action.is(action));
//                }
//                i++;
//            }
//        } else {
//            int i = 0;
//            for (Action action : actions) {
//                if (i > 0) {
//                    where = where.or(State_Flow_Table.action.isNot(action));
//                } else {
//                    where = where.and(State_Flow_Table.action.isNot(action));
//                }
//                i++;
//            }
//        }
//
//        List<? extends Model> list = where.queryList();
//        System.out.println("LIST: " + list.size());
//        return list;
        return null;
    }

    private IStateMapper getStateMapper() {
        return (IStateMapper) getMapper();
    }
}
