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

public final class TrackedEntityInstanceQueryDataSource
        extends ItemKeyedDataSource<TrackedEntityInstance, TrackedEntityInstance> {

    private final TrackedEntityInstanceStore store;
    private final TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private final TrackedEntityInstanceQueryRepositoryScope scope;
    private final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;

    private List<String> returnedUids = new ArrayList<>();

    public TrackedEntityInstanceQueryDataSource(TrackedEntityInstanceStore store,
                                                TrackedEntityInstanceQueryCallFactory onlineCallFactory,
                                                TrackedEntityInstanceQueryRepositoryScope scope,
                                                Map<String, ChildrenAppender<TrackedEntityInstance>>
                                                        childrenAppenders) {
        this.store = store;
        this.onlineCallFactory = onlineCallFactory;
        this.scope = scope;
        this.childrenAppenders = childrenAppenders;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<TrackedEntityInstance> params,
                            @NonNull LoadInitialCallback<TrackedEntityInstance> callback) {
        List<TrackedEntityInstance> result = new ArrayList<>();
        if (scope.mode().equals(OFFLINE_ONLY) || scope.mode().equals(OFFLINE_FIRST)) {
            List<TrackedEntityInstance> instances = queryOffline(params.requestedLoadSize);
            result.addAll(appendEnrollments(instances));
        } else {
            List<TrackedEntityInstance> instances = queryOnline(params.requestedLoadSize);
            result.addAll(instances);
        }
        callback.onResult(result);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<TrackedEntityInstance> params,
                          @NonNull LoadCallback<TrackedEntityInstance> callback) {
        loadPages(params, callback, false);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<TrackedEntityInstance> params,
                           @NonNull LoadCallback<TrackedEntityInstance> callback) {
        loadPages(params, callback, true);
    }

    private void loadPages(@NonNull LoadParams<TrackedEntityInstance> params,
                           @NonNull LoadCallback<TrackedEntityInstance> callback, boolean reversed) {
        List<TrackedEntityInstance> result = new ArrayList<>();
        if (scope.mode().equals(OFFLINE_ONLY) || scope.mode().equals(OFFLINE_FIRST)) {
            List<TrackedEntityInstance> instances = queryOffline(params.requestedLoadSize);
            result.addAll(appendEnrollments(instances));
        } else {
            List<TrackedEntityInstance> instances = queryOnline(params.requestedLoadSize);
            result.addAll(instances);
        }
        callback.onResult(result);
    }

    @NonNull
    @Override
    public TrackedEntityInstance getKey(@NonNull TrackedEntityInstance item) {
        return item;
    }

    private List<TrackedEntityInstance> queryOffline(int requestedLoadSize) {
        String sqlQuery = TrackedEntityInstanceLocalQueryHelper.getSqlQuery(scope.query(), returnedUids,
                requestedLoadSize);
        List<TrackedEntityInstance> instances = store.selectRawQuery(sqlQuery);
        addUids(returnedUids, instances);
        return instances;
    }

    private List<TrackedEntityInstance> queryOnline(int requestLoadSize) {
        TrackedEntityInstanceQuery onlineQuery = scope.query().toBuilder()
                .page(1)
                .pageSize(requestLoadSize)
                .paging(true).build();
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

    private void addUids(List<String> list, List<TrackedEntityInstance> instances) {
        for (TrackedEntityInstance instance : instances) {
            list.add(instance.uid());
        }
    }

    private List<TrackedEntityInstance> appendEnrollments(List<TrackedEntityInstance> withoutChildren) {
        return ChildrenAppenderExecutor.appendInObjectCollection(withoutChildren, childrenAppenders,
                new ChildrenSelection(Collections.singleton(TrackedEntityInstanceFields.ENROLLMENTS), false));
    }
}