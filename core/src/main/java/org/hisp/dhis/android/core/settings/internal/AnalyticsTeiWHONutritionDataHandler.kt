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
package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.arch.handlers.internal.ChildElementHandlerImpl
import org.hisp.dhis.android.core.settings.AnalyticsTeiDataElement
import org.hisp.dhis.android.core.settings.AnalyticsTeiIndicator
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionData
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionItem
import org.hisp.dhis.android.core.settings.WHONutritionComponent
import org.koin.core.annotation.Singleton

@Singleton
internal class AnalyticsTeiWHONutritionDataHandler(
    store: AnalyticsTeiWHONutritionDataStore,
    private val teiDataElementHandler: AnalyticsTeiDataElementHandler,
    private val teiIndicatorHandler: AnalyticsTeiIndicatorHandler,
) : ChildElementHandlerImpl<AnalyticsTeiWHONutritionData>(store) {

    override suspend fun afterObjectHandled(o: AnalyticsTeiWHONutritionData) {
        val dataElementList =
            getDataElements(o.x(), WHONutritionComponent.X) + getDataElements(o.y(), WHONutritionComponent.Y)

        val indicatorList =
            getIndicators(o.x(), WHONutritionComponent.X) + getIndicators(o.y(), WHONutritionComponent.Y)

        teiDataElementHandler.handleMany(o.teiSetting()!!, dataElementList) {
            it.toBuilder().teiSetting(o.teiSetting()).build()
        }

        teiIndicatorHandler.handleMany(o.teiSetting()!!, indicatorList) {
            it.toBuilder().teiSetting(o.teiSetting()).build()
        }
    }

    private fun getDataElements(
        item: AnalyticsTeiWHONutritionItem?,
        whoComponent: WHONutritionComponent,
    ): List<AnalyticsTeiDataElement> {
        return item?.dataElements()?.map { it.toBuilder().whoComponent(whoComponent).build() } ?: emptyList()
    }

    private fun getIndicators(
        item: AnalyticsTeiWHONutritionItem?,
        whoComponent: WHONutritionComponent,
    ): List<AnalyticsTeiIndicator> {
        return item?.indicators()?.map { it.toBuilder().whoComponent(whoComponent).build() } ?: emptyList()
    }
}
