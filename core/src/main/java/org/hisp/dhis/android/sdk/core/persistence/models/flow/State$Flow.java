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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.state.State;

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
    int itemId;

    @Column
    @PrimaryKey
    @Unique(unique = false, uniqueGroups = {UNIQUE_GROUP_NUMBER})
    Class<?> itemType;

    // We need to specify FQCN in order to avoid collision with BaseMode.Action class.
    @Column
    org.hisp.dhis.android.sdk.models.state.Action action;

    public State$Flow() {
        // empty constructor
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Class<?> getItemType() {
        return itemType;
    }

    public void setItemType(Class<?> itemType) {
        this.itemType = itemType;
    }

    public org.hisp.dhis.android.sdk.models.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.android.sdk.models.state.Action action) {
        this.action = action;
    }

    public static State toModel(State$Flow stateFlow) {
        if (stateFlow == null) {
            return null;
        }

        State state = new State();
        state.setItemId(stateFlow.getItemId());
        state.setItemType(stateFlow.getItemType());
        state.setAction(stateFlow.getAction());

        return state;
    }

    public static State$Flow fromModel(State state) {
        if (state == null) {
            return null;
        }

        State$Flow stateFlow = new State$Flow();
        stateFlow.setItemId(state.getItemId());
        stateFlow.setItemType(state.getItemType());
        stateFlow.setAction(state.getAction());

        return stateFlow;
    }
}
