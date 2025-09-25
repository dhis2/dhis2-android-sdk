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
package org.hisp.dhis.android.core.option.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.option.Option
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class OptionHandlerShould {
    private val optionStore: OptionStore = mock()
    private val option: Option = mock()
    private val style: ObjectStyle = mock()
    private val optionCleaner: OptionSubCollectionCleaner = mock()

    // object to test
    private lateinit var optionHandler: OptionHandler
    private lateinit var options: List<Option>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        optionHandler = OptionHandler(optionStore, optionCleaner)
        whenever(option.uid()).thenReturn("test_option_uid")
        options = listOf(option)
        whenever(option.style()).thenReturn(style)
        whenever(optionStore.updateOrInsert(listOf(option))).thenReturn(listOf(HandleAction.Insert))
    }

    @Test
    fun clean_orphan_options() = runTest {
        optionHandler.handleMany(options)
        verify(optionCleaner).deleteNotPresent(options)
    }

    @Test
    @Suppress("UnusedPrivateMember")
    fun extend_identifiable_handler_impl() {
        val genericHandler: IdentifiableHandlerImpl<Option> = OptionHandler(
            optionStore,
            optionCleaner,
        )
    }
}
