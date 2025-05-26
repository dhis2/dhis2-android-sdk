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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.user.UserGroupCollectionRepository
import org.hisp.dhis.android.core.user.UserModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class LatestAppVersionCallShould {
    private val handler: LatestAppVersionHandler = mock()
    private val service: SettingAppService = mock()
    private val userModule: UserModule = mock()
    private val userGroupCollectionRepository: UserGroupCollectionRepository = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutorMock = CoroutineAPICallExecutorMock()

    private lateinit var latestAppVersionCall: LatestAppVersionCall

    private val versionComparator = LatestAppVersionComparator()

    private fun mockApkDistributionVersion(
        groupIdList: List<String>,
        isDefaultFlag: Boolean,
        versionTag: String,
    ): ApkDistributionVersion {
        return mock<ApkDistributionVersion> {
            on { userGroups } doReturn groupIdList
            on { isDefault } doReturn isDefaultFlag
            on { version } doReturn versionTag
        }
    }

    private val version1 = mockApkDistributionVersion(listOf("uid1"), false, "1.0.3")
    private val version2 = mockApkDistributionVersion(listOf(), true, "1.1.0")
    private val version3 = mockApkDistributionVersion(listOf("uid3"), false, "2.0.0")

    @Before
    fun setUp() {
        latestAppVersionCall = LatestAppVersionCall(
            handler, service, userModule, versionComparator, coroutineAPICallExecutor,
        )
    }

    private fun mockUserGroups(userGroupUids: List<String>) {
        whenever(userModule.userGroups()).thenReturn(userGroupCollectionRepository)
        whenever(userGroupCollectionRepository.blockingGetUids()).thenReturn(userGroupUids)
    }

    private suspend fun mockVersions(versions: List<ApkDistributionVersion>) {
        val payload = mock<Payload<ApkDistributionVersion>> {
            on { items } doReturn versions
        }
        whenever(service.versions()).thenReturn(payload)
    }

    @Test
    fun default_to_empty_collection_if_not_found() = runTest {
        whenever(userModule.userGroups()).thenReturn(userGroupCollectionRepository)
        whenever(userGroupCollectionRepository.blockingGet()).thenReturn(emptyList())

        whenever(service.versions()) doAnswer { throw D2ErrorSamples.notFound() }
        whenever(service.latestAppVersion()) doAnswer { throw D2ErrorSamples.notFound() }

        latestAppVersionCall.download(false)

        verify(handler).handleMany(emptyList())
        verifyNoMoreInteractions(handler)
    }

    @Test
    fun resolve_correct_apk_distribution_version_with_groups() = runTest {
        mockUserGroups(listOf("uid1", "uid2"))
        mockVersions(listOf(version1, version2, version3))

        val result = latestAppVersionCall.resolveApkDistributionVersion()
        assertThat("1.1.0").isEqualTo(result?.version)
    }

    @Test
    fun resolve_correct_apk_distribution_version_default() = runTest {
        mockUserGroups(emptyList<String>())
        mockVersions(listOf(version1, version2, version3))

        val result = latestAppVersionCall.resolveApkDistributionVersion()
        assertThat("1.1.0").isEqualTo(result?.version)
    }

    @Test
    fun resolve_correct_apk_distribution_version_repeated() = runTest {
        mockUserGroups(listOf("uid1", "uid2"))

        val version2_rep = mockApkDistributionVersion(listOf("uid2"), false, "1.1.1")
        mockVersions(listOf(version1, version2, version3, version2_rep))

        val result = latestAppVersionCall.resolveApkDistributionVersion()
        assertThat("1.1.1").isEqualTo(result?.version)
    }

    @Test
    fun resolve_other_correct_apk_distribution_version_with_groups() = runTest {
        mockUserGroups(listOf("uid2", "uid3"))
        mockVersions(listOf(version1, version2, version3))

        val result = latestAppVersionCall.resolveApkDistributionVersion()
        assertThat("2.0.0").isEqualTo(result?.version)
    }

    @Test
    fun resolve_correct_apk_distribution_version_repeated_default() = runTest {
        mockUserGroups(listOf("uid1", "uid2"))

        val version_rep_default = mockApkDistributionVersion(listOf("uid2"), true, "1.1.1")
        mockVersions(listOf(version1, version2, version3, version_rep_default))

        val result = latestAppVersionCall.resolveApkDistributionVersion()
        assertThat("1.1.1").isEqualTo(result?.version)
    }
}
