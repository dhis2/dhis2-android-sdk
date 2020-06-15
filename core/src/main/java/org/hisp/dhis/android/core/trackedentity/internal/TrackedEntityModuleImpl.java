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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttributeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public final class TrackedEntityModuleImpl implements TrackedEntityModule {

    private final TrackedEntityTypeCollectionRepository trackedEntityTypes;
    private final TrackedEntityInstanceCollectionRepository trackedEntityInstances;
    private final TrackedEntityDataValueCollectionRepository trackedEntityDataValues;
    private final TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValues;
    private final TrackedEntityAttributeCollectionRepository trackedEntityAttributes;
    private final TrackedEntityTypeAttributeCollectionRepository trackedEntityTypeAttributes;

    private final TrackedEntityInstanceQueryCollectionRepository trackedEntityInstanceQuery;

    private final TrackedEntityAttributeReservedValueManager reservedValueManager;

    private final TrackedEntityInstanceDownloader trackedEntityInstanceDownloader;
    private final TrackedEntityInstanceListDownloadAndPersistCallFactory downloadAndPersistCallFactory;

    private final TrackedEntityInstanceService trackedEntityInstanceService;

    @Inject
    TrackedEntityModuleImpl(
            TrackedEntityTypeCollectionRepository trackedEntityTypes,
            TrackedEntityInstanceCollectionRepository trackedEntityInstances,
            TrackedEntityDataValueCollectionRepository trackedEntityDataValues,
            TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValues,
            TrackedEntityAttributeCollectionRepository trackedEntityAttributes,
            TrackedEntityTypeAttributeCollectionRepository trackedEntityTypeAttributes,
            TrackedEntityAttributeReservedValueManager reservedValueManager,
            TrackedEntityInstanceDownloader trackedEntityInstanceDownloader,
            TrackedEntityInstanceListDownloadAndPersistCallFactory downloadAndPersistCallFactory,
            TrackedEntityInstanceQueryCollectionRepository trackedEntityInstanceQuery,
            TrackedEntityInstanceService trackedEntityInstanceService) {
        this.trackedEntityTypes = trackedEntityTypes;
        this.trackedEntityInstances = trackedEntityInstances;
        this.trackedEntityDataValues = trackedEntityDataValues;
        this.trackedEntityAttributeValues = trackedEntityAttributeValues;
        this.trackedEntityAttributes = trackedEntityAttributes;
        this.trackedEntityTypeAttributes =trackedEntityTypeAttributes;
        this.reservedValueManager = reservedValueManager;
        this.trackedEntityInstanceDownloader = trackedEntityInstanceDownloader;
        this.downloadAndPersistCallFactory = downloadAndPersistCallFactory;
        this.trackedEntityInstanceQuery = trackedEntityInstanceQuery;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
    }

    /**
     * Download and persists a list of TrackedEntityInstances for a specific program. This method is required to
     * download glass-protected TrackedEntityInstances.
     *
     * @param uids List of TrackedEntityInstance uids
     * @param program Program uid
     * @return -
     */
    Single<List<TrackedEntityInstance>> downloadTrackedEntityInstancesByUid(Collection<String> uids,
                                                                                     String program) {
        return downloadAndPersistCallFactory.getCall(uids, program);
    }


    /**
     * Download and persists a list of TrackedEntityInstances for a specific program. This method is required to
     * download glass-protected TrackedEntityInstances.
     *
     * @param uids List of TrackedEntityInstance uids
     * @param program Program uid
     * @return -
     */
    List<TrackedEntityInstance> blockingDownloadTrackedEntityInstancesByUid(Collection<String> uids,
                                                                                     String program) {
        return downloadTrackedEntityInstancesByUid(uids, program).blockingGet();
    }

    @Override
    public TrackedEntityTypeCollectionRepository trackedEntityTypes() {
        return trackedEntityTypes;
    }

    @Override
    public TrackedEntityInstanceCollectionRepository trackedEntityInstances() {
        return trackedEntityInstances;
    }

    @Override
    public TrackedEntityDataValueCollectionRepository trackedEntityDataValues() {
        return trackedEntityDataValues;
    }

    @Override
    public TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValues() {
        return trackedEntityAttributeValues;
    }

    @Override
    public TrackedEntityAttributeCollectionRepository trackedEntityAttributes() {
        return trackedEntityAttributes;
    }

    @Override
    public TrackedEntityTypeAttributeCollectionRepository trackedEntityTypeAttributes() {
        return trackedEntityTypeAttributes;
    }

    @Override
    public TrackedEntityInstanceQueryCollectionRepository trackedEntityInstanceQuery() {
        return trackedEntityInstanceQuery;
    }

    @Override
    public TrackedEntityAttributeReservedValueManager reservedValueManager() {
        return reservedValueManager;
    }

    @Override
    public TrackedEntityInstanceDownloader trackedEntityInstanceDownloader() {
        return trackedEntityInstanceDownloader;
    }

    @Override
    public TrackedEntityInstanceService trackedEntityInstanceService() {
        return trackedEntityInstanceService;
    }
}
