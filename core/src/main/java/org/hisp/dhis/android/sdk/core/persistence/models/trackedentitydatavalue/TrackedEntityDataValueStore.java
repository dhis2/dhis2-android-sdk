/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.core.persistence.models.trackedentitydatavalue;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.TrackedEntityDataValue$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.TrackedEntityDataValue$Flow$Table;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.hisp.dhis.android.sdk.models.trackedentitydatavalue.ITrackedEntityDataValueStore;
import org.hisp.dhis.android.sdk.models.trackedentitydatavalue.TrackedEntityDataValue;

import java.util.List;

public final class TrackedEntityDataValueStore implements ITrackedEntityDataValueStore {

    public TrackedEntityDataValueStore() {
        //empty constructor
    }

    @Override
    public void insert(TrackedEntityDataValue object) {
        TrackedEntityDataValue$Flow trackedEntityDataValueFlow =
                TrackedEntityDataValue$Flow.fromModel(object);
        trackedEntityDataValueFlow.insert();
    }

    @Override
    public void update(TrackedEntityDataValue object) {
        TrackedEntityDataValue$Flow.fromModel(object).update();
    }

    @Override
    public void save(TrackedEntityDataValue object) {
        TrackedEntityDataValue$Flow trackedEntityDataValueFlow =
                TrackedEntityDataValue$Flow.fromModel(object);
        trackedEntityDataValueFlow.save();
    }

    @Override
    public void delete(TrackedEntityDataValue object) {
        TrackedEntityDataValue$Flow.fromModel(object).delete();
    }

    @Override
    public List<TrackedEntityDataValue> query() {
        List<TrackedEntityDataValue$Flow> trackedEntityDataValueFlow = new Select()
                .from(TrackedEntityDataValue$Flow.class)
                .queryList();
        return TrackedEntityDataValue$Flow.toModels(trackedEntityDataValueFlow);
    }

    @Override
    public List<TrackedEntityDataValue> query(Event event) {
        if(event == null) {
            return null;
        }
        List<TrackedEntityDataValue$Flow> trackedEntityDataValueFlow = new Select()
                .from(TrackedEntityDataValue$Flow.class)
                .where(Condition.column(TrackedEntityDataValue$Flow$Table.EVENT_EVENT)
                        .is(event))
                .queryList();
        return TrackedEntityDataValue$Flow.toModels(trackedEntityDataValueFlow);
    }

    @Override
    public TrackedEntityDataValue query(DataElement dataElement, Event event) {
        if(dataElement == null || event == null) {
            return null;
        }
        return TrackedEntityDataValue$Flow.toModel(new Select().from(TrackedEntityDataValue$Flow.
                class).where(Condition.column(TrackedEntityDataValue$Flow$Table.EVENT_EVENT).
                is(event)).and(Condition.column(TrackedEntityDataValue$Flow$Table.
                DATAELEMENT).is(dataElement.getUId())).
                querySingle());
    }
}
