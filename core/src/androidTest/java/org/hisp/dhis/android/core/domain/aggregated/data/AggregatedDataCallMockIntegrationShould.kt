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
package org.hisp.dhis.android.core.domain.aggregated.data

import org.hisp.dhis.android.core.arch.call.D2ProgressStatus
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class AggregatedDataCallMockIntegrationShould : BaseMockIntegrationTestMetadataDispatcher() {

    @Test
    fun emit_progress() {
        val testObserver = d2.aggregatedModule().data().download().test()

        testObserver.awaitTerminalEvent()

        testObserver.assertValueCount(4)

        testObserver.assertValueAt(0) { v: AggregatedD2Progress ->
            !v.isComplete && hasNCompletedDataSets(v.dataSets(), number = 0)
        }
        testObserver.assertValueAt(1) { v: AggregatedD2Progress ->
            !v.isComplete && hasNCompletedDataSets(v.dataSets(), number = 2)
        }
        testObserver.assertValueAt(2) { v: AggregatedD2Progress ->
            !v.isComplete && hasNCompletedDataSets(v.dataSets(), number = 3)
        }
        testObserver.assertValueAt(3) { v: AggregatedD2Progress ->
            v.isComplete && hasAllCompletedDataSets(v.dataSets())
        }

        testObserver.dispose()
    }

    private fun hasNCompletedDataSets(dataSets: Map<String, D2ProgressStatus>, number: Int): Boolean {
        return dataSets.filter { (_, progress) -> progress.isComplete }.size == number
    }

    private fun hasAllCompletedDataSets(dataSets: Map<String, D2ProgressStatus>): Boolean {
        return dataSets.all { (_, progress) -> progress.isComplete }
    }
}
