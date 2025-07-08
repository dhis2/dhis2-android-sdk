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

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.persistence.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDataDomainType
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class FileResourceHelper(
    private val dataElementStore: DataElementStore,
    private val attributeStore: TrackedEntityAttributeStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val eventStore: EventStore,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val dataValueStore: DataValueStore,
    private val fileResourceStore: FileResourceStore,
) {

    suspend fun getUploadableFileResources(): List<FileResource> {
        return fileResourceStore.getUploadableSyncStatesIncludingError()
    }

    suspend fun isPresentInDataValues(
        fileResourceUid: String,
        dataValues: Collection<TrackedEntityDataValue>?,
    ): Boolean {
        return dataValues?.any {
            fileResourceUid == it.value() && isFileDataElement(it.dataElement())
        } ?: false
    }

    suspend fun isPresentInAttributeValues(
        fileResourceUid: String,
        attributeValues: Collection<TrackedEntityAttributeValue>?,
    ): Boolean {
        return attributeValues?.any {
            fileResourceUid == it.value() && isFileAttribute(it.trackedEntityAttribute())
        } ?: false
    }

    suspend fun findAttributeFileResource(
        attributeValue: NewTrackerImporterTrackedEntityAttributeValue,
        fileResources: List<FileResource>,
    ): FileResource? {
        return fileResources.find {
            it.uid() == attributeValue.value && isFileAttribute(attributeValue.trackedEntityAttribute)
        }
    }

    suspend fun findAttributeFileResource(
        attributeValue: TrackedEntityAttributeValue,
        fileResources: List<FileResource>,
    ): FileResource? {
        return fileResources.find {
            it.uid() == attributeValue.value() && isFileAttribute(attributeValue.trackedEntityAttribute())
        }
    }

    suspend fun findDataValueFileResource(
        dataValue: NewTrackerImporterTrackedEntityDataValue,
        fileResources: List<FileResource>,
    ): FileResource? {
        return fileResources.find {
            it.uid() == dataValue.value && isFileDataElement(dataValue.dataElement)
        }
    }

    suspend fun findDataValueFileResource(
        dataValue: TrackedEntityDataValue,
        fileResources: List<FileResource>,
    ): FileResource? {
        return fileResources.find {
            it.uid() == dataValue.value() && isFileDataElement(dataValue.dataElement())
        }
    }

    suspend fun findDataValueFileResource(
        dataValue: DataValue,
        fileResource: List<FileResource>,
    ): FileResource? {
        return fileResource.find {
            it.uid() == dataValue.value() && isFileDataElement(dataValue.dataElement())
        }
    }

    suspend fun updateFileResourceStates(fileResources: List<String>, domainType: FileResourceDataDomainType) {
        fileResources.forEach { fr ->
            val relatedState = getRelatedResourceState(fr, domainType)
            val state = if (relatedState == State.SYNCED) State.SYNCED else State.TO_POST
            fileResourceStore.setSyncStateIfUploading(fr, state)
        }
    }

    private suspend fun getRelatedResourceState(fileResourceUid: String, domain: FileResourceDataDomainType): State {
        return when (domain) {
            FileResourceDataDomainType.TRACKER ->
                getRelatedEvent(fileResourceUid)?.syncState()
                    ?: getRelatedTei(fileResourceUid)?.syncState()
                    ?: State.TO_POST

            FileResourceDataDomainType.AGGREGATED ->
                getRelatedDataValue(fileResourceUid)?.syncState()
                    ?: State.TO_POST
        }
    }

    private suspend fun getRelatedEvent(fileResourceUid: String): Event? {
        val candidates = trackedEntityDataValueStore.selectWhere(
            WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.VALUE, fileResourceUid)
                .build(),
        )
        val dataValue = candidates.find { isFileDataElement(it.dataElement()) }

        return dataValue?.event()?.let { eventStore.selectByUid(it) }
    }

    private suspend fun getRelatedTei(fileResourceUid: String): TrackedEntityInstance? {
        val candidates = trackedEntityAttributeValueStore.selectWhere(
            WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE, fileResourceUid)
                .build(),
        )
        val attributeValue = candidates.find { isFileAttribute(it.trackedEntityAttribute()) }

        return attributeValue?.trackedEntityInstance()?.let { trackedEntityInstanceStore.selectByUid(it) }
    }

    private suspend fun getRelatedDataValue(fileResourceUid: String): DataValue? {
        val candidates = dataValueStore.selectWhere(
            WhereClauseBuilder()
                .appendKeyStringValue(DataValueTableInfo.Columns.VALUE, fileResourceUid)
                .build(),
        )
        return candidates.find { isFileDataElement(it.dataElement()) }
    }

    private suspend fun isFileDataElement(dataElementUid: String?): Boolean {
        return dataElementUid?.let { dataElementStore.selectByUid(it)?.valueType()?.isFile } ?: false
    }

    private suspend fun isFileAttribute(attributeUid: String?): Boolean {
        return attributeUid?.let { attributeStore.selectByUid(it)?.valueType()?.isFile } ?: false
    }
}
