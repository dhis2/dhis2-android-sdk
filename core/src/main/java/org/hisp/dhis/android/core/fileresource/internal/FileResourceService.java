/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.fileresource.internal;

import org.hisp.dhis.android.core.fileresource.FileResource;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface FileResourceService {
    String FILE_RESOURCES = "fileResources";
    String FILE_RESOURCE = "fileResource";
    String TRACKED_ENTITY_INSTANCES = "trackedEntityInstances";
    String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    String EVENTS = "events";

    @Multipart
    @POST(FILE_RESOURCES)
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part filePart);

    @GET(FILE_RESOURCES + "/{" + FILE_RESOURCE + "}")
    Call<FileResource> getFileResource(@Path(FILE_RESOURCE) String fileResource);

    @GET(TRACKED_ENTITY_INSTANCES + "/{" + TRACKED_ENTITY_INSTANCE + "}/{" + TRACKED_ENTITY_ATTRIBUTE + "}/image")
    Call<ResponseBody> getFileFromTrackedEntityAttribute(
            @Path(TRACKED_ENTITY_INSTANCE) String trackedEntityInstanceUid,
            @Path(TRACKED_ENTITY_ATTRIBUTE) String trackedEntityAttributeUid,
            @Query("dimension") String dimension);

    @GET(EVENTS + "/files")
    Call<ResponseBody> getFileFromDataElement(
            @Query("eventUid") String eventUid,
            @Query("dataElementUid") String dataElementUid,
            @Query("dimension") String dimension);
}