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

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.settings.*
import org.junit.Before
import org.junit.Test

class AnalyticsTeiSettingHandlerShould {

    private val analyticsTeiSettingStore: ObjectWithoutUidStore<AnalyticsTeiSetting> = mock()

    private val analyticsTeiSetting: AnalyticsTeiSetting = mock()

    private lateinit var analyticsTeiSettingHandler: Handler<AnalyticsTeiSetting>

    private val teiDataElementHandler: LinkHandler<AnalyticsTeiDataElement, AnalyticsTeiDataElement> = mock()
    private val teiIndicatorHandler: LinkHandler<AnalyticsTeiIndicator, AnalyticsTeiIndicator> = mock()
    private val teiAttributeHandler: LinkHandler<AnalyticsTeiAttribute, AnalyticsTeiAttribute> = mock()
    private val whoDataHandler: LinkHandler<AnalyticsTeiWHONutritionData, AnalyticsTeiWHONutritionData> = mock()

    private val analyticsTeiSettingList: List<AnalyticsTeiSetting> = listOf(analyticsTeiSetting)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        whenever(analyticsTeiSettingStore.updateOrInsertWhere(any())) doReturn HandleAction.Insert
        whenever(analyticsTeiSetting.uid()) doReturn "tei_setting_uid"

        analyticsTeiSettingHandler = AnalyticsTeiSettingHandler(
            analyticsTeiSettingStore, teiDataElementHandler,
            teiIndicatorHandler, teiAttributeHandler, whoDataHandler
        )
    }

    @Test
    fun clean_database_before_insert_collection() {
        analyticsTeiSettingHandler.handleMany(analyticsTeiSettingList)
        verify(analyticsTeiSettingStore).delete()
        verify(analyticsTeiSettingStore).updateOrInsertWhere(analyticsTeiSetting)
    }

    @Test
    fun clean_database_if_empty_collection() {
        analyticsTeiSettingHandler.handleMany(emptyList())
        verify(analyticsTeiSettingStore).delete()
        verify(analyticsTeiSettingStore, never()).updateOrInsertWhere(analyticsTeiSetting)
    }

    @Test
    fun call_data_handlers() {
        analyticsTeiSettingHandler.handleMany(analyticsTeiSettingList)

        verify(teiDataElementHandler).handleMany(any(), any(), any())
        verify(teiIndicatorHandler).handleMany(any(), any(), any())
        verify(teiAttributeHandler).handleMany(any(), any(), any())
        verify(whoDataHandler).handleMany(any(), any(), any())
    }
}
