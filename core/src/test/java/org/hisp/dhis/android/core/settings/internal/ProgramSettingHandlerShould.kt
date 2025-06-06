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
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProgramSettingHandlerShould {
    private val programSettingStore: ProgramSettingStore = mock()
    private val programSetting: ProgramSetting = mock()

    // object to test
    private lateinit var programSettingHandler: Handler<ProgramSetting>
    private lateinit var programSettings: MutableList<ProgramSetting>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        programSettings = mutableListOf(programSetting)
        whenever(programSettingStore.updateOrInsertWhere(any())).thenReturn(HandleAction.Insert)
        programSettingHandler = ProgramSettingHandler(programSettingStore)
    }

    @Test
    fun clean_database_before_insert_collection() = runTest {
        programSettingHandler.handleMany(programSettings)
        verify(programSettingStore, times(1)).delete()
        verify(programSettingStore, times(1)).updateOrInsertWhere(programSetting)
    }

    @Test
    fun clean_database_if_empty_collection() = runTest {
        programSettingHandler.handleMany(emptyList())
        verify(programSettingStore, times(1)).delete()
        verify(programSettingStore, never()).updateOrInsertWhere(programSetting)
    }
}
