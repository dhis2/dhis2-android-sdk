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
import org.hisp.dhis.android.core.systeminfo.DHISPatchVersion
import org.hisp.dhis.android.core.systeminfo.DHISPatchVersion.Companion.isGreaterThanPatch
import org.junit.Test

class DHISPatchVersionShould {
    @Test
    fun compare_if_greater_than_patch() {
        assertThat(isGreaterThanPatch("2.42.0", DHISPatchVersion.V2_42_1)).isFalse()
        assertThat(isGreaterThanPatch("2.42.1", DHISPatchVersion.V2_42_1)).isFalse()
        assertThat(isGreaterThanPatch("2.42.2", DHISPatchVersion.V2_42_1)).isTrue()
        assertThat(isGreaterThanPatch("2.42.10", DHISPatchVersion.V2_42_1)).isTrue()
        assertThat(isGreaterThanPatch("2.41.5", DHISPatchVersion.V2_42_1)).isFalse()

        assertThat(isGreaterThanPatch("2.42.3-SNAPSHOT", DHISPatchVersion.V2_42_1)).isTrue()
        assertThat(isGreaterThanPatch("2.42-SNAPSHOT", DHISPatchVersion.V2_41_0)).isTrue()
    }
}
