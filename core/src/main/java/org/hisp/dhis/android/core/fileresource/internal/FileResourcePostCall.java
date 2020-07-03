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
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@Reusable
@SuppressWarnings("PMD.ExcessiveImports")
public final class FileResourcePostCall {

    private final FileResourceService fileResourceService;
    private final APICallExecutor apiCallExecutor;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IdentifiableDataObjectStore<FileResource> fileResourceStore;
    private final HandlerWithTransformer<FileResource> fileResourceHandler;
    private final Context context;


    @Inject
    FileResourcePostCall(@NonNull FileResourceService fileResourceService,
                         @NonNull APICallExecutor apiCallExecutor,
                         @NonNull TrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                         @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                         @NonNull IdentifiableDataObjectStore<FileResource> fileResourceStore,
                         @NonNull HandlerWithTransformer<FileResource> fileResourceHandler,
                         @NonNull Context context) {
        this.fileResourceService = fileResourceService;
        this.apiCallExecutor = apiCallExecutor;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.fileResourceStore = fileResourceStore;
        this.fileResourceHandler = fileResourceHandler;
        this.context = context;
    }

    public Observable<D2Progress> uploadFileResources(List<FileResource> filteredFileResources) {
        return Observable.defer(() -> {

            // if there is nothing to send, complete
            if (filteredFileResources.isEmpty()) {
                return Observable.empty();
            } else {
                D2ProgressManager progressManager = new D2ProgressManager(filteredFileResources.size());

                return Observable.create(emitter -> {
                    for (FileResource fileResource : filteredFileResources) {

                        File file = getRelatedFile(fileResource);

                        ResponseBody responseBody =
                                apiCallExecutor.executeObjectCall(fileResourceService.uploadFile(getFilePart(file)));

                        handleResponse(responseBody.string(), fileResource, file);

                        emitter.onNext(progressManager.increaseProgress(FileResource.class, true));
                    }

                    emitter.onComplete();
                });
            }
        });
    }

    private File getRelatedFile(FileResource fileResource) throws D2Error {
        return FileResourceUtil.getFile(context, fileResource);
    }

    private MultipartBody.Part getFilePart(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (type == null) {
            type = "image/*";
        }

        return MultipartBody.Part
                .createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
    }

    private void handleResponse(String responseBody, FileResource fileResource, File file) {
        try {
            FileResource downloadedFileResource = getDownloadedFileResource(responseBody);

            updateValue(fileResource, downloadedFileResource);

            File downloadedFile = updateFile(file, downloadedFileResource, context);

            updateFileResource(fileResource, downloadedFileResource, downloadedFile);

        } catch (IOException e) {
            Log.v(FileResourcePostCall.class.getCanonicalName(), e.getMessage());
        }
    }

    private FileResource getDownloadedFileResource(String responseBody)
            throws IOException {

        FileResourceResponse fileResourceResponse =
                ObjectMapperFactory.objectMapper().readValue(responseBody, FileResourceResponse.class);

        return  fileResourceResponse.response().fileResource();
    }

    private void updateValue(FileResource fileResource, FileResource downloadedFileResource) {
        if (!updateTrackedEntityAttributeValue(fileResource, downloadedFileResource)) {
            updateTrackedEntityDataValue(fileResource, downloadedFileResource);
        }
    }

    private boolean updateTrackedEntityAttributeValue(FileResource fileResource, FileResource downloadedFileResource) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE, fileResource.uid())
                .build();

        TrackedEntityAttributeValue trackedEntityAttributeValue =
                trackedEntityAttributeValueStore.selectOneWhere(whereClause);

        if (trackedEntityAttributeValue == null) {
            return false;
        } else {
            trackedEntityAttributeValueStore.updateWhere(trackedEntityAttributeValue.toBuilder()
                    .value(downloadedFileResource.uid())
                    .build());
            return true;
        }
    }

    private void updateTrackedEntityDataValue(FileResource fileResource, FileResource downloadedFileResource) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.VALUE, fileResource.uid())
                .build();

        TrackedEntityDataValue trackedEntityDataValue =
                trackedEntityDataValueStore.selectOneWhere(whereClause);

        if (trackedEntityDataValue != null) {
            trackedEntityDataValueStore.updateWhere(trackedEntityDataValue.toBuilder()
                    .value(downloadedFileResource.uid())
                    .build());
        }
    }

    private File updateFile(File file, FileResource fileResource, Context context) {
        return FileResourceUtil.renameFile(file, fileResource.uid(), context);
    }

    private void updateFileResource(FileResource fileResource, FileResource downloadedFileResource, File file) {
        fileResourceStore.delete(fileResource.uid());
        fileResourceHandler.handle(downloadedFileResource.toBuilder()
                .state(State.SYNCED)
                .path(file.getAbsolutePath())
                .build());
    }
}