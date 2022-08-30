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
package org.hisp.dhis.android.core.programtheme.stock

sealed class StockThemeTransaction {
    abstract val order: Int
    abstract val transactionType: TransactionType

    data class Distributed(
        override val order: Int,
        override val transactionType: TransactionType,
        val distributedTo: String,
        val stockDistributed: String
    ) : StockThemeTransaction()

    data class Discarded(
        override val order: Int,
        override val transactionType: TransactionType,
        val stockDiscarded: String
    ) : StockThemeTransaction()

    data class Correction(
        override val order: Int,
        override val transactionType: TransactionType,
        val stockCorrected: String
    ) : StockThemeTransaction()

    companion object {
        enum class TransactionType {
            DISTRIBUTED,
            DISCARDED,
            CORRECTED
        }

        internal fun transformFrom(t: InternalStockThemeTransaction): StockThemeTransaction {
            return when (val type = TransactionType.valueOf(t.transactionType())) {
                TransactionType.DISTRIBUTED -> Distributed(t.order(), type, t.distributedTo()!!, t.stockDistributed()!!)
                TransactionType.DISCARDED -> Discarded(t.order(), type, t.stockDiscarded()!!)
                TransactionType.CORRECTED -> Correction(t.order(), type, t.stockCorrected()!!)
            }
        }

        internal fun transformTo(programUid: String, t: StockThemeTransaction): InternalStockThemeTransaction {
            val builder = InternalStockThemeTransaction.builder()
                .programUid(programUid)
                .transactionType(t.transactionType.name)
                .order(t.order)

            when (t) {
                is Distributed -> builder.distributedTo(t.distributedTo).stockDistributed(t.stockDistributed)
                is Correction -> builder.stockCorrected(t.stockCorrected)
                is Discarded -> builder.stockDiscarded(t.stockDiscarded)
            }

            return builder.build()
        }
    }
}
