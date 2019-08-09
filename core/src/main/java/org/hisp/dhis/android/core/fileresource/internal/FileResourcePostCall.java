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
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Reusable
public final class FileResourcePostCall {

    private final FileResourceService fileResourceService;
    private final APICallExecutor apiCallExecutor;
    private final Context context;

    @Inject
    FileResourcePostCall(@NonNull FileResourceService fileResourceService,
                         @NonNull APICallExecutor apiCallExecutor,
                         @NonNull Context context) {
        this.fileResourceService = fileResourceService;
        this.apiCallExecutor = apiCallExecutor;
        this.context = context;
    }

    public Observable<D2Progress> uploadFileResources(List<FileResource> filteredFileResources) {
        return Observable.create(emitter -> {

            // if there is nothing to send, return null
            if (filteredFileResources.isEmpty()) {
                emitter.onComplete();
            } else {
                D2ProgressManager progressManager = new D2ProgressManager(1);

                for (FileResource fileResource : filteredFileResources) {
                    File file = getRelatedFile(fileResource);

                    apiCallExecutor.executeObjectCall(fileResourceService.uploadFile(getFilePart(file)));

                    // TODO handleWebResponse(webResponse);
                    emitter.onNext(progressManager.increaseProgress(FileResource.class, true));
                }

                emitter.onComplete();
            }
        });
    }

    private File getRelatedFile(FileResource fileResource) throws D2Error {
        return FileResourceUtil.getFile(context, fileResource.uid());
    }

    private MultipartBody.Part getFilePart(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if(type == null)
            type = "image/*";
        return MultipartBody.Part
                .createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
    }
}