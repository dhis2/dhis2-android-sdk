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
package org.hisp.dhis.android.core.usecase.stock

sealed class StockUseCaseTransaction {
    abstract val sortOrder: Int
    abstract val transactionType: TransactionType

    data class Distributed(
        override val sortOrder: Int,
        override val transactionType: TransactionType,
        val distributedTo: String,
        val stockDistributed: String
    ) : StockUseCaseTransaction()

    data class Discarded(
        override val sortOrder: Int,
        override val transactionType: TransactionType,
        val stockDiscarded: String
    ) : StockUseCaseTransaction()

    data class Correction(
        override val sortOrder: Int,
        override val transactionType: TransactionType,
        val stockCount: String
    ) : StockUseCaseTransaction()

    companion object {
        enum class TransactionType {
            DISTRIBUTED,
            DISCARDED,
            CORRECTED
        }

        internal fun transformFrom(t: InternalStockUseCaseTransaction): StockUseCaseTransaction {
            return when (val type = TransactionType.valueOf(t.transactionType())) {
                TransactionType.DISTRIBUTED ->
                    Distributed(
                        t.sortOrder(),
                        type,
                        t.distributedTo()!!,
                        t.stockDistributed()!!
                    )
                TransactionType.DISCARDED -> Discarded(t.sortOrder(), type, t.stockDiscarded()!!)
                TransactionType.CORRECTED -> Correction(t.sortOrder(), type, t.stockCount()!!)
            }
        }

        internal fun transformTo(programUid: String, t: StockUseCaseTransaction): InternalStockUseCaseTransaction {
            val builder = InternalStockUseCaseTransaction.builder()
                .programUid(programUid)
                .transactionType(t.transactionType.name)
                .sortOrder(t.sortOrder)

            when (t) {
                is Distributed -> builder.distributedTo(t.distributedTo).stockDistributed(t.stockDistributed)
                is Correction -> builder.stockCount(t.stockCount)
                is Discarded -> builder.stockDiscarded(t.stockDiscarded)
            }

            return builder.build()
        }
    }
}
