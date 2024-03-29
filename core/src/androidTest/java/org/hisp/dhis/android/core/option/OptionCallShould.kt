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
package org.hisp.dhis.android.core.option

import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class OptionCallShould : BaseMockIntegrationTestEmptyEnqueable() {
    private lateinit var optionCall: suspend () -> List<Option>
    private lateinit var coroutineAPICallExecutor: CoroutineAPICallExecutor

    @Before
    @Throws(Exception::class)
    fun setUp() {
        dhis2MockServer.enqueueMockResponse("option/option_sets.json")
        dhis2MockServer.enqueueMockResponse("option/options.json")
        val uids = hashSetOf(
            "Y1ILwhy5VDY",
            "egT1YqFWsVk",
            "non_existent_option_uid",
            "Z1ILwhy5VDY",
        )

        optionCall = { objects.d2DIComponent.optionCall.download(uids) }

        coroutineAPICallExecutor = objects.d2DIComponent.coroutineApiCallExecutor
        executeOptionSetCall()
    }

    @Test
    @Throws(Exception::class)
    fun persist_options_in_data_base_when_call() = runTest {
        executeOptionCall()
        val options = d2.optionModule().options()
        Truth.assertThat(options.blockingCount()).isEqualTo(3)
        Truth.assertThat(options.uid("Y1ILwhy5VDY").blockingExists()).isTrue()
        Truth.assertThat(options.uid("egT1YqFWsVk").blockingExists()).isTrue()
        Truth.assertThat(options.uid("non_existent_option_uid").blockingExists()).isFalse()
        Truth.assertThat(options.uid("Z1ILwhy5VDY").blockingExists()).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun return_options_after_call() = runTest {
        val optionList = executeOptionCall()
        Truth.assertThat(optionList!!.size).isEqualTo(4)
        val option = optionList[0]
        Truth.assertThat(option.uid()).isEqualTo("Y1ILwhy5VDY")
        Truth.assertThat(option.code()).isEqualTo("0-14 years")
        Truth.assertThat(option.name()).isEqualTo("0-14 years")
        Truth.assertThat(option.displayName()).isEqualTo("0-14 years")
        Truth.assertThat(option.created()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-18T12:39:16.000"),
        )
        Truth.assertThat(option.lastUpdated()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-18T12:39:16.000"),
        )
        Truth.assertThat(option.optionSet()!!.uid()).isEqualTo("VQ2lai3OfVG")
        Truth.assertThat(option.sortOrder()).isEqualTo(1)
    }

    @Throws(Exception::class)
    private fun executeOptionSetCall() = runTest {
        coroutineAPICallExecutor.wrapTransactionally<List<OptionSet>> {
            var optionSets: List<OptionSet>? = null
            try {
                val uids: MutableSet<String> = HashSet()
                uids.add("POc7DkGU3QU")

                optionSets = objects.d2DIComponent.optionSetCall.download(uids)
            } catch (ignored: Exception) {
            }
            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors()
            optionSets!!
        }
    }

    @Throws(Exception::class)
    private suspend fun executeOptionCall(): List<Option>? {
        return coroutineAPICallExecutor.wrapTransactionally {
            var options: List<Option>? = null
            try {
                options = optionCall.invoke()
            } catch (ignored: Exception) {
            }
            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors()
            options
        }
    }
}
