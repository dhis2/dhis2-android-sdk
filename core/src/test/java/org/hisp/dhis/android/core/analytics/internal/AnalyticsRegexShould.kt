/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.dateRangeRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.orgunitLevelRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.uidRegex
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnalyticsRegexShould {

    @Test
    fun should_evaluate_orgunit_regex() {
        assertThat(orgunitLevelRegex.matches("LEVEL-4")).isTrue()
        assertThat(orgunitLevelRegex.matches("LEVEL-ajflkaaf")).isFalse()

        val (level) = orgunitLevelRegex.find("LEVEL-5")!!.destructured
        assertThat(level).isEqualTo("5")
    }

    @Test
    fun should_evaluate_date_range() {
        assertThat(dateRangeRegex.matches("2015-12-11_2024-01-04")).isTrue()
        assertThat(dateRangeRegex.matches("2015-12-11")).isFalse()

        val (start, end) = dateRangeRegex.find("2015-12-11_2024-01-04")!!.destructured
        assertThat(start).isEqualTo("2015-12-11")
        assertThat(end).isEqualTo("2024-01-04")
    }

    @Test
    fun should_evaluate_uid() {
        assertThat(uidRegex.matches("YuQRtpL10I")).isFalse()
        assertThat(uidRegex.matches("YuQRtpLP10I")).isTrue()
        assertThat(uidRegex.matches("YuQRtpL10Ier")).isFalse()
    }
}
