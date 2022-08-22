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

package org.hisp.dhis.android.testapp.option

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class OptionServiceShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun find_options_for_option_set() {
        val options = d2.optionModule().optionService()
            .blockingSearchForOptions(optionSetUid = "VQ2lai3OfVG")

        assertThat(options.size).isEqualTo(2)
        assertThat(options[0].uid()).isEqualTo("Y1ILwhy5VDY")
        assertThat(options[1].uid()).isEqualTo("egT1YqFWsVk")
    }

    @Test
    fun find_option_with_search_text() {
        val options = d2.optionModule().optionService()
            .blockingSearchForOptions(optionSetUid = "VQ2lai3OfVG", searchText = "19")

        assertThat(options.size).isEqualTo(1)
        assertThat(options[0].uid()).isEqualTo("egT1YqFWsVk")
    }

    @Test
    fun find_option_with_hide_uids() {
        val options = d2.optionModule().optionService()
            .blockingSearchForOptions(optionSetUid = "VQ2lai3OfVG", optionToHideUids = listOf("Y1ILwhy5VDY"))

        assertThat(options.size).isEqualTo(1)
        assertThat(options[0].uid()).isEqualTo("egT1YqFWsVk")
    }

    @Test
    fun find_option_with_show_uids() {
        val options = d2.optionModule().optionService()
            .blockingSearchForOptions(optionSetUid = "VQ2lai3OfVG", optionToShowUids = listOf("Y1ILwhy5VDY"))

        assertThat(options.size).isEqualTo(1)
        assertThat(options[0].uid()).isEqualTo("Y1ILwhy5VDY")
    }

    @Test
    fun find_option_with_multiple_options() {
        val options = d2.optionModule().optionService()
            .blockingSearchForOptions(
                optionSetUid = "VQ2lai3OfVG",
                searchText = "19",
                optionToHideUids = listOf("Y1ILwhy5VDY"),
                optionToShowUids = listOf("egT1YqFWsVk")
            )

        assertThat(options.size).isEqualTo(1)
        assertThat(options[0].uid()).isEqualTo("egT1YqFWsVk")
    }
}
