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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import okhttp3.ResponseBody;

@Reusable
class FileResourceCallFactory {

    private final FileResourceService fileResourceService;
    private final Handler<FileResource> handler;
    private final APICallExecutor apiCallExecutor;
    private final Context context;

    private enum Dimension {
        SMALL, MEDIUM
    }

    @Inject
    FileResourceCallFactory(@NonNull FileResourceService fileResourceService,
                            @NonNull Handler<FileResource> handler,
                            @NonNull APICallExecutor apiCallExecutor,
                            @NonNull Context context) {
        this.fileResourceService = fileResourceService;
        this.handler = handler;
        this.apiCallExecutor = apiCallExecutor;
        this.context = context;
    }

    public Callable<Unit> create(final List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {

        return () -> {
            downloadFileResources(trackedEntityAttributeValues);
            downloadFiles(trackedEntityAttributeValues);

            return new Unit();
        };
    }

    private void downloadFileResources(final List<TrackedEntityAttributeValue> trackedEntityAttributeValues)
            throws D2Error {
        List<FileResource> fileResources = new ArrayList<>();

        for (TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues) {
            fileResources.add(apiCallExecutor.executeObjectCall(
                    fileResourceService.getFileResource(trackedEntityAttributeValue.value())));
        }

        handler.handleMany(fileResources);
    }

    private void downloadFiles(final List<TrackedEntityAttributeValue> trackedEntityAttributeValues) throws D2Error {
        List<ResponseBody> responseBodies = new ArrayList<>();

        for (TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues) {
            responseBodies.add(apiCallExecutor.executeObjectCall(fileResourceService.getFile(
                    trackedEntityAttributeValue.trackedEntityInstance(),
                    trackedEntityAttributeValue.trackedEntityAttribute(),
                    Dimension.MEDIUM.name())));
        }

        writeFiles(responseBodies);
    }

    private void writeFiles(List<ResponseBody> responseBodies) {
        // TODO generate file name
        for (ResponseBody responseBody : responseBodies) {
            writeFileToDisk(responseBody, "FileName");
        }
    }

    private boolean writeFileToDisk(ResponseBody body, String generatedFileName) {
        try {
            File futureStudioIconFile = new File(FileResourceUtil.getFileResourceDirectory(context), generatedFileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(FileResourceCallFactory.class.getCanonicalName(),
                            "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}