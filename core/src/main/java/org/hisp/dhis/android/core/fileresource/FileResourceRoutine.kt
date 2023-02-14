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

package org.hisp.dhis.android.core.fileresource

import dagger.Reusable
import io.reactivex.Completable
import java.io.File
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueCollectionRepository

@Reusable
internal class FileResourceRoutine @Inject constructor(
    private val dataValueCollectionRepository: DataValueCollectionRepository,
    private val dataElementCollectionRepository: DataElementCollectionRepository,
    private val fileResourceCollectionRepository: FileResourceCollectionRepository,
    private val trackedEntityAttributeCollectionRepository: TrackedEntityAttributeCollectionRepository,
    private val trackedEntityDataValueCollectionRepository: TrackedEntityDataValueCollectionRepository,
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val trackedEntityAttributeValueCollectionRepository: TrackedEntityAttributeValueCollectionRepository
) {
    fun deleteOutdatedFileResources(after: Date? = null): Completable {
        return Completable.fromCallable {
            blockingDeleteOutdatedFileResources(after)
        }
    }

    @SuppressWarnings("MagicNumber")
    fun blockingDeleteOutdatedFileResources(after: Date? = null) {
        val dataElementsUids = dataElementCollectionRepository
            .byValueType().`in`(ValueType.FILE_RESOURCE, ValueType.IMAGE)
            .blockingGet().map(DataElement::uid)

        val trackedEntityAttributesUids = trackedEntityAttributeCollectionRepository
            .byValueType().`in`(ValueType.FILE_RESOURCE, ValueType.IMAGE)
            .blockingGet().map(TrackedEntityAttribute::uid)

        val trackedEntityDataValues = trackedEntityDataValueCollectionRepository
            .byDataElement().`in`(dataElementsUids)
            .blockingGet()

        val trackedEntityAttributeValues = trackedEntityAttributeValueCollectionRepository
            .byTrackedEntityAttribute().`in`(trackedEntityAttributesUids)
            .blockingGet()

        val dataValues = dataValueCollectionRepository
            .byDataElementUid().`in`(dataElementsUids)
            .blockingGet()

        val fileResourceUids = dataValues.map(DataValue::value) +
            trackedEntityAttributeValues.map(TrackedEntityAttributeValue::value) +
            trackedEntityDataValues.map(TrackedEntityDataValue::value)

        val calendar = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, -2)
        }
        val fileResources = fileResourceCollectionRepository
            .byUid().notIn(fileResourceUids)
            .byDomain().eq(FileResourceDomain.DATA_VALUE)
            .byLastUpdated().before(after ?: calendar.time)
            .blockingGet()

        blockingDeleteFileResources(fileResources)
    }

    private fun blockingDeleteFileResources(fileResources: List<FileResource>) {
        fileResources.forEach { fileResource ->
            fileResource.uid()?.let { uid ->
                fileResourceStore.deleteIfExists(uid)
                fileResource.path()?.let { path ->
                    deleteFile(path)
                }
            }
        }
    }

    private fun deleteFile(path: String) {
        runCatching {
            val file = File(path)
            file.delete()
        }
    }
}
