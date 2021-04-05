/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.search;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import org.hisp.dhis.android.core.arch.cache.internal.D2Cache;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode.OFFLINE_FIRST;
import static org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode.OFFLINE_ONLY;
import static org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode.ONLINE_FIRST;

public final class TrackedEntityInstanceQueryDataSource
        extends ItemKeyedDataSource<TrackedEntityInstance, TrackedEntityInstance> {

    private final TrackedEntityInstanceStore store;
    private final TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private final TrackedEntityInstanceQueryRepositoryScope scope;
    private final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;
    private final D2Cache<TrackedEntityInstanceQueryOnline, List<TrackedEntityInstance>> onlineCache;
    private final TrackedEntityInstanceLocalQueryHelper localQueryHelper;

    private Set<String> returnedUidsOffline = new HashSet<>();
    private Set<String> returnedUidsOnline = new HashSet<>();

    private final List<TrackedEntityInstanceQueryOnline> baseOnlineQueries;

    private final Map<TrackedEntityInstanceQueryOnline, OnlineQueryStatus> onlineQueryStatusMap = new HashMap<>();

    private boolean isExhaustedOffline;

    TrackedEntityInstanceQueryDataSource(TrackedEntityInstanceStore store,
                                         TrackedEntityInstanceQueryCallFactory onlineCallFactory,
                                         TrackedEntityInstanceQueryRepositoryScope scope,
                                         Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders,
                                         D2Cache<TrackedEntityInstanceQueryOnline,
                                                 List<TrackedEntityInstance>> onlineCache,
                                         TrackedEntityInstanceQueryOnlineHelper onlineHelper,
                                         TrackedEntityInstanceLocalQueryHelper localQueryHelper) {
        this.store = store;
        this.onlineCallFactory = onlineCallFactory;
        this.scope = scope;
        this.childrenAppenders = childrenAppenders;
        this.onlineCache = onlineCache;
        this.localQueryHelper = localQueryHelper;

        this.baseOnlineQueries = onlineHelper.fromScope(scope);

        for (TrackedEntityInstanceQueryOnline onlineQuery : this.baseOnlineQueries) {
            this.onlineQueryStatusMap.put(onlineQuery, OnlineQueryStatus.create());
        }
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<TrackedEntityInstance> params,
                            @NonNull LoadInitialCallback<TrackedEntityInstance> callback) {
        returnedUidsOffline = new HashSet<>();
        returnedUidsOnline = new HashSet<>();
        callback.onResult(loadPages(params.requestedLoadSize));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<TrackedEntityInstance> params,
                          @NonNull LoadCallback<TrackedEntityInstance> callback) {
        callback.onResult(loadPages(params.requestedLoadSize));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<TrackedEntityInstance> params,
                           @NonNull LoadCallback<TrackedEntityInstance> callback) {
        // do nothing
    }

    @NonNull
    @Override
    public TrackedEntityInstance getKey(@NonNull TrackedEntityInstance item) {
        return item;
    }

    private List<TrackedEntityInstance> loadPages(int requestedLoadSize) {
        List<TrackedEntityInstance> result = new ArrayList<>();
        if (scope.mode().equals(OFFLINE_ONLY) || scope.mode().equals(OFFLINE_FIRST)) {
            if (!isExhaustedOffline) {
                List<TrackedEntityInstance> instances = queryOffline(requestedLoadSize);
                result.addAll(instances);
                isExhaustedOffline = instances.size() < requestedLoadSize;
            }

            if (result.size() < requestedLoadSize && scope.mode().equals(OFFLINE_FIRST)) {
                List<TrackedEntityInstance> onlineInstances = queryOnlineRecursive(requestedLoadSize);
                result.addAll(onlineInstances);
            }
        } else {
            List<TrackedEntityInstance> instances = queryOnlineRecursive(requestedLoadSize);
            result.addAll(instances);

            if (result.size() < requestedLoadSize && scope.mode().equals(ONLINE_FIRST)) {
                List<TrackedEntityInstance> onlineInstances = queryOffline(requestedLoadSize);
                result.addAll(onlineInstances);
            }
        }
        return result;
    }

    private List<TrackedEntityInstance> queryOffline(int requestedLoadSize) {
        String sqlQuery = localQueryHelper.getSqlQuery(scope, returnedUidsOffline,
                requestedLoadSize);
        List<TrackedEntityInstance> instances = store.selectRawQuery(sqlQuery);
        addUids(returnedUidsOffline, instances);
        return appendAttributes(instances);
    }

    private List<TrackedEntityInstance> queryOnlineRecursive(int requestLoadSize) {
        List<TrackedEntityInstance> result = new ArrayList<>();
        do {
            for (TrackedEntityInstanceQueryOnline baseOnlineQuery : baseOnlineQueries) {
                OnlineQueryStatus status = onlineQueryStatusMap.get(baseOnlineQuery);
                if (status.isExhausted) {
                    continue;
                }

                int page = status.requestedItems / requestLoadSize + 1;
                TrackedEntityInstanceQueryOnline onlineQuery = baseOnlineQuery.toBuilder()
                        .page(page)
                        .pageSize(requestLoadSize)
                        .paging(true).build();

                List<TrackedEntityInstance> queryInstances = queryOnline(onlineQuery);

                // If first page, the requestedSize is three times the original. Increment in three.
                status.requestedItems += requestLoadSize;
                status.isExhausted = queryInstances.size() < requestLoadSize;

                for (TrackedEntityInstance instance : queryInstances) {
                    if (!returnedUidsOffline.contains(instance.uid()) && !returnedUidsOnline.contains(instance.uid())) {
                        result.add(instance);
                    }
                }
                addUids(returnedUidsOnline, queryInstances);
            }
        } while (result.size() < requestLoadSize && !areAllOnlineQueriesExhausted());

        return result;
    }

    private List<TrackedEntityInstance> queryOnline(TrackedEntityInstanceQueryOnline onlineQuery) {
        try {
            List<TrackedEntityInstance> queryInstances = scope.allowOnlineCache() ? onlineCache.get(onlineQuery) : null;
            if (queryInstances == null) {
                queryInstances = onlineCallFactory.getCall(onlineQuery).call();
                onlineCache.set(onlineQuery, queryInstances);
            }
            return queryInstances;
        } catch (D2Error e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void addUids(Set<String> set, List<TrackedEntityInstance> instances) {
        for (TrackedEntityInstance instance : instances) {
            set.add(instance.uid());
        }
    }

    private List<TrackedEntityInstance> appendAttributes(List<TrackedEntityInstance> withoutChildren) {
        return ChildrenAppenderExecutor.appendInObjectCollection(withoutChildren, childrenAppenders,
                new ChildrenSelection(Collections.singleton(
                        TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES)));
    }

    private boolean areAllOnlineQueriesExhausted() {
        for (OnlineQueryStatus status : onlineQueryStatusMap.values()) {
            if (!status.isExhausted) {
                return false;
            }
        }
        return true;
    }
}

class OnlineQueryStatus {
    int requestedItems;
    boolean isExhausted;

    static OnlineQueryStatus create() {
        return new OnlineQueryStatus();
    }
}