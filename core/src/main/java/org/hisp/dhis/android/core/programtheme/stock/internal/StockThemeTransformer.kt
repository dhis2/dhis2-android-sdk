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
package org.hisp.dhis.android.core.programtheme.stock.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.handlers.internal.TwoWayTransformer
import org.hisp.dhis.android.core.programtheme.stock.InternalStockTheme
import org.hisp.dhis.android.core.programtheme.stock.StockTheme
import org.hisp.dhis.android.core.programtheme.stock.StockThemeTransaction

@Reusable
internal class StockThemeTransformer : TwoWayTransformer<InternalStockTheme, StockTheme> {
    override fun transform(o: InternalStockTheme): StockTheme {
        return StockTheme(
            o.uid(),
            o.itemCode(),
            o.itemDescription(),
            o.stockOnHand(),
            o.transactions().map { StockThemeTransaction.transformFrom(it) }
        )
    }

    override fun deTransform(t: StockTheme): InternalStockTheme {
        return InternalStockTheme.builder()
            .uid(t.programUid)
            .itemCode(t.itemCode)
            .itemDescription(t.itemDescription)
            .stockOnHand(t.stockOnHand)
            .transactions(t.transactions.map { StockThemeTransaction.transformTo(t.programUid, it) })
            .build()
    }
}
