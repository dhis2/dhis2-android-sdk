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
package org.hisp.dhis.android.core.usecase.stock

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth
import java.io.InputStream
import org.hisp.dhis.android.core.Inject
import org.junit.Test

class InternalStockUseCaseShould {
    private var objectMapper: ObjectMapper = Inject.objectMapper()
    private var jsonStream: InputStream = this.javaClass.classLoader!!
        .getResourceAsStream("usecase.stock/stock_use_case.json")

    @Test
    fun map_from_json_string() {
        val internalStockUseCase: InternalStockUseCase = objectMapper
            .readValue(jsonStream, InternalStockUseCase::class.java)

        Truth.assertThat(internalStockUseCase).isNotNull()
        Truth.assertThat(internalStockUseCase.uid()).isEqualTo("IpHINAT79UW")
        Truth.assertThat(internalStockUseCase.itemCode()).isEqualTo("wBr4wccNBj1")
        Truth.assertThat(internalStockUseCase.itemDescription()).isEqualTo("MBczRWvfM46")
        Truth.assertThat(internalStockUseCase.programType()).isEqualTo("logistics")
        Truth.assertThat(internalStockUseCase.description()).isEqualTo("this is a logistics program, stock management")
        Truth.assertThat(internalStockUseCase.stockOnHand()).isEqualTo("ypCQAFr1a5l")
        Truth.assertThat(internalStockUseCase.uid()).isEqualTo("IpHINAT79UW")

        val transactions = internalStockUseCase.transactions()
        Truth.assertThat(transactions?.size).isEqualTo(3)
        Truth.assertThat(transactions?.get(0)?.sortOrder()).isEqualTo(0)
        Truth.assertThat(transactions?.get(0)?.transactionType()).isEqualTo("DISTRIBUTED")
        Truth.assertThat(transactions?.get(0)?.distributedTo()).isEqualTo("yfsEseIcEXr")
        Truth.assertThat(transactions?.get(0)?.stockDistributed()).isEqualTo("lpGYJoVUudr")
        Truth.assertThat(transactions?.get(1)?.stockCount()).isEqualTo("ej1YwWaYGmm")
        Truth.assertThat(transactions?.get(2)?.stockDiscarded()).isEqualTo("I7cmT3iXT0y")
    }
}
