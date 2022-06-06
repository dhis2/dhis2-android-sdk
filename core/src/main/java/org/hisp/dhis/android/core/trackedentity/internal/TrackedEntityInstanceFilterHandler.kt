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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import javax.inject.Inject

@Reusable
internal class TrackedEntityInstanceFilterHandler @Inject constructor(
    trackedEntityInstanceFilterStore: IdentifiableObjectStore<TrackedEntityInstanceFilter>,
    private val trackedEntityInstanceEventFilterHandler: HandlerWithTransformer<TrackedEntityInstanceEventFilter>,
    private val attributeValueFilterHandler: HandlerWithTransformer<AttributeValueFilter>,
    private val versionManager: DHISVersionManager
) : IdentifiableHandlerImpl<TrackedEntityInstanceFilter>(trackedEntityInstanceFilterStore) {

    override fun beforeCollectionHandled(
        oCollection: Collection<TrackedEntityInstanceFilter>
    ): Collection<TrackedEntityInstanceFilter> {
        store.delete()
        return super.beforeCollectionHandled(oCollection)
    }

    override fun beforeObjectHandled(o: TrackedEntityInstanceFilter): TrackedEntityInstanceFilter {
        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_38)) {
            super.beforeObjectHandled(o)
        } else {
            super.beforeObjectHandled(
                o.toBuilder().entityQueryCriteria(
                    EntityQueryCriteria.builder()
                        .followUp(o.followUp())
                        .enrollmentStatus(o.enrollmentStatus())
                        .enrollmentCreatedDate(
                            DateFilterPeriod.builder()
                                .startBuffer(o.enrollmentCreatedPeriod()?.periodFrom())
                                .endBuffer(o.enrollmentCreatedPeriod()?.periodTo())
                                .period(RelativePeriod.TODAY)
                                .type(DatePeriodType.RELATIVE)
                                .build()
                        )
                        .build()
                ).build()
            )
        }
    }

    override fun afterObjectHandled(o: TrackedEntityInstanceFilter, action: HandleAction) {
        if (action !== HandleAction.Delete) {
            trackedEntityInstanceEventFilterHandler.handleMany(o.eventFilters()) { ef: TrackedEntityInstanceEventFilter ->
                ef.toBuilder().trackedEntityInstanceFilter(o.uid()).build()
            }
            o.entityQueryCriteria()?.attributeValueFilters()?.let {
                attributeValueFilterHandler.handleMany(it) { avf: AttributeValueFilter ->
                    avf.toBuilder().trackedEntityInstanceFilter(o.uid()).build()
                }
            }
        }
    }
}
