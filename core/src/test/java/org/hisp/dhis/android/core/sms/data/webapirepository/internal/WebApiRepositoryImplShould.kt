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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.hisp.dhis.android.network.metadata.MetadataIdsDTO
import org.hisp.dhis.android.network.metadata.MetadataNetworkHandlerImpl
import org.hisp.dhis.android.network.metadata.MetadataService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class WebApiRepositoryImplShould {

    private val dhisVersionManager: DHISVersionManager = mock()
    private val metadataIdsDTO: MetadataIdsDTO = mock()
    private val metadataService: MetadataService = mock()
    private val clientKotlinx: HttpServiceClientKotlinx = mock()
    private val defaultMetadataConfig = GetMetadataIdsConfig()
    private val ID_FIELD = "id"

    private lateinit var metadataNetworkHandler: MetadataNetworkHandlerImpl
    private lateinit var testWebRepository: WebApiRepositoryImpl

    @Before
    fun init() {
        metadataNetworkHandler = MetadataNetworkHandlerImpl(clientKotlinx, dhisVersionManager)
        testWebRepository = WebApiRepositoryImpl(metadataNetworkHandler)

        val networkHandlerImpl = metadataNetworkHandler
        val serviceField = networkHandlerImpl.javaClass.getDeclaredField("service")
        serviceField.isAccessible = true
        serviceField.set(networkHandlerImpl, metadataService)

        whenever(metadataIdsDTO.toDomain()).thenReturn(MetadataIds())
    }

    @Test
    fun `Include users query if version lower than 2_35`() = runTest {
        whenever(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35)) doReturn false
        whenever(metadataService.getMetadataFields(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(metadataIdsDTO)

        testWebRepository.getMetadataIds(defaultMetadataConfig)
        verify(metadataService).getMetadataFields(
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
        )
        verifyNoMoreInteractions(metadataService)
    }

    @Test
    fun `Exclude users if version greater or equal to 2_35`() = runTest {
        whenever(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35)) doReturn true
        whenever(metadataService.getMetadataFields(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(metadataIdsDTO)

        testWebRepository.getMetadataIds(defaultMetadataConfig)
        verify(metadataService).getMetadataFields(
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
            isNull(),
            eq(ID_FIELD),
            eq(ID_FIELD),
            eq(ID_FIELD),
        )
        verifyNoMoreInteractions(metadataService)
    }
}
