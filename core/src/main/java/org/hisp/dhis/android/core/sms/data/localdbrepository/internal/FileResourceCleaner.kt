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

package org.hisp.dhis.android.core.sms.data.localdbrepository.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.DataElementModule
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.fileresource.FileResourceModule
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule
import org.koin.core.annotation.Singleton

@Singleton
class FileResourceCleaner(
    private val dataElementModule: DataElementModule,
    private val trackedEntityModule: TrackedEntityModule,
    private val fileResourceModule: FileResourceModule,
) {
    internal fun removeFileDataValues(event: Event): Single<Event> {
        val dataValues = event.trackedEntityDataValues()
        if (dataValues.isNullOrEmpty()) {
            return Single.just(event)
        }

        val dataElementUids = dataValues.map { it.dataElement() }

        return dataElementModule.dataElements()
            .byUid().`in`(dataElementUids)
            .byValueType().`in`(ValueType.FILE_RESOURCE, ValueType.IMAGE)
            .get()
            .map { fileDataElements ->
                if (fileDataElements.isEmpty()) {
                    event
                } else {
                    val fileDataElementUids = UidsHelper.getUidsList(fileDataElements)
                    val newDataValues = dataValues.filterNot { value ->
                        fileDataElementUids.contains(value.dataElement()) &&
                            isExistingAndNotSyncedFileResource(value.value())
                    }
                    event.toBuilder().trackedEntityDataValues(newDataValues).build()
                }
            }
    }

    internal fun removeFileAttributeValues(instance: TrackedEntityInstance): Single<TrackedEntityInstance> {
        val attributeValues = instance.trackedEntityAttributeValues()
        if (attributeValues.isNullOrEmpty()) {
            return Single.just(instance)
        }

        val attributeUids = attributeValues.map { it.trackedEntityAttribute() }

        return trackedEntityModule.trackedEntityAttributes()
            .byUid().`in`(attributeUids)
            .byValueType().`in`(ValueType.FILE_RESOURCE, ValueType.IMAGE)
            .get()
            .map { fileAttributes ->
                if (fileAttributes.isEmpty()) {
                    instance
                } else {
                    val fileAttributeUids = UidsHelper.getUidsList(fileAttributes)
                    val newAttributeValues = attributeValues.filterNot { value ->
                        fileAttributeUids.contains(value.trackedEntityAttribute()) &&
                            isExistingAndNotSyncedFileResource(value.value())
                    }
                    instance.toBuilder().trackedEntityAttributeValues(newAttributeValues).build()
                }
            }
    }

    private fun isExistingAndNotSyncedFileResource(resourceUid: String?): Boolean {
        return fileResourceModule.fileResources()
            .bySyncState().notIn(State.SYNCED)
            .uid(resourceUid)
            .blockingExists()
    }
}
