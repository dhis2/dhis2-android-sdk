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

package org.hisp.dhis.android.core.fileresource.internal;

import android.content.Context;
import android.util.Log;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

@Reusable
class FileResourceCallFactory {

    private final FileResourceService fileResourceService;
    private final HandlerWithTransformer<FileResource> handler;
    private final IdentifiableDataObjectStore<FileResource> store;
    private final APICallExecutor apiCallExecutor;
    private final Context context;
    private final SystemInfoModuleDownloader systemInfoDownloader;

    private enum Dimension {
        SMALL, MEDIUM
    }

    @Inject
    FileResourceCallFactory(@NonNull FileResourceService fileResourceService,
                            @NonNull HandlerWithTransformer<FileResource> handler,
                            @NonNull IdentifiableDataObjectStore<FileResource> store,
                            @NonNull APICallExecutor apiCallExecutor,
                            @NonNull Context context,
                            @NonNull SystemInfoModuleDownloader systemInfoDownloader) {
        this.fileResourceService = fileResourceService;
        this.handler = handler;
        this.store = store;
        this.apiCallExecutor = apiCallExecutor;
        this.context = context;
        this.systemInfoDownloader = systemInfoDownloader;
    }

    public Observable<D2Progress> create(final List<TrackedEntityAttributeValue> trackedEntityAttributeValues,
                                         final List<TrackedEntityDataValue> trackedEntityDataValues) {

        int attributeNotifications = trackedEntityAttributeValues.isEmpty() ? 0 : 1;
        int dataValueNotifications = trackedEntityDataValues.isEmpty() ? 0 : 1;
        if (attributeNotifications + dataValueNotifications == 0) {
            return Observable.empty();
        } else {
            D2ProgressManager progressManager = new D2ProgressManager(
                    attributeNotifications + dataValueNotifications + 1);

            Single<D2Progress> systemInfoDownload = systemInfoDownloader.downloadMetadata().toSingle(() ->
                    progressManager.increaseProgress(SystemInfo.class, false));

            return systemInfoDownload.flatMapObservable(systemInfoProgress -> Observable.create(emitter -> {
                if (attributeNotifications == 1) {
                    downloadAttributeValueFiles(trackedEntityAttributeValues);
                    emitter.onNext(progressManager.increaseProgress(TrackedEntityAttributeValue.class, true));
                }

                if (dataValueNotifications == 1) {
                    downloadDataValueFiles(trackedEntityDataValues);
                    emitter.onNext(progressManager.increaseProgress(TrackedEntityDataValue.class, true));
                }

                emitter.onComplete();
            }));
        }
    }

    private void downloadAttributeValueFiles(final List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        List<FileResource> fileResources = new ArrayList<>();

        for (TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues) {
            try {
                ResponseBody responseBody = apiCallExecutor.executeObjectCall(
                        fileResourceService.getFileFromTrackedEntityAttribute(
                                trackedEntityAttributeValue.trackedEntityInstance(),
                                trackedEntityAttributeValue.trackedEntityAttribute(),
                                Dimension.MEDIUM.name()));

                File file = FileResourceUtil.saveFileFromResponse(
                        responseBody, trackedEntityAttributeValue.value(), context);

                fileResources.add(apiCallExecutor.executeObjectCall(
                        fileResourceService.getFileResource(trackedEntityAttributeValue.value()))
                        .toBuilder().path(file.getAbsolutePath()).build());
            } catch (D2Error d2Error) {
                if (trackedEntityAttributeValue.value() != null) {
                    this.store.deleteIfExists(trackedEntityAttributeValue.value());
                }
                Log.v(FileResourceCallFactory.class.getCanonicalName(), d2Error.errorDescription());
            }
        }

        handler.handleMany(fileResources, fileResource -> fileResource.toBuilder()
                .state(State.SYNCED)
                .build());
    }

    private void downloadDataValueFiles(final List<TrackedEntityDataValue> trackedEntityDataValues) {
        List<FileResource> fileResources = new ArrayList<>();

        for (TrackedEntityDataValue trackedEntityDataValue : trackedEntityDataValues) {
            try {
                ResponseBody responseBody = apiCallExecutor.executeObjectCall(
                        fileResourceService.getFileFromDataElement(
                                trackedEntityDataValue.event(),
                                trackedEntityDataValue.dataElement(),
                                Dimension.MEDIUM.name()));

                File file = FileResourceUtil.saveFileFromResponse(
                        responseBody, trackedEntityDataValue.value(), context);

                fileResources.add(apiCallExecutor.executeObjectCall(
                        fileResourceService.getFileResource(trackedEntityDataValue.value()))
                        .toBuilder().path(file.getAbsolutePath()).build());
            } catch (D2Error d2Error) {
                if (trackedEntityDataValue.value() != null) {
                    this.store.deleteIfExists(trackedEntityDataValue.value());
                }
                Log.v(FileResourceCallFactory.class.getCanonicalName(), d2Error.errorDescription());
            }
        }

        handler.handleMany(fileResources, fileResource -> fileResource.toBuilder()
                .state(State.SYNCED)
                .build());
    }
}