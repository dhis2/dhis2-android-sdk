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
import org.hisp.dhis.android.core.settings.internal.CustomIntentServiceImpl
import org.junit.Test
import org.mockito.Answers
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CustomIntentServiceShould {

    private val customIntent: CustomIntent = mock(defaultAnswer = Answers.RETURNS_DEEP_STUBS)

    private val service: CustomIntentService = CustomIntentServiceImpl()

    @Test
    fun evaluate_custom_intent_parameter() {
        mockArguments(
            listOf(
                CustomIntentRequestArgument.builder().key("username").value("'admin'").build(),
                CustomIntentRequestArgument.builder().key("quoted").value("'\\'admin\\''").build(),
                CustomIntentRequestArgument.builder().key("project").value("V{program_stage_id}").build(),
                CustomIntentRequestArgument.builder().key("version").value("20250102").build(),
                CustomIntentRequestArgument.builder().key("threshold").value("98.3").build(),
            ),
        )

        val context = CustomIntentContext(
            programUid = "t62IcmZdP3T",
            programStageUid = "yRqcmmdO6cJ",
        )

        val params = service.blockingEvaluateRequestParams(customIntent, context)

        assertThat(params).isEqualTo(
            mapOf(
                "username" to "admin",
                "quoted" to "'admin'",
                "project" to "yRqcmmdO6cJ",
                "version" to 20250102,
                "threshold" to 98.3,
            ),
        )
    }

    private fun mockArguments(arguments: List<CustomIntentRequestArgument>) {
        whenever(customIntent.request()?.arguments()) doReturn arguments
    }
}
