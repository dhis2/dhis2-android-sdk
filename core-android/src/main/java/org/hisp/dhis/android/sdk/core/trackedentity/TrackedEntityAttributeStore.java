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

package org.hisp.dhis.android.sdk.core.trackedentity;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.flow.TrackedEntityAttribute$Flow;
import org.hisp.dhis.android.sdk.core.flow.TrackedEntityAttribute$Flow$Table;
import org.hisp.dhis.android.sdk.corejava.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;

public final class TrackedEntityAttributeStore implements IIdentifiableObjectStore<TrackedEntityAttribute> {

    public TrackedEntityAttributeStore() {
        //empty constructor
    }

    @Override
    public boolean insert(TrackedEntityAttribute object) {
        TrackedEntityAttribute$Flow trackedEntityAttributeFlow = TrackedEntityAttribute$Flow.fromModel(object);
        trackedEntityAttributeFlow.insert();

        object.setId(trackedEntityAttributeFlow.getId());
        return true;
    }

    @Override
    public boolean update(TrackedEntityAttribute object) {
        TrackedEntityAttribute$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(TrackedEntityAttribute object) {
        TrackedEntityAttribute$Flow trackedEntityAttributeFlow =
                TrackedEntityAttribute$Flow.fromModel(object);
        trackedEntityAttributeFlow.save();

        object.setId(trackedEntityAttributeFlow.getId());
        return true;
    }

    @Override
    public boolean delete(TrackedEntityAttribute object) {
        TrackedEntityAttribute$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<TrackedEntityAttribute> queryAll() {
        List<TrackedEntityAttribute$Flow> trackedEntityAttributeFlows = new Select()
                .from(TrackedEntityAttribute$Flow.class)
                .queryList();
        return TrackedEntityAttribute$Flow.toModels(trackedEntityAttributeFlows);
    }

    @Override
    public TrackedEntityAttribute queryById(long id) {
        TrackedEntityAttribute$Flow trackedEntityAttributeFlow = new Select()
                .from(TrackedEntityAttribute$Flow.class)
                .where(Condition.column(TrackedEntityAttribute$Flow$Table.ID).is(id))
                .querySingle();
        return TrackedEntityAttribute$Flow.toModel(trackedEntityAttributeFlow);
    }

    @Override
    public TrackedEntityAttribute queryByUid(String uid) {
        TrackedEntityAttribute$Flow trackedEntityAttributeFlow = new Select()
                .from(TrackedEntityAttribute$Flow.class)
                .where(Condition.column(TrackedEntityAttribute$Flow$Table.UID).is(uid))
                .querySingle();
        return TrackedEntityAttribute$Flow.toModel(trackedEntityAttributeFlow);
    }
}
