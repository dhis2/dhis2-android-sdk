/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteValueObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadWriteWithValueObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore;

import java.util.Date;
import java.util.Map;

import io.reactivex.Completable;

public final class TrackedEntityAttributeValueObjectRepository extends ReadWriteWithValueObjectRepositoryImpl
        <TrackedEntityAttributeValue, TrackedEntityAttributeValueObjectRepository>
        implements ReadWriteValueObjectRepository<TrackedEntityAttributeValue> {

    private final DataStatePropagator dataStatePropagator;
    private final String trackedEntityAttribute;
    private final String trackedEntityInstance;

    TrackedEntityAttributeValueObjectRepository(
            final TrackedEntityAttributeValueStore store,
            final Map<String, ChildrenAppender<TrackedEntityAttributeValue>> childrenAppenders,
            final RepositoryScope scope,
            final DataStatePropagator dataStatePropagator,
            final String trackedEntityAttribute,
            final String trackedEntityInstance) {
        super(store, childrenAppenders, scope, s -> new TrackedEntityAttributeValueObjectRepository(
                store, childrenAppenders, s, dataStatePropagator, trackedEntityAttribute, trackedEntityInstance));
        this.dataStatePropagator = dataStatePropagator;
        this.trackedEntityAttribute = trackedEntityAttribute;
        this.trackedEntityInstance = trackedEntityInstance;
    }

    @Override
    public Completable set(String value) {
        return Completable.fromAction(() -> blockingSet(value));
    }

    public void blockingSet(String value) throws D2Error {
        setObject(setBuilder().value(value).build());
    }

    @Override
    protected void delete(TrackedEntityAttributeValue trackedEntityAttributeValue) throws D2Error {
        blockingSet(null);
    }

    @Override
    public boolean blockingExists() {
        TrackedEntityAttributeValue value = blockingGetWithoutChildren();
        return value == null ? Boolean.FALSE : value.deleted() != Boolean.TRUE;
    }

    private TrackedEntityAttributeValue.Builder setBuilder() {
        Date date = new Date();
        if (blockingExists()) {
            return blockingGetWithoutChildren().toBuilder()
                    .lastUpdated(date);
        } else {
            return TrackedEntityAttributeValue.builder()
                    .created(date)
                    .lastUpdated(date)
                    .trackedEntityAttribute(trackedEntityAttribute)
                    .trackedEntityInstance(trackedEntityInstance);
        }
    }

    @Override
    protected void propagateState(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        dataStatePropagator.propagateTrackedEntityAttributeUpdate(trackedEntityAttributeValue);
    }
}