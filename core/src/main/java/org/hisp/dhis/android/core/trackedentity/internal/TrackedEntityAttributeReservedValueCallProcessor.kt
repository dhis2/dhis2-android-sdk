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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor.Companion.create
import org.hisp.dhis.android.core.arch.call.processors.internal.CallProcessor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue
import java.util.Date

internal class TrackedEntityAttributeReservedValueCallProcessor(
    private val databaseAdapter: DatabaseAdapter,
    private val handler: Handler<TrackedEntityAttributeReservedValue>,
    organisationUnit: OrganisationUnit?,
    private val pattern: String
) : CallProcessor<TrackedEntityAttributeReservedValue> {
    private val organisationUnitUid = if (organisationUnit == null) null else organisationUnit.uid()
    private val temporalValidityDate: Date?

    init {
        this.temporalValidityDate = fillTemporalValidityDate(pattern)
    }

    @Throws(D2Error::class)
    override fun process(objectList: List<TrackedEntityAttributeReservedValue>) {
        if (objectList.isNotEmpty()) {
            create(databaseAdapter).executeD2CallTransactionally<Any?>({
                for (trackedEntityAttributeReservedValue in objectList) {
                    handler.handle(
                        trackedEntityAttributeReservedValue.toBuilder()
                            .pattern(pattern)
                            .organisationUnit(organisationUnitUid)
                            .temporalValidityDate(temporalValidityDate)
                            .build()
                    )
                }
                null
            })
        }
    }

    private fun fillTemporalValidityDate(pattern: String): Date? {
        try {
            return Date(
                TrackedEntityAttributeReservedValueValidatorHelper()
                    .getExpiryDateCode(pattern).toEpochMilliseconds()
            )
        } catch (e: IllegalStateException) {
            return null
        }
    }
}