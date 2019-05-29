/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteValueObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteWithValueObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.DataStatePropagator;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Date;
import java.util.Map;

public final class TrackedEntityDataValueObjectRepository
        extends ReadWriteWithValueObjectRepositoryImpl<TrackedEntityDataValue, TrackedEntityDataValueObjectRepository>
        implements ReadWriteValueObjectRepository<TrackedEntityDataValue> {

    private final DataStatePropagator dataStatePropagator;
    private final String event;
    private final String dataElement;

    TrackedEntityDataValueObjectRepository(
            final TrackedEntityDataValueStore store,
            final Map<String, ChildrenAppender<TrackedEntityDataValue>> childrenAppenders,
            final RepositoryScope scope,
            final DataStatePropagator dataStatePropagator,
            final String event,
            final String dataElement) {
        super(store, childrenAppenders, scope, s -> new TrackedEntityDataValueObjectRepository(
                store, childrenAppenders, s, dataStatePropagator, event, dataElement));
        this.dataStatePropagator = dataStatePropagator;
        this.event = event;
        this.dataElement = dataElement;
    }

    public Unit set(String value) throws D2Error {
        objectWithValue = setBuilder().value(value).build();
        return setObject(objectWithValue);
    }

    private TrackedEntityDataValue.Builder setBuilder() {
        Date date = new Date();
        if (exists()) {
            return getWithoutChildren().toBuilder()
                    .lastUpdated(date);
        } else {
            return TrackedEntityDataValue.builder()
                    .created(date)
                    .lastUpdated(date)
                    .providedElsewhere(Boolean.FALSE)
                    .event(event)
                    .dataElement(dataElement);
        }
    }

    @Override
    protected void propagateState() {
        dataStatePropagator.propagateTrackedEntityDataValueUpdate(objectWithValue);
    }
}