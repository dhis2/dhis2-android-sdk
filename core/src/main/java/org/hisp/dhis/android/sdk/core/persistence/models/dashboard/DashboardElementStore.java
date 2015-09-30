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

package org.hisp.dhis.android.sdk.core.persistence.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardElement$Flow$Table;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;

import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardElementStore implements IDashboardElementStore {
    private final IStateStore stateStore;

    public DashboardElementStore(IStateStore stateStore) {
        this.stateStore = stateStore;
    }

    @Override
    public void insert(DashboardElement object) {
        DashboardElement$Flow elementFlow = DashboardElement$Flow
                .fromModel(object);
        elementFlow.insert();

        object.setId(elementFlow.getId());

        stateStore.save(object, Action.SYNCED);
    }

    @Override
    public void update(DashboardElement object) {
        DashboardElement$Flow.fromModel(object).update();
    }

    @Override
    public void save(DashboardElement object) {
        DashboardElement$Flow elementFlow
                = DashboardElement$Flow.fromModel(object);
        elementFlow.save();

        object.setId(elementFlow.getId());

        Action action = stateStore.queryAction(object);
        if (action == null) {
            stateStore.save(object, Action.SYNCED);
        }
    }

    @Override
    public void delete(DashboardElement object) {
        DashboardElement$Flow.fromModel(object).delete();

        DashboardElement$Flow dashboardElementFlow = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardElementFlow != null) {
            dashboardElementFlow.delete();

            stateStore.delete(object);
        }
    }

    @Override
    public List<DashboardElement> queryAll() {
        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .queryList();
        return DashboardElement$Flow.toModels(elementFlows);
    }

    @Override
    public DashboardElement queryById(long id) {
        DashboardElement$Flow dashboardElementFlow = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table.ID).is(id))
                .querySingle();
        return DashboardElement$Flow.toModel(dashboardElementFlow);
    }

    @Override
    public DashboardElement queryByUid(String uid) {
        DashboardElement$Flow dashboardElementFlow = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table.UID).is(uid))
                .querySingle();
        return DashboardElement$Flow.toModel(dashboardElementFlow);
    }

    @Override
    public List<DashboardElement> query(DashboardItem dashboardItem) {
        isNull(dashboardItem, "dashboard item must not be null");

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId()))
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    }

    /* @Override
    public List<DashboardElement> queryById(DashboardItem dashboardItem, Action... actions) {
        return queryById(dashboardItem, Arrays.asList(actions));
    }

    @Override
    public List<DashboardElement> queryById(DashboardItem dashboardItem, List<Action> actions) {
        if (actions != null && actions.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one Action");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(actions);
        combinedCondition = combinedCondition.and(Condition
                .column(DashboardElement$Flow$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId()));

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(combinedCondition)
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    }

    @Override
    public List<DashboardElement> filter(DashboardItem dashboardItem, Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Please, provide Action");
        }

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardElement$Flow$Table
                                .ACTION).isNot(action.toString()))
                        .and(Condition.column(DashboardElement$Flow$Table
                                .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId())))
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    }

    @Override
    public List<DashboardElement> filter(Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Please, provide Action");
        }

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table
                        .ACTION).isNot(action.toString()))
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    } */

    /* private static Condition.CombinedCondition buildCombinedCondition(List<Action> actions) {
        Condition.CombinedCondition combinedCondition = null;
        for (Action action : actions) {
            if (combinedCondition == null) {
                combinedCondition = Condition.CombinedCondition.begin(isState(action));
            } else {
                combinedCondition = combinedCondition.or(isState(action));
            }
        }
        return combinedCondition;
    } */

    /* private static Condition isState(Action action) {
        return Condition.column(DashboardElement$Flow$Table
                .ACTION).is(action.toString());
    } */
}
