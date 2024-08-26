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
package org.hisp.dhis.android.testapp.maintenance

import com.google.common.truth.Truth
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class D2ErrorCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun filter_d2_error_by_url() {
        val d2Errors = d2.maintenanceModule().d2Errors()
            .byUrl().like("http://dhis2.org/api/programs/uid").blockingGet()
        Truth.assertThat(d2Errors.size).isEqualTo(1)
    }

    @Test
    fun filter_d2_error_by_d2_error_code() {
        val d2Errors = d2.maintenanceModule().d2Errors()
            .byD2ErrorCode().eq(D2ErrorCode.BAD_CREDENTIALS).blockingGet()
        Truth.assertThat(d2Errors.size).isEqualTo(1)
    }

    @Test
    fun filter_d2_error_by_d2_error_component() {
        val d2Errors = d2.maintenanceModule().d2Errors()
            .byD2ErrorComponent().eq(D2ErrorComponent.Server).blockingGet()
        Truth.assertThat(d2Errors.size).isEqualTo(4)
    }

    @Test
    fun filter_d2_error_by_error_description() {
        val d2Errors = d2.maintenanceModule().d2Errors()
            .byErrorDescription().eq("Error processing response").blockingGet()
        Truth.assertThat(d2Errors.size).isEqualTo(1)
    }

    @Test
    fun filter_d2_error_by_http_error_code() {
        val d2Errors = d2.maintenanceModule().d2Errors()
            .byHttpErrorCode().eq(402).blockingGet()
        Truth.assertThat(d2Errors.size).isEqualTo(1)
    }

    @Test
    fun filter_d2_error_by_created() {
        val startDate = ClockProviderFactory.clockProvider.clock.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .atStartOfDayIn(TimeZone.currentSystemDefault()).toJavaDate()

        val d2Errors = d2.maintenanceModule().d2Errors()
            .byCreated().afterOrEqual(startDate).blockingGet()

        Truth.assertThat(d2Errors.size).isEqualTo(5)
    }
}