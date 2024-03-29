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
package org.hisp.dhis.android.core.arch.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserHelperShould {
    @Test
    fun md5_evaluate_same_string() {
        val md5s1 = UserHelper.md5("user1", "password1")
        val md5s2 = UserHelper.md5("user1", "password1")

        assertThat(md5s1.length).isEqualTo(32)
        assertThat(md5s2.length).isEqualTo(32)
        assertThat(md5s1 == md5s2).isTrue()
    }

    @Test
    fun md5_evaluate_different_string() {
        val md5s1 = UserHelper.md5("user2", "password2")
        val md5s2 = UserHelper.md5("user3", "password3")

        assertThat(md5s1.length).isEqualTo(32)
        assertThat(md5s2.length).isEqualTo(32)
        assertThat(md5s1 == md5s2).isFalse()
    }

    @Test
    fun md5_evaluate_special_chars() {
        val md5s1 = UserHelper.md5("user1", "pässword")
        val md5s2 = UserHelper.md5("user1", "password")

        assertThat(md5s1.length).isEqualTo(32)
        assertThat(md5s2.length).isEqualTo(32)
        assertThat(md5s1 == md5s2).isFalse()
    }

    @Test
    fun base64_encode_credentials() {
        val base64 = UserHelper.base64("user", "password")
        assertThat(base64).isEqualTo("dXNlcjpwYXNzd29yZA==")
    }

    @Test
    fun base64_encode_special_chars() {
        val base64 = UserHelper.base64("user", "pässword")
        assertThat(base64).isEqualTo("dXNlcjpww6Rzc3dvcmQ=")
    }
}
