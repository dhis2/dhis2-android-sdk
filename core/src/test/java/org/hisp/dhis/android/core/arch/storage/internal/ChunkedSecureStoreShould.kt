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
package org.hisp.dhis.android.core.arch.storage.internal

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class ChunkedSecureStoreShould {

    private val internalSecureStore: SecureStore = mock()

    private lateinit var chunkedSecureStore: ChunkedSecureStore

    @Before
    fun setUp() {
        chunkedSecureStore = ChunkedSecureStore(internalSecureStore)
    }

    @Test
    fun should_remove_matched_keys() {
        whenever(internalSecureStore.getAllKeys()).doReturn(
            setOf(
                "sampleKey",
                "sampleKey_[LEN]_",
                "sampleKey_[0]_",
                "sampleKey_[1]_",
                "sampleKey_[11]_",
                "sampleKey_[110]_",
                "otherKey",
                "otherKey_[LEN]_",
                "otherKey_[1]_",
                "sampleKey_[3]",
                "OTHER_CONFIG",
            ),
        )

        chunkedSecureStore.removeData("sampleKey")

        verify(internalSecureStore).getAllKeys()
        verify(internalSecureStore).removeData("sampleKey")
        verify(internalSecureStore).removeData("sampleKey_[LEN]_")
        verify(internalSecureStore).removeData("sampleKey_[0]_")
        verify(internalSecureStore).removeData("sampleKey_[1]_")
        verify(internalSecureStore).removeData("sampleKey_[11]_")
        verify(internalSecureStore).removeData("sampleKey_[110]_")
        verifyNoMoreInteractions(internalSecureStore)
    }
}
