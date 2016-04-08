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

package org.hisp.dhis.client.sdk.android.trackedentity;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public final class TrackedEntityDataValueStoreImpl extends AbsStore<TrackedEntityDataValue,
        TrackedEntityDataValueFlow> implements TrackedEntityDataValueStore {

    public TrackedEntityDataValueStoreImpl() {
        super(TrackedEntityDataValueFlow.MAPPER);
    }

    @Override
    public List<TrackedEntityDataValue> query(Event event) {
        return query(Arrays.asList(event));
    }

    @Override
    public List<TrackedEntityDataValue> query(List<Event> events) {
        isNull(events, "List of events must not be null");

        Set<String> eventUids = ModelUtils.toUidSet(events);
        List<TrackedEntityDataValueFlow> trackedEntityDataValueFlows = new Select()
                .from(TrackedEntityDataValueFlow.class)
                .where(TrackedEntityDataValueFlow_Table
                        .event.in(eventUids))
                .queryList();

        return getMapper().mapToModels(trackedEntityDataValueFlows);
    }

    @Override
    public TrackedEntityDataValue query(Event event, DataElement dataElement) {
        isNull(event, "Event object must ot be null");
        isNull(dataElement, "DataElement object must not be null");

        return getMapper().mapToModel(new Select()
                .from(TrackedEntityDataValueFlow.class)
                .where(TrackedEntityDataValueFlow_Table
                        .event.is(event.getUId()))
                .and(TrackedEntityDataValueFlow_Table
                        .dataElement.is(dataElement.getUId()))
                .querySingle());
    }
}
