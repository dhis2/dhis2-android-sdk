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

package org.hisp.dhis.android.core.tracker.importer.internal.interpreters

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore

@Reusable
internal class InterpreterHelper @Inject internal constructor(
    private val teiStore: TrackedEntityInstanceStore,
    private val eventStore: EventStore,
    private val programStore: ProgramStoreInterface,
    private val programStageStore: IdentifiableObjectStore<ProgramStage>,
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>,
    private val trackedEntityTypeStore: IdentifiableObjectStore<TrackedEntityType>,
    private val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>
) {

    fun trackedEntityInstance(teiUid: String): TrackedEntityInstance {
        return teiStore.selectByUid(teiUid)!!
    }

    fun programStageUid(eventUid: String): String {
        return eventStore.selectByUid(eventUid)!!.programStage()!!
    }

    fun programDisplayName(programUid: String): String {
        return programStore.selectByUid(programUid)!!.displayName()!!
    }

    fun programStageDisplayName(programStageUid: String): String {
        return programStageStore.selectByUid(programStageUid)!!.displayName()!!
    }

    fun organisationUnitDisplayName(orgUnitUid: String): String {
        return organisationUnitStore.selectByUid(orgUnitUid)!!.displayName()!!
    }

    fun trackedEntityTypeDisplayName(trackedEntityTypeUid: String): String {
        return trackedEntityTypeStore.selectByUid(trackedEntityTypeUid)!!.displayName()!!
    }

    fun trackedEntityAttributeStoreDisplayName(attributeUid: String): String {
        return trackedEntityAttributeStore.selectByUid(attributeUid)!!.displayName()!!
    }

    fun parseIdentifiableUid(classAndUid: String): String {
        val classAndUidRegex = Regex("(.*?) [(](\\w{11})[)]")
        return classAndUidRegex.find(classAndUid)!!.groupValues.last()
    }
}
