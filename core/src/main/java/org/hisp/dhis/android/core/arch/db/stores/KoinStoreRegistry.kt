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

package org.hisp.dhis.android.core.arch.db.stores

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteStore
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.koin.core.annotation.Singleton
import kotlin.reflect.KClass

@Singleton
internal class KoinStoreRegistry(
    private val koin: org.koin.core.Koin,
) : StoreRegistry {

    private val mappings: Map<KClass<out CoreObject>, KClass<out ObjectStore<*>>> = mapOf(
        DataValue::class to DataValueStore::class,
        Enrollment::class to EnrollmentStore::class,
        Event::class to EventStore::class,
        Relationship::class to RelationshipStore::class,
        TrackedEntityInstance::class to TrackedEntityInstanceStore::class,
        TrackedEntityAttributeValue::class to TrackedEntityAttributeValueStore::class,
        TrackedEntityDataValue::class to TrackedEntityDataValueStore::class,
        TrackerImportConflict::class to TrackerImportConflictStore::class,
        Note::class to NoteStore::class,
        DataSetCompleteRegistration::class to DataSetCompleteRegistrationStore::class,
        ProgramOwner::class to ProgramOwnerStore::class,
        FileResource::class to FileResourceStore::class,
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : CoreObject> getStoreFor(type: KClass<T>): ObjectStore<T>? {
        val storeInterfaceKClass = mappings[type]
            ?: throw IllegalArgumentException(
                "No store interface mapping found for ${type.simpleName}. " +
                    "Ensure it's registered in storeMappings."
            )

        return koin.get(storeInterfaceKClass) as? ObjectStore<T>
            ?: throw IllegalStateException(
                "Failed to retrieve or cast store for ${storeInterfaceKClass.simpleName} " +
                    "to ObjectStore<${type.simpleName}>"
            )
    }
}
