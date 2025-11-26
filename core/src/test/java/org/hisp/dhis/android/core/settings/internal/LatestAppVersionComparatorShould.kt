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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.*

@RunWith(JUnit4::class)
class LatestAppVersionComparatorShould {
    private val version1: ApkDistributionVersion = mock()
    private val version2: ApkDistributionVersion = mock()
    private val version3: ApkDistributionVersion = mock()
    private val comparator = LatestAppVersionComparator().comparator

    @Test
    fun correctly_compare_versions_where_one_is_greater() {
        whenever(version1.version) doReturn "1.2.3"
        whenever(version2.version) doReturn "1.2.4"

        val result = comparator.compare(version1, version2)

        assertThat(result).isLessThan(0)
    }

    @Test
    fun treat_versions_as_equal_when_they_are_the_same() {
        whenever(version1.version) doReturn "1.2.3"
        whenever(version2.version) doReturn "1.2.3"

        val result = comparator.compare(version1, version2)

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun handle_versions_with_different_lengths_correctly() {
        whenever(version1.version) doReturn "1.2"
        whenever(version2.version) doReturn "1.2.1"

        val result = comparator.compare(version1, version2)

        assertThat(result).isLessThan(0)
    }

    @Test
    fun handle_non_numeric_parts_by_treating_them_as_0() {
        whenever(version1.version) doReturn "1.2.x"
        whenever(version2.version) doReturn "1.2.1"

        val result = comparator.compare(version1, version2)

        assertThat(result).isLessThan(0)
    }

    @Test
    fun return_the_greatest_version() {
        whenever(version1.version) doReturn "1.2"
        whenever(version2.version) doReturn "1.1.1"
        whenever(version3.version) doReturn "1.1.0"

        val highestVersion = listOf(version1, version2, version3).maxWithOrNull(comparator)

        assertThat(highestVersion).isEqualTo(version1)
    }

    @Test
    fun return_the_first_in_list_when_two_greatest_versions() {
        whenever(version1.version) doReturn "1.2.0"
        whenever(version2.version) doReturn "1.2.1"
        whenever(version3.version) doReturn "1.2.1"

        val highestVersion = listOf(version1, version2, version3).maxWithOrNull(comparator)

        assertThat(highestVersion).isEqualTo(version2)
    }

    @Test
    fun return_the_first_version_when_string_is_not_a_number() {
        whenever(version1.version) doReturn "version_one"
        whenever(version2.version) doReturn "version_two"
        whenever(version3.version) doReturn "version_three"

        val highestVersion = listOf(version1, version2, version3).maxWithOrNull(comparator)

        assertThat(highestVersion).isEqualTo(version1)
    }

    @Test
    fun return_null_when_empty_list() {
        val highestVersion = emptyList<ApkDistributionVersion>().maxWithOrNull(comparator)

        assertThat(highestVersion).isNull()
    }
}
