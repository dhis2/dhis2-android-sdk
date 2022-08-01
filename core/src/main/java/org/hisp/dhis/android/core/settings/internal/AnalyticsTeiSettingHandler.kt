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

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl
import org.hisp.dhis.android.core.settings.*

@Reusable
internal class AnalyticsTeiSettingHandler @Inject constructor(
    store: ObjectWithoutUidStore<AnalyticsTeiSetting>,
    private val teiDataElementHandler: LinkHandler<AnalyticsTeiDataElement, AnalyticsTeiDataElement>,
    private val teiIndicatorHandler: LinkHandler<AnalyticsTeiIndicator, AnalyticsTeiIndicator>,
    private val teiAttributeHandler: LinkHandler<AnalyticsTeiAttribute, AnalyticsTeiAttribute>,
    private val whoNutritionDataHandler: LinkHandler<AnalyticsTeiWHONutritionData, AnalyticsTeiWHONutritionData>
) : ObjectWithoutUidHandlerImpl<AnalyticsTeiSetting>(store) {

    override fun beforeCollectionHandled(
        oCollection: Collection<AnalyticsTeiSetting>
    ): Collection<AnalyticsTeiSetting> {
        store.delete()
        return oCollection
    }

    override fun afterObjectHandled(o: AnalyticsTeiSetting, action: HandleAction) {
        teiDataElementHandler.handleMany(o.uid(), o.data()?.dataElements() ?: emptyList()) { de ->
            de.toBuilder().teiSetting(o.uid()).build()
        }

        teiIndicatorHandler.handleMany(o.uid(), o.data()?.indicators() ?: emptyList()) { ind ->
            ind.toBuilder().teiSetting(o.uid()).build()
        }

        teiAttributeHandler.handleMany(o.uid(), o.data()?.attributes() ?: emptyList()) { att ->
            att.toBuilder().teiSetting(o.uid()).build()
        }

        whoNutritionDataHandler.handleMany(o.uid(), listOfNotNull(o.whoNutritionData())) { whoData ->
            whoData.toBuilder().teiSetting(o.uid()).build()
        }
    }
}
