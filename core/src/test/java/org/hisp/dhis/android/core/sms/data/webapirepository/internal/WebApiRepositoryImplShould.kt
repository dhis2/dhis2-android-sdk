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

package org.hisp.dhis.android.core.sms.data.webapirepository.internal

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WebApiRepositoryImplShould {

    private val apiService: ApiService = mock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val defalutMetadataConfig = GetMetadataIdsConfig()

    private lateinit var testWebRepository: WebApiRepositoryImpl

    @Before
    fun init() {
        testWebRepository = WebApiRepositoryImpl(apiService, dhisVersionManager)
    }

    @Test
    fun `Include users query if version lower than 2_35`() {
        whenever(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35)) doReturn false

        testWebRepository.metadataCall(defalutMetadataConfig)
        verify(apiService).getMetadataIds(
            notNull(), notNull(), notNull(), notNull(), notNull(),
            notNull(), notNull()
        )
    }

    @Test
    fun `Exclude users if version greater or equal to 2_35`() {
        whenever(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35)) doReturn true

        testWebRepository.metadataCall(defalutMetadataConfig)
        verify(apiService).getMetadataIds(
            notNull(), notNull(), notNull(), isNull(), notNull(),
            notNull(), notNull()
        )
    }
}
