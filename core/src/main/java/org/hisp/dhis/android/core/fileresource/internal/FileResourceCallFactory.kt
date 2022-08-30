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

import android.content.Context
import android.util.Log
import dagger.Reusable
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue

@Reusable
internal class FileResourceCallFactory @Inject constructor(
    private val fileResourceService: FileResourceService,
    private val handler: HandlerWithTransformer<FileResource>,
    private val store: IdentifiableDataObjectStore<FileResource>,
    private val apiCallExecutor: APICallExecutor,
    private val context: Context
) {
    private enum class Dimension {
        SMALL, MEDIUM
    }

    fun create(
        trackedEntityAttributeValues: List<TrackedEntityAttributeValue>,
        trackedEntityDataValues: List<TrackedEntityDataValue>
    ): Callable<Unit> {
        return Callable {
            downloadAttributeValueFiles(trackedEntityAttributeValues)
            downloadDataValueFiles(trackedEntityDataValues)
            Unit()
        }
    }

    private fun downloadAttributeValueFiles(trackedEntityAttributeValues: List<TrackedEntityAttributeValue>) {
        val fileResources: MutableList<FileResource> = ArrayList()
        for (trackedEntityAttributeValue in trackedEntityAttributeValues) {
            try {
                val responseBody = apiCallExecutor.executeObjectCall(
                    fileResourceService.getFileFromTrackedEntityAttribute(
                        trackedEntityAttributeValue.trackedEntityInstance()!!,
                        trackedEntityAttributeValue.trackedEntityAttribute()!!,
                        Dimension.MEDIUM.name
                    )
                )
                val file = FileResourceUtil.saveFileFromResponse(
                    responseBody, trackedEntityAttributeValue.value()!!, context
                )
                fileResources.add(
                    apiCallExecutor.executeObjectCall(
                        fileResourceService.getFileResource(trackedEntityAttributeValue.value()!!)
                    )
                        .toBuilder().path(file.absolutePath).build()
                )
            } catch (d2Error: D2Error) {
                if (trackedEntityAttributeValue.value() != null) {
                    store.deleteIfExists(trackedEntityAttributeValue.value()!!)
                }
                Log.v(FileResourceCallFactory::class.java.canonicalName, d2Error.errorDescription())
            }
        }
        handler.handleMany(fileResources) { fileResource: FileResource ->
            fileResource.toBuilder()
                .syncState(State.SYNCED)
                .build()
        }
    }

    private fun downloadDataValueFiles(trackedEntityDataValues: List<TrackedEntityDataValue>) {
        val fileResources: MutableList<FileResource> = ArrayList()
        for (trackedEntityDataValue in trackedEntityDataValues) {
            try {
                val responseBody = apiCallExecutor.executeObjectCall(
                    fileResourceService.getFileFromDataElement(
                        trackedEntityDataValue.event()!!,
                        trackedEntityDataValue.dataElement()!!,
                        Dimension.MEDIUM.name
                    )
                )
                val file = FileResourceUtil.saveFileFromResponse(
                    responseBody, trackedEntityDataValue.value()!!, context
                )
                fileResources.add(
                    apiCallExecutor.executeObjectCall(
                        fileResourceService.getFileResource(trackedEntityDataValue.value()!!)
                    )
                        .toBuilder().path(file.absolutePath).build()
                )
            } catch (d2Error: D2Error) {
                if (trackedEntityDataValue.value() != null) {
                    store.deleteIfExists(trackedEntityDataValue.value()!!)
                }
                Log.v(FileResourceCallFactory::class.java.canonicalName, d2Error.errorDescription())
            }
        }
        handler.handleMany(fileResources) { fileResource: FileResource ->
            fileResource.toBuilder()
                .syncState(State.SYNCED)
                .build()
        }
    }
}
