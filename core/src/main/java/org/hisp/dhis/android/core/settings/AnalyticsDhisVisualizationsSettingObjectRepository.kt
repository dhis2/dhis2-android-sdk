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

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl
import org.hisp.dhis.android.core.settings.internal.AnalyticsDhisVisualizationStore
import org.hisp.dhis.android.core.settings.internal.AnalyticsSettingCall
import org.koin.core.annotation.Singleton

@Singleton(binds = [AnalyticsDhisVisualizationsSettingObjectRepository::class])
class AnalyticsDhisVisualizationsSettingObjectRepository internal constructor(
    private val analyticsDhisVisualizationStore: AnalyticsDhisVisualizationStore,
    analyticsSettingCall: AnalyticsSettingCall,
) : ReadOnlyAnyObjectWithDownloadRepositoryImpl<AnalyticsDhisVisualizationsSetting>(analyticsSettingCall),
    ReadOnlyWithDownloadObjectRepository<AnalyticsDhisVisualizationsSetting> {
    fun getByProgram(program: String?): Single<List<AnalyticsDhisVisualizationsGroup>> {
        return rxSingle { getByProgramInternal(program)!! }
    }

    fun blockingGetByProgram(program: String?): List<AnalyticsDhisVisualizationsGroup>? {
        return runBlocking { getByProgramInternal(program) }
    }

    private suspend fun getByProgramInternal(program: String?): List<AnalyticsDhisVisualizationsGroup>? {
        return generateGroups(analyticsDhisVisualizationStore.selectAll()).program()[program]
    }

    fun getByDataSet(dataSet: String?): Single<List<AnalyticsDhisVisualizationsGroup>> {
        return rxSingle { byDataSetInternal(dataSet)!! }
    }

    fun blockingByDataSet(dataSet: String?): List<AnalyticsDhisVisualizationsGroup>? {
        return runBlocking { byDataSetInternal(dataSet) }
    }

    private suspend fun byDataSetInternal(dataSet: String?): List<AnalyticsDhisVisualizationsGroup>? {
        return generateGroups(analyticsDhisVisualizationStore.selectAll()).dataSet()[dataSet]
    }

    override suspend fun getInternal(): AnalyticsDhisVisualizationsSetting {
        return generateGroups(analyticsDhisVisualizationStore.selectAll())
    }
}
