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
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqLikeItemFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public final class TrackedEntityInstanceQueryCollectionRepository
        implements ReadOnlyCollectionRepository<TrackedEntityInstance> {

    private final TrackedEntityInstanceStore store;
    private final TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;
    private final ScopedFilterConnectorFactory<TrackedEntityInstanceQueryCollectionRepository,
                    TrackedEntityInstanceQueryRepositoryScope> connectorFactory;

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
        this.connectorFactory = new ScopedFilterConnectorFactory<>(s ->
                new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders, s));
    }

    /**
     * Only TrackedEntityInstances coming from the server are shown in the list.
     * <br><b>Important:</b> Internet connection is required to use this mode.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository onlineOnly() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build());
    }

    /**
     * Only TrackedEntityInstances coming from local database are shown in the list.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository offlineOnly() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.OFFLINE_ONLY).build());
    }

    /**
     * TrackedEntityInstances coming from the server are shown in first place. Once there are no more results online,
     * it continues with TrackedEntityInstances in local database.
     * <br><b>Important:</b> Internet connection is required to use this mode.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository onlineFirst() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.ONLINE_FIRST).build());
    }

    /**
     * TrackedEntityInstances coming from local database are shown in first place. Once there are no more results, it
     * continues with TrackedEntityInstances coming from the server. This method may speed up the initial load.
     * <br><b>Important:</b> Internet connection is required to use this mode.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository offlineFirst() {
        return new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders,
                scope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build());
    }

    /**
     * Add an "attribute" filter to the query. If this method is call several times, conditions are appended with
     * AND connector.
     *
     * For example,
     * <pre><br>.byAttribute("uid1").eq("value1")<br>.byAttribute("uid2").eq("value2")<br></pre>
     * means that the instance must have attribute "uid1" with value "value1" <b>AND</b> attribute "uid2" with
     * value "value2".
     *
     * @param attributeId Attribute uid to use in the filter
     * @return Repository connector
     */
    public EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
                TrackedEntityInstanceQueryRepositoryScope> byAttribute(String attributeId) {
        return connectorFactory.eqLikeItemC(attributeId, filterItem -> {
            List<RepositoryScopeFilterItem> attributes = new ArrayList<>(scope.attribute());
            attributes.add(filterItem);
            return scope.toBuilder().attribute(attributes).build();
        });
    }

    /**
     * Add an "filter" to the query. If this method is call several times, conditions are appended with
     * AND connector.
     *
     * For example,
     * <pre><br>.byFilter("uid1").eq("value1")<br>.byFilter("uid2").eq("value2")<br></pre>
     * means that the instance must have attribute "uid1" with value "value1" <b>AND</b> attribute "uid2" with
     * value "value2".
     *
     * @param attributeId Attribute uid to use in the filter
     * @return Repository connector
     */
    public EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope> byFilter(String attributeId) {
        return connectorFactory.eqLikeItemC(attributeId, filterItem -> {
            List<RepositoryScopeFilterItem> filters = new ArrayList<>(scope.filter());
            filters.add(filterItem);
            return scope.toBuilder().filter(filters).build();
        });
    }

    /**
     * Search tracked entity instances with <b>any</b> attribute matching the query.
     *
     * @return Repository connector
     */
    public EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope> byQuery() {
        return connectorFactory.eqLikeItemC("", filterItem -> scope.toBuilder().query(filterItem).build());
    }

    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
                TrackedEntityInstanceQueryRepositoryScope, String> byProgram() {
        return connectorFactory.eqConnector(programUid -> scope.toBuilder().program(programUid).build());
    }

    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
                TrackedEntityInstanceQueryRepositoryScope, String> byOrgUnits() {
        return connectorFactory.listConnector(orgunitUids -> scope.toBuilder().orgUnits(orgunitUids).build());
    }

    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope, OrganisationUnitMode> byOrgUnitMode() {
        return connectorFactory.eqConnector(mode -> scope.toBuilder().orgUnitMode(mode).build());
    }

    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
                TrackedEntityInstanceQueryRepositoryScope, Date> byProgramStartDate() {
        return connectorFactory.eqConnector(date -> scope.toBuilder().programStartDate(date).build());
    }

    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
                TrackedEntityInstanceQueryRepositoryScope, Date> byProgramEndDate() {
        return connectorFactory.eqConnector(date -> scope.toBuilder().programEndDate(date).build());
    }

    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
                TrackedEntityInstanceQueryRepositoryScope, String> byTrackedEntityType() {
        return connectorFactory.eqConnector(type -> scope.toBuilder().trackedEntityType(type).build());
    }

    /**
     * Whether to include or not deleted tracked entity instances.
     * <br><b>IMPORTANT:</b> currently this filter only applies to <b>offline</b> instances.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope, Boolean> byIncludeDeleted() {
        return connectorFactory.eqConnector(bool -> scope.toBuilder().includeDeleted(bool).build());
    }

    /**
     * Filter by sync status.
     * <br><b>IMPORTANT:</b> using this filter forces <b>offlineOnly</b> mode.
     *
     * @return Repository connector
     */
    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope, State> byStates() {
        return connectorFactory.listConnector(states -> scope.toBuilder().states(states).build());
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
    public List<TrackedEntityInstance> blockingGet() {
        if (scope.mode().equals(RepositoryMode.OFFLINE_ONLY) || scope.mode().equals(RepositoryMode.OFFLINE_FIRST)) {
            String sqlQuery = TrackedEntityInstanceLocalQueryHelper.getSqlQuery(scope, Collections.emptyList(),
                    -1);
            List<TrackedEntityInstance> instances = store.selectRawQuery(sqlQuery);
            return ChildrenAppenderExecutor.appendInObjectCollection(instances, childrenAppenders,
                    new ChildrenSelection(Collections.singleton(
                            TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES), false));
        } else {
            try {
                TrackedEntityInstanceQueryOnline noPagingQuery = TrackedEntityInstanceQueryOnline.create(scope)
                        .toBuilder().paging(false).build();
                return onlineCallFactory.getCall(noPagingQuery).call();
            } catch (D2Error e) {
                return Collections.emptyList();
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public Single<List<TrackedEntityInstance>> get() {
        return Single.fromCallable(this::blockingGet);
    }

    @Override
    public Single<Integer> count() {
        return Single.fromCallable(this::blockingCount);
    }

    @Override
    public int blockingCount() {
        return blockingGet().size();
    }

    @Override
    public Single<Boolean> isEmpty() {
        return Single.fromCallable(this::blockingIsEmpty);
    }

    @Override
    public boolean blockingIsEmpty() {
        return blockingCount() == 0;
    }

    @Override
    public ReadOnlyObjectRepository<TrackedEntityInstance> one() {
        return new ReadOnlyObjectRepository<TrackedEntityInstance>() {

            @Override
            public Single<TrackedEntityInstance> get() {
                return Single.fromCallable(this::blockingGet);
            }

            @Override
            public TrackedEntityInstance blockingGet() {
                List<TrackedEntityInstance> list = TrackedEntityInstanceQueryCollectionRepository.this.blockingGet();
                return list.isEmpty() ? null : list.get(0);
            }

            @Override
            public Single<Boolean> exists() {
                return Single.fromCallable(this::blockingExists);
            }

            @Override
            public boolean blockingExists() {
                return blockingGet() != null;
            }
        };
    }
}
