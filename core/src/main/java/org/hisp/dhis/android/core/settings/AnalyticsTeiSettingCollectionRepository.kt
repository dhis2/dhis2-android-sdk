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

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.settings.internal.AnalyticsTeiDataChildrenAppender
import org.hisp.dhis.android.core.settings.internal.AnalyticsTeiSettingStore
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiSettingTableInfo
import org.koin.core.annotation.Singleton

@Singleton
class AnalyticsTeiSettingCollectionRepository internal constructor(
    store: AnalyticsTeiSettingStore,
    scope: RepositoryScope,
) : ReadOnlyCollectionRepositoryImpl<AnalyticsTeiSetting, AnalyticsTeiSettingCollectionRepository>(
    store,
    childrenAppenders,
    scope.toBuilder().children(scope.children().withChild(AnalyticsTeiDataChildrenAppender.KEY)).build(),
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        AnalyticsTeiSettingCollectionRepository(
            store,
            s,
        )
    },
) {
    fun byUid(): StringFilterConnector<AnalyticsTeiSettingCollectionRepository> {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.UID)
    }

    fun byName(): StringFilterConnector<AnalyticsTeiSettingCollectionRepository> {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.NAME)
    }

    fun byShortName(): StringFilterConnector<AnalyticsTeiSettingCollectionRepository> {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.SHORT_NAME)
    }

    fun byProgram(): StringFilterConnector<AnalyticsTeiSettingCollectionRepository> {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.PROGRAM)
    }

    fun byProgramStage(): StringFilterConnector<AnalyticsTeiSettingCollectionRepository> {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byPeriod(): EnumFilterConnector<AnalyticsTeiSettingCollectionRepository, PeriodType> {
        return cf.enumC(AnalyticsTeiSettingTableInfo.Columns.PERIOD)
    }

    fun byType(): EnumFilterConnector<AnalyticsTeiSettingCollectionRepository, ChartType> {
        return cf.enumC(AnalyticsTeiSettingTableInfo.Columns.TYPE)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<AnalyticsTeiSetting> = mapOf(
            AnalyticsTeiDataChildrenAppender.KEY to AnalyticsTeiDataChildrenAppender::create,
        )
    }
}
