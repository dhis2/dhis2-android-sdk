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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.settings.LatestAppVersion
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.user.UserGroupCollectionRepository
import org.hisp.dhis.android.core.user.UserModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.stubbing.Answer

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class LatestAppVersionCallShould {
    private val handler: LatestAppVersionHandler = mock()
    private val service: SettingAppService = mock()
    private val userModule: UserModule = mock()
    private val versionComparator: LatestAppVersionComparator = mock()
    private val versions: List<LatestAppVersion> = mock()
    private val latestVersion: LatestAppVersion = mock()
    private val userGroupCollectionRepository: UserGroupCollectionRepository = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutorMock = CoroutineAPICallExecutorMock()

    private lateinit var latestAppVersionCall: LatestAppVersionCall

    @Before
    fun setUp() {
        whenVersionsAPICall { versions }
        whenLatestVersionAPICall { latestVersion }

        whenever(userModule.userGroups()).thenReturn(userGroupCollectionRepository)
        whenever(userGroupCollectionRepository.blockingGet()).thenReturn(emptyList())

        latestAppVersionCall = LatestAppVersionCall(handler, service, userModule, versionComparator, coroutineAPICallExecutor)
    }

    private fun whenVersionsAPICall(answer: Answer<List<LatestAppVersion>>) {
        service.stub {
            onBlocking { versions() }.doAnswer(answer)
        }
    }

    private fun whenLatestVersionAPICall(answer: Answer<LatestAppVersion>) {
        service.stub {
            onBlocking { latestAppVersion() }.doAnswer(answer)
        }
    }

    @Test
    fun default_to_empty_collection_if_not_found() = runTest {
        whenever(service.versions()) doAnswer { throw D2ErrorSamples.notFound() }
        whenever(service.latestAppVersion()) doAnswer { throw D2ErrorSamples.notFound() }

        latestAppVersionCall.download(false)

        verify(handler).handleMany(emptyList())
        verifyNoMoreInteractions(handler)
    }
}
