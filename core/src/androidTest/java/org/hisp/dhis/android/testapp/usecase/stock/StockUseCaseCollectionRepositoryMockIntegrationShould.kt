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
package org.hisp.dhis.android.testapp.usecase.stock

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class StockUseCaseCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val stockUseCases = d2.useCaseModule().stockUseCases().blockingGet()

        assertThat(stockUseCases.size).isEqualTo(1)
    }

    @Test
    fun filter_by_uid() {
        val stockUseCase = d2.useCaseModule().stockUseCases()
            .uid("IpHINAT79UW")
            .blockingGet()

        assertThat(stockUseCase).isNotNull()
        assertThat(stockUseCase!!.stockOnHand).isEqualTo("ypCQAFr1a5l")
    }

    @Test
    fun filter_by_number() {
        val stockUseCases = d2.useCaseModule().stockUseCases()
            .withTransactions().blockingGet()

        assertThat(stockUseCases.size).isEqualTo(1)
        assertThat(stockUseCases[0].transactions.size).isEqualTo(3)
    }

    @Test
    fun return_false_when_use_case_does_not_exist() {
        val stockUseCaseExists = d2.useCaseModule().stockUseCases()
            .uid("IpHINAT79UW")
            .blockingExists()

        val stockUseCaseDoesNotExist = d2.useCaseModule().stockUseCases()
            .uid("false_uid")
            .blockingExists()

        assertThat(stockUseCaseExists).isEqualTo(true)
        assertThat(stockUseCaseDoesNotExist).isEqualTo(false)
    }
}
