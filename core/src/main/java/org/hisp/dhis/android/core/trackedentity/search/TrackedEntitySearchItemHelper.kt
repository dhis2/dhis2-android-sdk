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

package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType

object TrackedEntitySearchItemHelper {
    internal fun from(
        i: TrackedEntityInstance,
        attributes: List<SimpleTrackedEntityAttribute>,
        type: TrackedEntityType,
    ): TrackedEntitySearchItem {
        return TrackedEntitySearchItem(
            uid = i.uid(),
            created = i.created(),
            lastUpdated = i.lastUpdated(),
            createdAtClient = i.createdAtClient(),
            lastUpdatedAtClient = i.lastUpdatedAtClient(),
            organisationUnit = i.organisationUnit(),
            geometry = i.geometry(),
            attributeValues = fromAttributes(attributes, i.trackedEntityAttributeValues()),
            syncState = i.syncState(),
            aggregatedSyncState = i.aggregatedSyncState(),
            deleted = i.deleted() ?: false,
            isOnline = i.id() != null,
            type = type,
        )
    }

    fun toTrackedEntityInstance(i: TrackedEntitySearchItem): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .uid(i.uid)
            .created(i.created)
            .lastUpdated(i.lastUpdated)
            .createdAtClient(i.createdAtClient)
            .lastUpdatedAtClient(i.lastUpdatedAtClient)
            .organisationUnit(i.organisationUnit)
            .trackedEntityType(i.type.uid())
            .geometry(i.geometry)
            .trackedEntityAttributeValues(i.attributeValues?.map(::toTrackedEntityAttributeValue))
            .syncState(i.syncState)
            .aggregatedSyncState(i.aggregatedSyncState)
            .build()
    }

    private fun fromAttributes(
        attributes: List<SimpleTrackedEntityAttribute>,
        attributeValues: List<TrackedEntityAttributeValue>?,
    ): List<TrackedEntitySearchItemAttribute> {
        return attributes.mapNotNull { attribute ->
            from(attribute, attributeValues)
        }
    }

    private fun from(
        attribute: SimpleTrackedEntityAttribute,
        attributeValues: List<TrackedEntityAttributeValue>?,
    ): TrackedEntitySearchItemAttribute? {
        return attributeValues?.find { it.trackedEntityAttribute() == attribute.attribute }?.let { value ->
            TrackedEntitySearchItemAttribute(
                attribute = attribute.attribute,
                displayName = attribute.displayName,
                displayFormName = attribute.displayFormName ?: attribute.displayName,
                value = value.value(),
                created = value.created(),
                lastUpdated = value.lastUpdated(),
                valueType = attribute.valueType,
                displayInList = attribute.displayInList,
                optionSet = attribute.optionSet,
            )
        }
    }

    private fun toTrackedEntityAttributeValue(a: TrackedEntitySearchItemAttribute): TrackedEntityAttributeValue {
        return TrackedEntityAttributeValue.builder()
            .trackedEntityAttribute(a.attribute)
            .value(a.value)
            .created(a.created)
            .lastUpdated(a.lastUpdated)
            .build()
    }
}
