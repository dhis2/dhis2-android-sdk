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

package org.hisp.dhis.android.core.arch.db.access.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.persistence.db.access.formatSqlReal
import org.junit.Test

class SqlStringValuesShould {

    @Test
    fun format_integral_reals_without_decimals() {
        assertThat(formatSqlReal(15.0)).isEqualTo("15")
        assertThat(formatSqlReal(-10.0)).isEqualTo("-10")
        assertThat(formatSqlReal(0.0)).isEqualTo("0")
        assertThat(formatSqlReal(-0.0)).isEqualTo("0")
        assertThat(formatSqlReal(571680.0)).isEqualTo("571680")
        assertThat(formatSqlReal(12345678.0)).isEqualTo("12345678")
    }

    @Test
    fun keep_decimals_of_non_integral_reals() {
        assertThat(formatSqlReal(2.5)).isEqualTo("2.5")
        assertThat(formatSqlReal(-6.5)).isEqualTo("-6.5")
        assertThat(formatSqlReal(1234567.5)).isEqualTo("1234567.5")
    }
}
