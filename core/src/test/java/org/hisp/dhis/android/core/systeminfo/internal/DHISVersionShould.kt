/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.systeminfo.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.junit.Test

class DHISVersionShould {
    @Test
    fun return_null_for_unsupported_versions() {
        DHISVersion.entries
            .forEach {
                if (it.supported) {
                    assertThat(DHISVersion.getValue(it.prefix + ".0")).isNotNull()
                    assertThat(DHISVersion.getValue(it.prefix + ".9")).isNotNull()
                } else {
                    assertThat(DHISVersion.getValue(it.prefix + ".0")).isNull()
                    assertThat(DHISVersion.getValue(it.prefix + ".9")).isNull()
                }
            }
    }

    @Test
    fun return_unknown_when_bypassing_version_for_unsupported_versions() {
        DHISVersion.entries
            .forEach {
                if (it.supported) {
                    assertThat(DHISVersion.getValue(it.prefix + ".0", true)).isNotNull()
                    assertThat(DHISVersion.getValue(it.prefix + ".9", true)).isNotNull()
                } else {
                    assertThat(DHISVersion.getValue(it.prefix + ".0", true))
                        .isEqualTo(DHISVersion.UNKNOWN)
                    assertThat(DHISVersion.getValue(it.prefix + ".9", true))
                        .isEqualTo(DHISVersion.UNKNOWN)
                }
            }
    }
}
