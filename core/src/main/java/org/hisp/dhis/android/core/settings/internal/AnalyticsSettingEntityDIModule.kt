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
package org.hisp.dhis.android.core.settings.internal

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandlerImpl
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.settings.AnalyticsDhisVisualization
import org.hisp.dhis.android.core.settings.AnalyticsTeiAttribute
import org.hisp.dhis.android.core.settings.AnalyticsTeiDataElement
import org.hisp.dhis.android.core.settings.AnalyticsTeiIndicator
import org.hisp.dhis.android.core.settings.AnalyticsTeiSetting
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionData

@Module
@Suppress("TooManyFunctions")
internal class AnalyticsSettingEntityDIModule {

    @Provides
    @Reusable
    fun analyticsSettingStore(databaseAdapter: DatabaseAdapter): ObjectWithoutUidStore<AnalyticsTeiSetting> {
        return AnalyticsTeiSettingStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun analyticsDhisVisualizationStore(
        databaseAdapter: DatabaseAdapter
    ): ObjectWithoutUidStore<AnalyticsDhisVisualization> {
        return AnalyticsDhisVisualizationStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun analyticsTeiDataElementStore(databaseAdapter: DatabaseAdapter): LinkStore<AnalyticsTeiDataElement> {
        return AnalyticsTeiDataElementStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun analyticsTeiIndicatorStore(databaseAdapter: DatabaseAdapter): LinkStore<AnalyticsTeiIndicator> {
        return AnalyticsTeiIndicatorStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun analyticsTeiAttributeStore(databaseAdapter: DatabaseAdapter): LinkStore<AnalyticsTeiAttribute> {
        return AnalyticsTeiAttributeStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun analyticsTeiWHONutritionDataStore(databaseAdapter: DatabaseAdapter): LinkStore<AnalyticsTeiWHONutritionData> {
        return AnalyticsTeiWHONutritionDataStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun analyticsTeiSettingHandler(impl: AnalyticsTeiSettingHandler): Handler<AnalyticsTeiSetting> {
        return impl
    }

    @Provides
    @Reusable
    fun analyticsDhisVisualizationsHandler(
        impl: AnalyticsDhisVisualizationSettingHandler
    ): Handler<AnalyticsDhisVisualization> {
        return impl
    }

    @Provides
    @Reusable
    fun analyticsTeiDataElementHandler(
        store: LinkStore<AnalyticsTeiDataElement>
    ): LinkHandler<AnalyticsTeiDataElement, AnalyticsTeiDataElement> {
        return LinkHandlerImpl(store)
    }

    @Provides
    @Reusable
    fun analyticsTeiIndicatorHandler(
        store: LinkStore<AnalyticsTeiIndicator>
    ): LinkHandler<AnalyticsTeiIndicator, AnalyticsTeiIndicator> {
        return LinkHandlerImpl(store)
    }

    @Provides
    @Reusable
    fun analyticsTeiAttributeHandler(
        store: LinkStore<AnalyticsTeiAttribute>
    ): LinkHandler<AnalyticsTeiAttribute, AnalyticsTeiAttribute> {
        return LinkHandlerImpl(store)
    }

    @Provides
    @Reusable
    fun analyticsTeiWhoNutritionDataHandler(
        impl: AnalyticsTeiWHONutritionDataHandler
    ): LinkHandler<AnalyticsTeiWHONutritionData, AnalyticsTeiWHONutritionData> {
        return impl
    }

    @Provides
    @Reusable
    fun teiChildrenAppenders(dataChildrenAppender: AnalyticsTeiDataChildrenAppender):
        Map<String, ChildrenAppender<AnalyticsTeiSetting>> {
            return mapOf(AnalyticsTeiDataChildrenAppender.KEY to dataChildrenAppender)
        }
}
