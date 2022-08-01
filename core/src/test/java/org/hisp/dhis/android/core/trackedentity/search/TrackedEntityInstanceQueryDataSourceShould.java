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

package org.hisp.dhis.android.core.trackedentity.search;

import androidx.paging.ItemKeyedDataSource;

import org.hisp.dhis.android.core.arch.cache.internal.D2Cache;
import org.hisp.dhis.android.core.arch.cache.internal.ExpirableCache;
import org.hisp.dhis.android.core.arch.helpers.Result;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.period.internal.CalendarProvider;
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory;
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode.OFFLINE_FIRST;
import static org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode.OFFLINE_ONLY;
import static org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode.ONLINE_ONLY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceQueryDataSourceShould {

    private TrackedEntityInstanceQueryRepositoryScope singleEventFilterScope, multipleEventFilterScope;

    @Mock
    private TrackedEntityInstanceStore store;

    @Mock
    private TrackedEntityInstanceQueryCallFactory onlineCallFactory;

    private List<TrackedEntityInstance> offlineObjects, onlineObjects1, onlineObjects2;

    @Captor
    ArgumentCaptor<List<TrackedEntityInstance>> captureInstances;

    @Mock
    private Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;

    @Mock
    private ItemKeyedDataSource.LoadInitialCallback<TrackedEntityInstance> initialCallback;

    private final CalendarProvider calendarProvider = CalendarProviderFactory.getCalendarProvider();

    private final DateFilterPeriodHelper periodHelper =
            new DateFilterPeriodHelper(calendarProvider, ParentPeriodGeneratorImpl.create(calendarProvider));

    private final TrackedEntityInstanceQueryOnlineHelper onlineHelper =
            new TrackedEntityInstanceQueryOnlineHelper(periodHelper);

    private final TrackedEntityInstanceLocalQueryHelper localQueryHelper =
            new TrackedEntityInstanceLocalQueryHelper(periodHelper);

    private final D2Cache<TrackedEntityInstanceQueryOnline, List<Result<TrackedEntityInstance, D2Error>>> onlineCache =
            new ExpirableCache<>();

    private final int initialLoad = 30;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        offlineObjects = Arrays.asList(
                TrackedEntityInstance.builder().uid("offline1").build(),
                TrackedEntityInstance.builder().uid("offline2").build(),
                TrackedEntityInstance.builder().uid("offline3").build());

        onlineObjects1 = Arrays.asList(
                TrackedEntityInstance.builder().uid("online1").build(),
                TrackedEntityInstance.builder().uid("offline2").build(),
                TrackedEntityInstance.builder().uid("online3").build(),
                TrackedEntityInstance.builder().uid("online4").build(),
                TrackedEntityInstance.builder().uid("online5").build());

        onlineObjects2 = Arrays.asList(
                TrackedEntityInstance.builder().uid("online5").build(),
                TrackedEntityInstance.builder().uid("online6").build());

        when(store.selectRawQuery(anyString())).thenReturn(offlineObjects);

        singleEventFilterScope = emptyScopeWithModes(AssignedUserMode.ANY);
        multipleEventFilterScope = emptyScopeWithModes(AssignedUserMode.ANY, AssignedUserMode.CURRENT);

        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.ANY)))).thenReturn(() -> onlineObjects1);
        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(2, initialLoad, AssignedUserMode.ANY)))).thenReturn(Collections::emptyList);
        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(4, 10, AssignedUserMode.ANY)))).thenReturn(Collections::emptyList);

        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.CURRENT)))).thenReturn(() -> onlineObjects2);
        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(4, 10, AssignedUserMode.CURRENT)))).thenReturn(Collections::emptyList);

        when(childrenAppenders.get(anyString())).thenReturn(identityAppender());
    }

    @Test
    public void get_initial_online_page() {
        TrackedEntityInstanceQueryRepositoryScope scope = singleEventFilterScope.toBuilder().mode(ONLINE_ONLY).build();
        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);

        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, initialLoad, false),
                initialCallback);
        verify(onlineCallFactory).getCall(argThat(new QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.ANY)));
        verify(initialCallback).onResult(onlineObjects1);
        verifyNoMoreInteractions(onlineCallFactory);
    }

    @Test
    public void get_initial_offline_page() {
        TrackedEntityInstanceQueryRepositoryScope scope = singleEventFilterScope.toBuilder().mode(OFFLINE_ONLY).build();
        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);

        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, initialLoad, false),
                initialCallback);
        verify(store).selectRawQuery(anyString());
        verify(initialCallback).onResult(offlineObjects);
        verifyNoMoreInteractions(store);
    }

    @Test
    public void query_online_when_offline_exhausted() {
        TrackedEntityInstanceQueryRepositoryScope scope = singleEventFilterScope.toBuilder().mode(OFFLINE_FIRST).build();
        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);

        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, initialLoad, false),
                initialCallback);
        verify(store).selectRawQuery(anyString());
        verifyNoMoreInteractions(store);
        verify(onlineCallFactory).getCall(argThat(new QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.ANY)));
        verifyNoMoreInteractions(onlineCallFactory);
    }

    @Test
    public void query_online_again_if_not_exhausted_and_use_right_paging() {
        TrackedEntityInstanceQueryRepositoryScope scope = singleEventFilterScope.toBuilder().mode(OFFLINE_FIRST).build();
        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);

        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(1, 4, AssignedUserMode.ANY)))).thenReturn(() -> onlineObjects1);
        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(3, 2, AssignedUserMode.ANY)))).thenReturn(Collections::emptyList);

        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, 4, false),
                initialCallback);
        verify(store).selectRawQuery(anyString());
        verifyNoMoreInteractions(store);
        verify(onlineCallFactory).getCall(argThat(new QueryPageUserModeMatcher(1, 4, AssignedUserMode.ANY)));

        dataSource.loadAfter(new ItemKeyedDataSource.LoadParams<>(null, 2), initialCallback);

        verify(onlineCallFactory).getCall(argThat(new QueryPageUserModeMatcher(3, 2, AssignedUserMode.ANY)));
        verifyNoMoreInteractions(onlineCallFactory);
    }

    @Test
    public void get_initial_online_page_from_cache() {
        TrackedEntityInstanceQueryRepositoryScope scope =
                singleEventFilterScope.toBuilder().mode(ONLINE_ONLY).allowOnlineCache(true).build();

        TrackedEntityInstanceQueryDataSource dataSource1 =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);
        dataSource1.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, initialLoad, false),
                initialCallback);
        verify(onlineCallFactory).getCall(any());

        TrackedEntityInstanceQueryDataSource dataSource2 =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);
        dataSource2.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, initialLoad, false),
                initialCallback);
        verifyNoMoreInteractions(onlineCallFactory);
    }

    @Test
    public void get_multiple_event_filter_queries() {
        TrackedEntityInstanceQueryRepositoryScope scope = multipleEventFilterScope.toBuilder().mode(OFFLINE_FIRST).build();

        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);

        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, initialLoad, false),
                initialCallback);

        verify(store).selectRawQuery(anyString());
        verifyNoMoreInteractions(store);
        verify(onlineCallFactory).getCall(argThat(new QueryUserModeMatcher(AssignedUserMode.ANY)));
        verify(onlineCallFactory).getCall(argThat(new QueryUserModeMatcher(AssignedUserMode.CURRENT)));
        verifyNoMoreInteractions(onlineCallFactory);

        verify(initialCallback).onResult(captureInstances.capture());
        assertThat(captureInstances.getValue().size()).isEqualTo(8);
    }

    /*
     This test makes sense when the online call returns the number of values requested, but the response contains
     values already returned offline (duplicates), so it is needed to do a second query.
     */
    @Test
    public void get_second_online_page_if_needed_in_initial_load() {
        TrackedEntityInstanceQueryRepositoryScope scope = singleEventFilterScope.toBuilder().mode(OFFLINE_FIRST).build();

        TrackedEntityInstanceQueryDataSource dataSource =
                new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope, childrenAppenders,
                        onlineCache, onlineHelper, localQueryHelper);

        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(1, 5, AssignedUserMode.ANY)))).thenReturn(() -> onlineObjects1);
        when(onlineCallFactory.getCall(argThat(new QueryPageUserModeMatcher(2, 5, AssignedUserMode.ANY)))).thenReturn(Collections::emptyList);

        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, 5, false),
                initialCallback);

        verify(store).selectRawQuery(anyString());
        verifyNoMoreInteractions(store);
        verify(onlineCallFactory).getCall(argThat(new QueryPageUserModeMatcher(1, 5, AssignedUserMode.ANY)));
        verify(onlineCallFactory).getCall(argThat(new QueryPageUserModeMatcher(2, 5, AssignedUserMode.ANY)));
        verifyNoMoreInteractions(onlineCallFactory);
    }

    private ChildrenAppender<TrackedEntityInstance> identityAppender() {
        return new ChildrenAppender<TrackedEntityInstance>() {
            @Override
            public TrackedEntityInstance appendChildren(TrackedEntityInstance m) {
                return m;
            }
        };
    }

    private TrackedEntityInstanceQueryRepositoryScope emptyScopeWithModes(AssignedUserMode ...assignedUserModes) {
        List<TrackedEntityInstanceQueryEventFilter> eventFilters = new ArrayList<>();
        for (AssignedUserMode mode : assignedUserModes) {
            eventFilters.add(TrackedEntityInstanceQueryEventFilter.builder().assignedUserMode(mode).build());
        }
        return TrackedEntityInstanceQueryRepositoryScope.builder().eventFilters(eventFilters).build();
    }
}