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
package org.hisp.dhis.android.core.fileresource.internal

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.fileresource.FileResource
import retrofit2.Call
import retrofit2.http.*

internal interface FileResourceService {

    @Multipart
    @POST(FILE_RESOURCES)
    fun uploadFile(@Part filePart: MultipartBody.Part): Call<ResponseBody>

    @GET("$FILE_RESOURCES/{$FILE_RESOURCE}")
    fun getFileResource(@Path(FILE_RESOURCE) fileResource: String): Call<FileResource>

    @GET("$TRACKED_ENTITY_INSTANCES/{$TRACKED_ENTITY_INSTANCE}/{$TRACKED_ENTITY_ATTRIBUTE}/image")
    fun getFileFromTrackedEntityAttribute(
        @Path(TRACKED_ENTITY_INSTANCE) trackedEntityInstanceUid: String,
        @Path(TRACKED_ENTITY_ATTRIBUTE) trackedEntityAttributeUid: String,
        @Query("dimension") dimension: String
    ): Call<ResponseBody>

    @GET("$EVENTS/files")
    fun getFileFromEventValue(
        @Query("eventUid") eventUid: String,
        @Query("dataElementUid") dataElementUid: String,
        @Query("dimension") dimension: String
    ): Call<ResponseBody>

    @GET("$DATA_VALUES/files")
    fun getFileFromDataValue(
        @Query("de") dataElement: String,
        @Query("pe") period: String,
        @Query("ou") organisationUnit: String,
        @Query("co") categoryOptionCombo: String,
        @Query("dimension") dimension: String
    ): Call<ResponseBody>

    companion object {
        const val FILE_RESOURCES = "fileResources"
        const val FILE_RESOURCE = "fileResource"
        const val TRACKED_ENTITY_INSTANCES = "trackedEntityInstances"
        const val TRACKED_ENTITY_INSTANCE = "trackedEntityInstance"
        const val TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute"
        const val EVENTS = "events"
        const val DATA_VALUES = "dataValues"
    }
}
