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
package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.arch.repositories.collection.CallableReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityInstanceQueryCollectionRepository
        implements CallableReadOnlyCollectionRepository<TrackedEntityInstance> {

    private TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private TrackedEntityInstanceLocalQueryCallFactory offlineCallFactory;

    private TrackedEntityInstanceQueryRepositoryScope scope;

    @Inject
    public TrackedEntityInstanceQueryCollectionRepository(TrackedEntityInstanceQueryCallFactory onlineCallFactory,
                                                   TrackedEntityInstanceLocalQueryCallFactory offlineCallFactory,
                                                   TrackedEntityInstanceQueryRepositoryScope scope) {
        this.onlineCallFactory = onlineCallFactory;
        this.offlineCallFactory = offlineCallFactory;
        this.scope = scope;
    }

    public TrackedEntityInstanceQueryCollectionRepository onlineOnly() {
        return new TrackedEntityInstanceQueryCollectionRepository(onlineCallFactory, offlineCallFactory,
                scope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository offlineOnly() {
        return new TrackedEntityInstanceQueryCollectionRepository(onlineCallFactory, offlineCallFactory,
                scope.toBuilder().mode(RepositoryMode.OFFLINE_ONLY).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository onlineFirst() {
        return new TrackedEntityInstanceQueryCollectionRepository(onlineCallFactory, offlineCallFactory,
                scope.toBuilder().mode(RepositoryMode.ONLINE_FIRST).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository offlineFirst() {
        return new TrackedEntityInstanceQueryCollectionRepository(onlineCallFactory, offlineCallFactory,
                scope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository query(TrackedEntityInstanceQuery query) {
        return new TrackedEntityInstanceQueryCollectionRepository(onlineCallFactory, offlineCallFactory,
                scope.toBuilder().query(query).build());
    }

    @Override
    public Callable<List<TrackedEntityInstance>> getCallable() {
        if (scope.mode().equals(RepositoryMode.ONLINE_ONLY)) {
            return onlineCallFactory.getCall(scope.query());
        } else {
            return offlineCallFactory.getCall(scope.query());
        }
    }
}
