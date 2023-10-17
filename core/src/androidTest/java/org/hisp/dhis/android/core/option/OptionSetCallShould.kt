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
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class OptionSetCallShould : BaseMockIntegrationTestEmptyEnqueable() {
    private lateinit var optionSetCall: suspend () -> List<OptionSet>
    private lateinit var coroutineAPICallExecutor: CoroutineAPICallExecutor

    @Before
    @Throws(D2Error::class)
    fun setUp() {
        dhis2MockServer.enqueueMockResponse("option/option_sets.json")
        val uids: MutableSet<String> = HashSet()
        uids.add("POc7DkGU3QU")

        optionSetCall = { objects.d2DIComponent.optionSetCall().download(uids) }

        coroutineAPICallExecutor = objects.d2DIComponent.coroutineApiCallExecutor()
    }

    @Test
    @Throws(Exception::class)
    fun persist_option_sets_in_data_base_when_call() = runTest {
        executeOptionSetCall()
        val optionSets = d2.optionModule().optionSets()
        Truth.assertThat(optionSets.blockingCount()).isEqualTo(2)
        Truth.assertThat(optionSets.uid("VQ2lai3OfVG").blockingExists()).isTrue()
        Truth.assertThat(optionSets.uid("TQ2lai3OfVG").blockingExists()).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun return_option_set_after_call() = runTest {
        val optionSetList = executeOptionSetCall()
        Truth.assertThat(optionSetList!!.size).isEqualTo(2)
        val optionSet = optionSetList[0]
        Truth.assertThat(optionSet.uid()).isEqualTo("VQ2lai3OfVG")
        Truth.assertThat(optionSet.code()).isNull()
        Truth.assertThat(optionSet.name()).isEqualTo("Age category")
        Truth.assertThat(optionSet.displayName()).isEqualTo("Age category")
        Truth.assertThat(optionSet.created()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2014-06-22T10:59:26.564"),
        )
        Truth.assertThat(optionSet.lastUpdated()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2015-08-06T14:23:38.789"),
        )
        Truth.assertThat(optionSet.version()).isEqualTo(1)
        Truth.assertThat(optionSet.valueType()).isEqualTo(ValueType.TEXT)
    }

    @Throws(Exception::class)
    private suspend fun executeOptionSetCall(): List<OptionSet>? {
        return coroutineAPICallExecutor.wrapTransactionally {
            var optionSets: List<OptionSet>? = null
            try {
                optionSets = optionSetCall.invoke()
            } catch (ignored: Exception) {
            }
            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors()
            optionSets
        }
    }
}
