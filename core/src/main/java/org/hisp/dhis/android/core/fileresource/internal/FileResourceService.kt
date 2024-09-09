/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import io.ktor.client.request.forms.MultiPartFormDataContent
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.fileresource.FileResource
import org.koin.core.annotation.Singleton

@Singleton
internal class FileResourceService(private val client: HttpServiceClient) {

    suspend fun uploadFile(filePart: MultiPartFormDataContent): ByteArray {
        return client.post {
            url(FILE_RESOURCES)
            body(filePart)
        }
    }

    suspend fun getFileResource(fileResource: String): FileResource {
        return client.get {
            url("$FILE_RESOURCES/$fileResource")
        }
    }

    suspend fun getFileResources(
        fields: Fields<FileResource>,
        fileResources: Filter<FileResource>,
        paging: Boolean,
    ): Payload<FileResource> {
        return client.get {
            url(FILE_RESOURCES)
            parameters {
                fields(fields)
                filter(fileResources)
                paging(paging)
            }
        }
    }

    suspend fun getImageFromTrackedEntityAttribute(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUid: String,
        dimension: String,
    ): ByteArray {
        return client.get {
            url("$TRACKED_ENTITY_INSTANCES/$trackedEntityInstanceUid/$trackedEntityAttributeUid/image")
            parameters {
                attribute(dimension to dimension)
            }
        }
    }

    suspend fun getFileFromTrackedEntityAttribute(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUid: String,
    ): ByteArray {
        return client.get {
            url("$TRACKED_ENTITY_INSTANCES/$trackedEntityInstanceUid/$trackedEntityAttributeUid/file")
        }
    }

    suspend fun getFileFromEventValue(
        eventUid: String,
        dataElementUid: String,
        dimension: String,
    ): ByteArray {
        return client.get {
            url("$EVENTS/files")
            parameters {
                attribute("eventUid" to eventUid)
                attribute("dataElementUid" to dataElementUid)
                attribute("dimension" to dimension)
            }
        }
    }

    suspend fun getCustomIcon(
        customIconHref: String,
    ): ByteArray {
        return client.get {
            absoluteUrl(customIconHref, false)
        }
    }

    suspend fun getFileFromDataValue(
        dataElement: String,
        period: String,
        organisationUnit: String,
        categoryOptionCombo: String,
        dimension: String,
    ): ByteArray {
        return client.get {
            url("$DATA_VALUES/files")
            parameters {
                attribute("de" to dataElement)
                attribute("pe" to period)
                attribute("ou" to organisationUnit)
                attribute("co" to categoryOptionCombo)
                attribute("dimension" to dimension)
            }
        }
    }

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
