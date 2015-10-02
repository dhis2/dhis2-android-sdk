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

package org.hisp.dhis.android.sdk.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.models.common.base.IModel;
import org.hisp.dhis.android.sdk.models.common.state.State;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.models.utils.Preconditions;
import org.hisp.dhis.android.sdk.persistence.models.common.meta.DbDhis;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME,
        uniqueColumnGroups = {
                @UniqueGroup(groupNumber = State$Flow.UNIQUE_GROUP_NUMBER, uniqueConflict = ConflictAction.REPLACE)
        }
)
public final class State$Flow extends BaseModel {
    static final int UNIQUE_GROUP_NUMBER = 1;

    @Column
    @PrimaryKey
    @Unique(unique = false, uniqueGroups = {UNIQUE_GROUP_NUMBER})
    long itemId;

    @Column
    @PrimaryKey
    @Unique(unique = false, uniqueGroups = {UNIQUE_GROUP_NUMBER})
    String itemType;

    // We need to specify FQCN in order to avoid collision with BaseMode.Action class.
    @Column
    org.hisp.dhis.android.sdk.models.common.state.Action action;

    public State$Flow() {
        // empty constructor
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;

    }

    public org.hisp.dhis.android.sdk.models.common.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.android.sdk.models.common.state.Action action) {
        this.action = action;
    }

    public static State toModel(State$Flow stateFlow) {
        if (stateFlow == null) {
            return null;
        }

        State state = new State();
        state.setItemId(stateFlow.getItemId());
        state.setItemType(getItemClass(stateFlow.getItemType()));
        state.setAction(stateFlow.getAction());

        return state;
    }

    public static List<State> toModels(List<State$Flow> stateFlows) {
        List<State> states = new ArrayList<>();

        if (stateFlows == null || stateFlows.isEmpty()) {
            return states;
        }

        for (State$Flow stateFlow : stateFlows) {
            states.add(toModel(stateFlow));
        }

        return states;
    }

    public static State$Flow fromModel(State state) {
        if (state == null) {
            return null;
        }

        State$Flow stateFlow = new State$Flow();
        stateFlow.setItemId(state.getItemId());
        stateFlow.setItemType(getItemType(state.getItemType()));
        stateFlow.setAction(state.getAction());

        return stateFlow;
    }

    public static List<State$Flow> fromModels(List<State> states) {
        List<State$Flow> stateFlows = new ArrayList<>();

        if (states == null || states.isEmpty()) {
            return stateFlows;
        }

        for (State state : states) {
            stateFlows.add(fromModel(state));
        }

        return stateFlows;
    }

    public static Class<? extends IModel> getItemClass(String type) {
        Preconditions.isNull(type, "type must not be null");

        if (Dashboard.class.getSimpleName().equals(type)) {
            return Dashboard.class;
        }

        if (DashboardItem.class.getSimpleName().equals(type)) {
            return DashboardItem.class;
        }

        if (DashboardElement.class.getSimpleName().equals(type)) {
            return DashboardElement.class;
        }

        if (Interpretation.class.getSimpleName().equals(type)) {
            return Interpretation.class;
        }

        if (InterpretationElement.class.getSimpleName().equals(type)) {
            return InterpretationElement.class;
        }

        if (InterpretationComment.class.getSimpleName().equals(type)) {
            return InterpretationComment.class;
        }

        if (TrackedEntityInstance.class.getSimpleName().equals(type)) {
            return TrackedEntityInstance.class;
        }

        if (Enrollment.class.getSimpleName().equals(type)) {
            return Enrollment.class;
        }

        if (Event.class.getSimpleName().equals(type)) {
            return Event.class;
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    public static Class<? extends Model> getFlowClass(Class<?> objectClass) {
        Preconditions.isNull(objectClass, "Class object must not be null");

        if (Dashboard.class.equals(objectClass)) {
            return Dashboard$Flow.class;
        }

        if (DashboardItem.class.equals(objectClass)) {
            return DashboardItem$Flow.class;
        }

        if (DashboardElement.class.equals(objectClass)) {
            return DashboardElement$Flow.class;
        }

        if (Interpretation.class.equals(objectClass)) {
            return Interpretation$Flow.class;
        }

        if (InterpretationElement.class.equals(objectClass)) {
            return InterpretationElement$Flow.class;
        }

        if (InterpretationComment.class.equals(objectClass)) {
            return InterpretationComment$Flow.class;
        }

        if (TrackedEntityInstance.class.equals(objectClass)) {
            return TrackedEntityInstance$Flow.class;
        }

        if (Enrollment.class.equals(objectClass)) {
            return Enrollment$Flow.class;
        }

        if (Event.class.equals(objectClass)) {
            return Event$Flow.class;
        }

        throw new IllegalArgumentException("Unsupported type: " + objectClass.getSimpleName());
    }

    public static String getItemType(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
