/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.settings

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.settings.internal.CustomIntentServiceImpl
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserStore
import org.junit.Test
import org.mockito.Answers
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CustomIntentServiceShould {

    private val customIntent: CustomIntent = mock(defaultAnswer = Answers.RETURNS_DEEP_STUBS)
    private val userStore: UserStore = mock()
    private val orgunitStore: OrganisationUnitStore = mock()

    private val user: User = mock()
    private val orgunit: OrganisationUnit = mock()

    private val service: CustomIntentService = CustomIntentServiceImpl(userStore, orgunitStore)

    @Test
    fun evaluate_custom_intent_parameter() {
        mockCustomIntentArguments(
            mapOf(
                "username" to "'admin'",
                "quoted" to "'\\'admin\\''",
                "version" to "20250102",
                "threshold" to "98.3",
            ),
        )

        val context = CustomIntentContext()

        val params = service.blockingEvaluateRequestParams(customIntent, context)

        assertThat(params).isEqualTo(
            mapOf(
                "username" to "admin",
                "quoted" to "'admin'",
                "version" to 20250102,
                "threshold" to 98.3,
            ),
        )
    }

    @Test
    fun evaluate_orgunit_variables() = runTest {
        mockCustomIntentArguments(
            mapOf(
                "orgunit" to "VAR{orgunit_id}",
                "district" to "d2:split(VAR{orgunit_path}, '/', 2)",
                "oucode" to "d2:concatenate('OU_', VAR{orgunit_code})",
            ),
        )

        val context = CustomIntentContext(orgunitUid = "yRqcmmdO6cJ")

        whenever(orgunitStore.selectByUid("yRqcmmdO6cJ")).doReturn(orgunit)
        whenever(orgunit.uid()).doReturn("yRqcmmdO6cJ")
        whenever(orgunit.path()).doReturn("/ImspTQPwCqd/at6UHUQatSo/qtr8GGlm4gg/yRqcmmdO6cJ")
        whenever(orgunit.code()).doReturn("LI456")

        val params = service.blockingEvaluateRequestParams(customIntent, context)

        assertThat(params).isEqualTo(
            mapOf(
                "orgunit" to "yRqcmmdO6cJ",
                "district" to "at6UHUQatSo",
                "oucode" to "OU_LI456",
            ),
        )
    }

    @Test
    fun evaluate_user_variables() = runTest {
        mockCustomIntentArguments(
            mapOf(
                "id" to "VAR{user_id}",
                "name" to "VAR{user_username}",
            ),
        )

        val context = CustomIntentContext()

        whenever(userStore.selectFirst()).doReturn(user)
        whenever(user.uid()).doReturn("yRqcmmdO6cJ")
        whenever(user.username()).doReturn("admin")

        val params = service.blockingEvaluateRequestParams(customIntent, context)

        assertThat(params).isEqualTo(
            mapOf(
                "id" to "yRqcmmdO6cJ",
                "name" to "admin",
            ),
        )
    }

    private fun mockCustomIntentArguments(argumentMap: Map<String, String>) {
        val requestArguments = argumentMap.map { (key, value) ->
            CustomIntentRequestArgument.builder().key(key).value(value).build()
        }
        whenever(customIntent.request()?.arguments()) doReturn requestArguments
    }
}
