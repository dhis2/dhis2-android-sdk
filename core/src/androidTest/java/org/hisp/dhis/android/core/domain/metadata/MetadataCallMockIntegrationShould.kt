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
package org.hisp.dhis.android.core.domain.metadata

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItem
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.settings.SystemSetting
import org.hisp.dhis.android.core.sms.SmsModule
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.usecase.stock.StockUseCase
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.core.visualization.Visualization
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class MetadataCallMockIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    @After
    fun tearDown() {
        d2.wipeModule().wipeEverything()
    }

    @Test
    fun emit_progress() {
        val testObserver = d2.metadataModule().download().test()

        testObserver.awaitTerminalEvent()

        testObserver.assertValueCount(15)

        val values = testObserver.values()

        val allExceptLastValue = values.dropLast(1)
        val lastValue = values.last()

        allExceptLastValue.forEach { assertThat(it.isComplete).isFalse() }

        assertThat(lastValue.isComplete).isTrue()
        assertThat(lastValue.doneCalls()).containsExactlyElementsIn(
            listOf(
                SystemInfo::class,
                SystemSetting::class,
                StockUseCase::class,
                Constant::class,
                SmsModule::class,
                User::class,
                OrganisationUnit::class,
                Program::class,
                DataSet::class,
                Category::class,
                Visualization::class,
                ProgramIndicator::class,
                Indicator::class,
                LegendSet::class,
                ExpressionDimensionItem::class,
            ).map { it.java.simpleName }
        )

        testObserver.dispose()
    }
}
