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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.paging.ItemKeyedDataSource;

import static org.hisp.dhis.android.core.arch.repositories.scope.RepositoryMode.OFFLINE_ONLY;
import static org.hisp.dhis.android.core.arch.repositories.scope.RepositoryMode.ONLINE_ONLY;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceQueryDataSourceShould {

    private TrackedEntityInstanceQueryRepositoryScope emptyScope = TrackedEntityInstanceQueryRepositoryScope.empty();

    @Mock
    private TrackedEntityInstanceStore store;

    @Mock
    private TrackedEntityInstanceQueryCallFactory onlineCallFactory;

    @Mock
    private TrackedEntityInstance object;

    private List<TrackedEntityInstance> objects;

    @Mock
    private Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;

    @Mock
    private ItemKeyedDataSource.LoadInitialCallback<TrackedEntityInstance> initialCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(object.uid()).thenReturn("object-uid");
        objects = Collections.singletonList(object);

        when(store.selectRawQuery(anyString())).thenReturn(objects);
        when(onlineCallFactory.getCall(emptyScope.query().toBuilder().page(1).build())).thenReturn(() -> objects);
        when(onlineCallFactory.getCall(emptyScope.query().toBuilder().page(4).build())).thenReturn(Collections::emptyList);

        when(childrenAppenders.get(anyString())).thenReturn(identityAppender());
    }

    @Test
    public void get_initial_online_page() {
        TrackedEntityInstanceQueryRepositoryScope scope = emptyScope.toBuilder().mode(ONLINE_ONLY).build();
        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, scope.query().pageSize(), false),
                initialCallback);
        verify(onlineCallFactory).getCall(scope.query());
        verify(initialCallback).onResult(objects);
    }

    @Test
    public void get_initial_offline_page() {
        TrackedEntityInstanceQueryRepositoryScope scope = emptyScope.toBuilder().mode(OFFLINE_ONLY).build();
        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, scope.query().pageSize(), false),
                initialCallback);
        verify(store).selectRawQuery(anyString());
        verify(initialCallback).onResult(objects);
    }

    private ChildrenAppender<TrackedEntityInstance> identityAppender() {
        return new ChildrenAppender<TrackedEntityInstance>() {
            @Override
            protected TrackedEntityInstance appendChildren(TrackedEntityInstance m) {
                return m;
            }
        };
    }
}