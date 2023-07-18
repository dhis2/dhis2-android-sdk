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
package org.hisp.dhis.android.core.trackedentity

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.trackedentity.internal.AttributeValueFilterEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.ReservedValueSettingDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueEndpointCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceEventFilterEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceSyncEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityModuleImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeAttributeEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeEntityDIModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeService
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipEntityDIModule
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryEntityDIModule
import retrofit2.Retrofit

@Module(
    includes = [
        OwnershipEntityDIModule::class,
        ReservedValueSettingDIModule::class,
        TrackedEntityAttributeEntityDIModule::class,
        TrackedEntityAttributeReservedValueEntityDIModule::class,
        TrackedEntityAttributeValueEntityDIModule::class,
        TrackedEntityDataValueEntityDIModule::class,
        TrackedEntityInstanceEntityDIModule::class,
        TrackedEntityInstanceEventFilterEntityDIModule::class,
        TrackedEntityInstanceFilterEntityDIModule::class,
        TrackedEntityInstanceQueryEntityDIModule::class,
        TrackedEntityInstanceSyncEntityDIModule::class,
        TrackedEntityTypeEntityDIModule::class,
        TrackedEntityTypeAttributeEntityDIModule::class,
        AttributeValueFilterEntityDIModule::class
    ]
)
internal class TrackedEntityPackageDIModule {
    @Provides
    @Reusable
    fun trackedEntityTypeService(retrofit: Retrofit): TrackedEntityTypeService {
        return retrofit.create(TrackedEntityTypeService::class.java)
    }

    @Provides
    @Reusable
    fun trackedEntityAttributeService(retrofit: Retrofit): TrackedEntityAttributeService {
        return retrofit.create(TrackedEntityAttributeService::class.java)
    }

    @Provides
    @Reusable
    fun dataValueCallFactory(
        impl: TrackedEntityAttributeReservedValueEndpointCallFactory
    ): QueryCallFactory<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueQuery> {
        return impl
    }

    @Provides
    @Reusable
    fun trackedEntityInstanceFilterCall(impl: TrackedEntityInstanceFilterCall): UidsCall<TrackedEntityInstanceFilter> {
        return impl
    }

    @Provides
    @Reusable
    fun trackedEntityInstanceFilterService(retrofit: Retrofit): TrackedEntityInstanceFilterService {
        return retrofit.create(TrackedEntityInstanceFilterService::class.java)
    }

    @Provides
    @Reusable
    fun module(impl: TrackedEntityModuleImpl): TrackedEntityModule {
        return impl
    }
}
