/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.fileresource

import io.ktor.client.request.forms.MultiPartFormDataContent
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.call.queries.internal.UidsQuery
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceNetworkHandler
import org.hisp.dhis.android.core.fileresource.internal.MissingTrackerAttributeValue
import org.hisp.dhis.android.core.icon.CustomIcon
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.koin.core.annotation.Singleton

@Singleton
internal class FileResourceNetworkHandlerImpl(
    httpServiceClient: HttpServiceClientKotlinx,
) : FileResourceNetworkHandler {
    private val service = FileResourceService(httpServiceClient)
    override suspend fun uploadFile(filePart: MultiPartFormDataContent): FileResource {
        val apiResponse = service.uploadFile(filePart)
        return apiResponse.response.fileResource.toDomain()
    }

    override suspend fun getFileResource(fileResource: String): FileResource {
        val apiResponse = service.getFileResource(fileResource)
        return apiResponse.toDomain()
    }

    override suspend fun getFileResources(
        query: UidsQuery,
    ): Payload<FileResource> {
        val apiPayload = service.getFileResources(
            FileResourceFields.allFields,
            FileResourceFields.uid.`in`(query.uids),
            false,
        )
        return apiPayload.mapItems(FileResourceDTO::toDomain)
    }

    override suspend fun getImageFromTrackedEntityAttribute(
        v: MissingTrackerAttributeValue,
        dimension: String,
    ): ByteArray {
        return service.getImageFromTrackedEntityAttribute(
            v.value.trackedEntityInstance()!!,
            v.value.trackedEntityAttribute()!!,
            dimension,
        )
    }

    override suspend fun getFileFromTrackedEntityAttribute(
        v: MissingTrackerAttributeValue,
    ): ByteArray {
        return service.getFileFromTrackedEntityAttribute(
            v.value.trackedEntityInstance()!!,
            v.value.trackedEntityAttribute()!!,
        )
    }

    override suspend fun getFileFromEventValue(
        v: TrackedEntityDataValue,
        dimension: String,
    ): ByteArray {
        return service.getFileFromEventValue(
            v.event()!!,
            v.dataElement()!!,
            dimension,
        )
    }

    override suspend fun getCustomIcon(
        v: CustomIcon,
    ): ByteArray {
        return service.getCustomIcon(v.href())
    }

    override suspend fun getFileFromDataValue(v: DataValue, dimension: String): ByteArray {
        return service.getFileFromDataValue(
            v.dataElement()!!,
            v.period()!!,
            v.organisationUnit()!!,
            v.attributeOptionCombo()!!,
            dimension,
        )
    }
}
