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

package org.hisp.dhis.android.core.analytics

import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsRepositoryParams
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsVisualizationsRepositoryParams
import org.hisp.dhis.android.core.analytics.linelist.EventLineListParams
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListParams
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@ComponentScan
internal class AnalyticsDIModule {
    @Singleton
    fun eventLineListParams(): EventLineListParams {
        return EventLineListParams()
    }

    @Singleton
    fun emptyAnalyticsParams(): AnalyticsRepositoryParams {
        return AnalyticsRepositoryParams(listOf(), listOf())
    }

    @Singleton
    fun emptyAnalyticsVisualizationsParam(): AnalyticsVisualizationsRepositoryParams {
        return AnalyticsVisualizationsRepositoryParams(null, null, null)
    }

    @Singleton
    fun emptyTrackerLineListParams(): TrackerLineListParams {
        return TrackerLineListParams(null, null, null, null, null, emptyList(), emptyList(), emptyList())
    }
}
