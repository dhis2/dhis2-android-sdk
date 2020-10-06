/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.event

import dagger.Reusable
import io.reactivex.Single
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import javax.inject.Inject

@Reusable
class EventService @Inject constructor(
        private val eventRepository: EventCollectionRepository,
        private val programRepository: ProgramCollectionRepository,
        private val programStageRepository: ProgramStageCollectionRepository,
        private val organisationUnitService: OrganisationUnitService,
        private val categoryOptionComboService: CategoryOptionComboService
) {

    fun blockingHasDataWriteAccess(eventUid: String): Boolean {
        val event = eventRepository.uid(eventUid).blockingGet() ?: return false

        return programRepository.uid(event.program()).blockingGet()?.access()?.data()?.write() ?: false &&
                programStageRepository.uid(event.programStage()).blockingGet()?.access()?.data()?.write() ?: false
    }

    fun hasDataWriteAccess(eventUid: String): Single<Boolean> {
        return Single.fromCallable { blockingHasDataWriteAccess(eventUid) }
    }

    fun blockingIsInOrgunitRange(event: Event): Boolean {
        return event.eventDate()?.let { eventDate ->
            event.organisationUnit()?.let { orgunitUid ->
                organisationUnitService.blockingIsDateInOrgunitRange(orgunitUid, eventDate)
            }
        } ?: true
    }

    fun isInOrgunitRange(event: Event): Single<Boolean> {
        return Single.fromCallable { blockingIsInOrgunitRange(event) }
    }

    fun blockingHasCategoryComboAccess(event: Event): Boolean {
        return event.attributeOptionCombo()?.let {
            categoryOptionComboService.blockingHasAccess(it, event.eventDate())
        } ?: true
    }

    fun hasCategoryComboAccess(event: Event): Single<Boolean> {
        return Single.fromCallable { blockingHasCategoryComboAccess(event) }
    }
}
