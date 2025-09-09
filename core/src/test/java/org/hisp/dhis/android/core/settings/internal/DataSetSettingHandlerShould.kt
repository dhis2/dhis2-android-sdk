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

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DataSetSettingHandlerShould {
    private val dataSetSettingStore: DataSetSettingStore = mock()
    private val dataSetSetting: DataSetSetting = mock()

    // object to test
    private lateinit var dataSetSettingHandler: Handler<DataSetSetting>
    private lateinit var dataSetSettings: MutableList<DataSetSetting>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        dataSetSettings = mutableListOf(dataSetSetting)
        whenever(
            dataSetSettingStore.updateOrInsert(any<List<DataSetSetting>>()),
        ).thenReturn(listOf(HandleAction.Insert))
        dataSetSettingHandler = DataSetSettingHandler(dataSetSettingStore)
    }

    @Test
    fun clean_database_before_insert_collection() = runTest {
        dataSetSettingHandler.handleMany(dataSetSettings)
        verify(dataSetSettingStore, times(1)).delete()
        verify(dataSetSettingStore, times(1)).updateOrInsert(listOf(dataSetSetting))
    }

    @Test
    fun clean_database_if_empty_collection() = runTest {
        dataSetSettingHandler.handleMany(emptyList())
        verify(dataSetSettingStore, times(1)).delete()
        verify(dataSetSettingStore, never()).updateOrInsert(listOf(dataSetSetting))
    }
}
