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
package org.hisp.dhis.android.core.settings

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl
import org.hisp.dhis.android.core.settings.internal.AnalyticsSettingCall
import org.koin.core.annotation.Singleton

@Singleton(binds = [AnalyticsSettingObjectRepository::class])
class AnalyticsSettingObjectRepository internal constructor(
    private val analyticsTeiSettingRepository: AnalyticsTeiSettingCollectionRepository,
    analyticsSettingCall: AnalyticsSettingCall,
    private val analyticsDhisVisualizationsSettingObjectRepository: AnalyticsDhisVisualizationsSettingObjectRepository,
) : ReadOnlyAnyObjectWithDownloadRepositoryImpl<AnalyticsSettings>(analyticsSettingCall),
    ReadOnlyWithDownloadObjectRepository<AnalyticsSettings> {
    override suspend fun getInternal(): AnalyticsSettings? {
        val analyticsTeiSettings = analyticsTeiSettingRepository.blockingGet()
        val analyticsDhisVisualizationsSetting = analyticsDhisVisualizationsSettingObjectRepository.blockingGet()

        return AnalyticsSettings.builder()
            .tei(analyticsTeiSettings)
            .dhisVisualizations(analyticsDhisVisualizationsSetting)
            .build()
    }

    fun teis(): AnalyticsTeiSettingCollectionRepository {
        return analyticsTeiSettingRepository
    }

    fun visualizationsSettings(): AnalyticsDhisVisualizationsSettingObjectRepository {
        return analyticsDhisVisualizationsSettingObjectRepository
    }
}
