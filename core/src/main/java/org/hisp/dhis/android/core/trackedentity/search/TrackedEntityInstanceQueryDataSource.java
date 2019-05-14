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

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenSelection;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import static org.hisp.dhis.android.core.arch.repositories.scope.RepositoryMode.OFFLINE_FIRST;
import static org.hisp.dhis.android.core.arch.repositories.scope.RepositoryMode.OFFLINE_ONLY;
import static org.hisp.dhis.android.core.arch.repositories.scope.RepositoryMode.ONLINE_FIRST;

public final class TrackedEntityInstanceQueryDataSource
        extends ItemKeyedDataSource<TrackedEntityInstance, TrackedEntityInstance> {

    private final TrackedEntityInstanceStore store;
    private final TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private final TrackedEntityInstanceQueryRepositoryScope scope;
    private final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;

    private final int initialLoadSizeFactor = 3;

    private List<String> returnedUids = new ArrayList<>();
    private int currentOnlinePage = 0;

    private boolean isExhaustedOnline = false;
    private boolean isExhaustedOffline = false;

    TrackedEntityInstanceQueryDataSource(TrackedEntityInstanceStore store,
                                         TrackedEntityInstanceQueryCallFactory onlineCallFactory,
                                         TrackedEntityInstanceQueryRepositoryScope scope,
                                         Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders) {
        this.store = store;
        this.onlineCallFactory = onlineCallFactory;
        this.scope = scope;
        this.childrenAppenders = childrenAppenders;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<TrackedEntityInstance> params,
                            @NonNull LoadInitialCallback<TrackedEntityInstance> callback) {
        returnedUids = new ArrayList<>();
        callback.onResult(loadPages(params.requestedLoadSize, true));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<TrackedEntityInstance> params,
                          @NonNull LoadCallback<TrackedEntityInstance> callback) {
        callback.onResult(loadPages(params.requestedLoadSize, false));
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

    private List<TrackedEntityInstance> loadPages(int requestedLoadSize, boolean isInitial) {
        List<TrackedEntityInstance> result = new ArrayList<>();
        if (scope.mode().equals(OFFLINE_ONLY) || scope.mode().equals(OFFLINE_FIRST)) {
            if (!isExhaustedOffline) {
                List<TrackedEntityInstance> instances = queryOffline(requestedLoadSize);
                result.addAll(instances);
                isExhaustedOffline = instances.size() < requestedLoadSize;
            }

            if (result.size() < requestedLoadSize && scope.mode().equals(OFFLINE_FIRST)) {
                List<TrackedEntityInstance> onlineInstances = queryOnlineRecursive(requestedLoadSize, isInitial);
                result.addAll(onlineInstances);
            }
        } else {
            if (!isExhaustedOnline) {
                List<TrackedEntityInstance> instances = queryOnlineRecursive(requestedLoadSize, isInitial);
                result.addAll(instances);
                isExhaustedOnline = instances.size() < requestedLoadSize;
            }

            if (result.size() < requestedLoadSize && scope.mode().equals(ONLINE_FIRST)) {
                List<TrackedEntityInstance> onlineInstances = queryOffline(requestedLoadSize);
                result.addAll(onlineInstances);
            }
        }
        return result;
    }

    private List<TrackedEntityInstance> queryOffline(int requestedLoadSize) {
        String sqlQuery = TrackedEntityInstanceLocalQueryHelper.getSqlQuery(scope.query(), returnedUids,
                requestedLoadSize);
        List<TrackedEntityInstance> instances = store.selectRawQuery(sqlQuery);
        addUids(returnedUids, instances);
        return appendAttributes(instances);
    }

    private List<TrackedEntityInstance> queryOnline(int requestLoadSize, boolean isInitial) {
        TrackedEntityInstanceQuery onlineQuery = scope.query().toBuilder()
                .page(currentOnlinePage + 1)
                .pageSize(requestLoadSize)
                .paging(true).build();

        // If first page, the requestedSize is three times the original. Increment in three.
        currentOnlinePage += isInitial ? initialLoadSizeFactor : 1;

        try {
            List<TrackedEntityInstance> instances = new ArrayList<>();
            for (TrackedEntityInstance instance : onlineCallFactory.getCall(onlineQuery).call()) {
                if (!returnedUids.contains(instance.uid())) {
                    instances.add(instance);
                }
            }
            return instances;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<TrackedEntityInstance> queryOnlineRecursive(int requestLoadSize, boolean isInitial) {
        List<TrackedEntityInstance> result = new ArrayList<>();
        List<TrackedEntityInstance> lastResult;
        do {
            lastResult = queryOnline(requestLoadSize, isInitial);
            result.addAll(lastResult);
        } while (result.size() < requestLoadSize && !lastResult.isEmpty());
        return result;
    }

    private void addUids(List<String> list, List<TrackedEntityInstance> instances) {
        for (TrackedEntityInstance instance : instances) {
            list.add(instance.uid());
        }
    }

    private List<TrackedEntityInstance> appendAttributes(List<TrackedEntityInstance> withoutChildren) {
        return ChildrenAppenderExecutor.appendInObjectCollection(withoutChildren, childrenAppenders,
                new ChildrenSelection(Collections.singleton(
                        TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES), false));
    }
}