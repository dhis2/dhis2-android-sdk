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

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityInstanceQueryCollectionRepository
        implements ReadOnlyCollectionRepository<TrackedEntityInstance> {

    private final TrackedEntityInstanceStore store;
    private final TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;

    private final TrackedEntityInstanceQueryRepositoryScope scope;

    @Inject
    public TrackedEntityInstanceQueryCollectionRepository(
            final TrackedEntityInstanceStore store,
            final TrackedEntityInstanceQueryCallFactory onlineCallFactory,
            final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders,
            final TrackedEntityInstanceQueryRepositoryScope scope) {
        this.store = store;
        this.onlineCallFactory = onlineCallFactory;
        this.childrenAppenders = childrenAppenders;
        this.scope = scope;
    }

    public TrackedEntityInstanceQueryCollectionRepository onlineOnly() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository offlineOnly() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.OFFLINE_ONLY).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository onlineFirst() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.ONLINE_FIRST).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository offlineFirst() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build());
    }

    public TrackedEntityInstanceQueryCollectionRepository query(TrackedEntityInstanceQuery query) {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().query(query).build());
    }

    @Override
    public LiveData<PagedList<TrackedEntityInstance>> getPaged(int pageSize) {
        DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance> factory =
                new DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance>() {
                    @Override
                    public DataSource<TrackedEntityInstance, TrackedEntityInstance> create() {
                        return getDataSource();
                    }
                };

        return new LivePagedListBuilder<>(factory, pageSize).build();
    }

    public DataSource<TrackedEntityInstance, TrackedEntityInstance> getDataSource() {
        return new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders);
    }

    @Override
    public List<TrackedEntityInstance> get() {
        if (scope.mode().equals(RepositoryMode.OFFLINE_ONLY) || scope.mode().equals(RepositoryMode.OFFLINE_FIRST)) {
            String sqlQuery = TrackedEntityInstanceLocalQueryHelper.getSqlQuery(scope.query(), Collections.emptyList(),
                    -1);
            List<TrackedEntityInstance> instances = store.selectRawQuery(sqlQuery);
            return ChildrenAppenderExecutor.appendInObjectCollection(instances, childrenAppenders,
                    new ChildrenSelection(Collections.singleton(
                            TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES), false));
        } else {
            try {
                TrackedEntityInstanceQuery noPagingQuery = scope.query().toBuilder().paging(false).build();
                return onlineCallFactory.getCall(noPagingQuery).call();
            } catch (D2Error e) {
                return Collections.emptyList();
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public int count() {
        return get().size();
    }

    @Override
    public ReadOnlyObjectRepository<TrackedEntityInstance> one() {
        return new ReadOnlyObjectRepository<TrackedEntityInstance>() {
            @Override
            public TrackedEntityInstance get() {
                List<TrackedEntityInstance> list = TrackedEntityInstanceQueryCollectionRepository.this.get();
                return list.isEmpty() ? null : list.get(0);
            }

            @Override
            public boolean exists() {
                return get() != null;
            }
        };
    }
}
