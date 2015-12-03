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

package org.hisp.dhis.android.sdk.common.state;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.flow.Enrollment$Flow;
import org.hisp.dhis.android.sdk.flow.Event$Flow;
import org.hisp.dhis.android.sdk.flow.Interpretation$Flow;
import org.hisp.dhis.android.sdk.flow.InterpretationComment$Flow;
import org.hisp.dhis.android.sdk.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.sdk.flow.State$Flow;
import org.hisp.dhis.android.sdk.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.java.sdk.models.common.base.IModel;
import org.hisp.dhis.java.sdk.models.common.state.State;
import org.hisp.dhis.java.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.java.sdk.models.event.Event;
import org.hisp.dhis.java.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.java.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.java.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;

import static org.hisp.dhis.java.sdk.models.utils.Preconditions.isNull;

public class StateMapper extends AbsMapper<State, State$Flow> implements IStateMapper {

    @Override
    public State$Flow mapToDatabaseEntity(State state) {
        if (state == null) {
            return null;
        }

        State$Flow stateFlow = new State$Flow();
        stateFlow.setItemId(state.getItemId());
        stateFlow.setItemType(getRelatedModelClass(state.getItemType()));
        stateFlow.setAction(state.getAction());

        return stateFlow;
    }

    @Override
    public State mapToModel(State$Flow stateFlow) {
        if (stateFlow == null) {
            return null;
        }

        State state = new State();
        state.setItemId(stateFlow.getItemId());
        state.setItemType(getRelatedModelClass(stateFlow.getItemType()));
        state.setAction(stateFlow.getAction());

        return state;
    }

    @Override
    public Class<State> getModelTypeClass() {
        return State.class;
    }

    @Override
    public Class<State$Flow> getDatabaseEntityTypeClass() {
        return State$Flow.class;
    }

    @Override
    public Class<? extends IModel> getRelatedModelClass(String type) {
        isNull(type, "type must not be null");

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

    @Override
    public String getRelatedModelClass(Class<? extends IModel> clazz) {
        isNull(clazz, "clazz must not be null");
        return clazz.toString();
    }

    @Override
    public Class<? extends Model> getRelatedDatabaseEntityClass(Class<? extends IModel> objectClass) {
        isNull(objectClass, "Class object must not be null");

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
}
