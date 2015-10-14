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

package org.hisp.dhis.android.sdk.core.models.trackedentity;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.models.flow.TrackedEntity$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.TrackedEntity$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntity;

import java.util.List;

public final class TrackedEntityStore implements IIdentifiableObjectStore<TrackedEntity> {

    public TrackedEntityStore() {
        //empty constructor
    }

    @Override
    public boolean insert(TrackedEntity object) {
        TrackedEntity$Flow trackedEntityFlow = TrackedEntity$Flow.fromModel(object);
        trackedEntityFlow.insert();

        object.setId(trackedEntityFlow.getId());
        return true;
    }

    @Override
    public boolean update(TrackedEntity object) {
        TrackedEntity$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(TrackedEntity object) {
        TrackedEntity$Flow trackedEntityFlow =
                TrackedEntity$Flow.fromModel(object);
        trackedEntityFlow.save();

        object.setId(trackedEntityFlow.getId());
        return true;
    }

    @Override
    public boolean delete(TrackedEntity object) {
        TrackedEntity$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<TrackedEntity> queryAll() {
        List<TrackedEntity$Flow> trackedEntityFlows = new Select()
                .from(TrackedEntity$Flow.class)
                .queryList();
        return TrackedEntity$Flow.toModels(trackedEntityFlows);
    }

    @Override
    public TrackedEntity queryById(long id) {
        TrackedEntity$Flow trackedEntityFlow = new Select()
                .from(TrackedEntity$Flow.class)
                .where(Condition.column(TrackedEntity$Flow$Table.ID).is(id))
                .querySingle();
        return TrackedEntity$Flow.toModel(trackedEntityFlow);
    }

    @Override
    public TrackedEntity queryByUid(String uid) {
        TrackedEntity$Flow trackedEntityFlow = new Select()
                .from(TrackedEntity$Flow.class)
                .where(Condition.column(TrackedEntity$Flow$Table.UID).is(uid))
                .querySingle();
        return TrackedEntity$Flow.toModel(trackedEntityFlow);
    }
}
