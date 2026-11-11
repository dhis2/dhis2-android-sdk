/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.arch.helpers.internal

import android.content.Context
import android.content.pm.ApplicationInfo
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

class D2LoggerShould {

    @After
    fun tearDown() {
        D2Logger.init(contextWithFlags(0))
    }

    @Test
    fun describe_nothing_when_there_is_no_throwable() {
        assertThat(D2Logger.describe(null)).isEmpty()
    }

    @Test
    fun describe_the_cause_chain_without_messages() {
        val throwable = IllegalStateException("token=secret", IOException("/data/user/server_admin.db"))

        val description = D2Logger.describe(throwable)

        assertThat(description).isEqualTo(" [IllegalStateException <- IOException]")
        assertThat(description).doesNotContain("secret")
        assertThat(description).doesNotContain("admin")
    }

    @Test
    fun stop_describing_when_the_cause_is_the_throwable_itself() {
        assertThat(D2Logger.describe(SelfCausedException())).isEqualTo(" [SelfCausedException]")
    }

    @Test
    fun limit_the_depth_of_the_cause_chain() {
        val throwable = (1..10).fold(RuntimeException()) { cause, _ -> RuntimeException(cause) }

        assertThat(D2Logger.describe(throwable).split(" <- ")).hasSize(5)
    }

    @Test
    fun be_debuggable_only_when_the_host_app_is_debuggable() {
        D2Logger.init(contextWithFlags(ApplicationInfo.FLAG_DEBUGGABLE))
        assertThat(D2Logger.debuggable).isTrue()

        D2Logger.init(contextWithFlags(0))
        assertThat(D2Logger.debuggable).isFalse()
    }

    private fun contextWithFlags(flags: Int): Context {
        val applicationInfo = mock<ApplicationInfo>().also { it.flags = flags }
        return mock<Context>().also { whenever(it.applicationInfo).thenReturn(applicationInfo) }
    }

    private class SelfCausedException : Exception() {
        override val cause: Throwable
            get() = this
    }
}
