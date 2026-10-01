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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.parser.internal.expression.QueryMods

/**
 * Values of the data items of an indicator expression, computed before the expression is visited.
 * The ANTLR visitor is not suspending, so without them every item runs its query from a nested
 * blocking call, outside the caller's database connection.
 */
internal class IndicatorItemValues {
    var collecting: Boolean = false

    private val pending = LinkedHashMap<Key, suspend () -> String?>()
    private val resolved = HashMap<Key, String?>()

    data class Key(
        val sql: Boolean,
        val evaluationItem: AnalyticsServiceEvaluationItem,
        val queryMods: QueryMods?,
    )

    fun collect(key: Key, compute: suspend () -> String?) {
        if (key !in resolved) {
            pending.putIfAbsent(key, compute)
        }
    }

    /**
     * An item that fails is left out: the visit computes it again, and fails, only if it reaches it.
     */
    suspend fun resolve() {
        pending.forEach { (key, compute) ->
            try {
                resolved[key] = compute()
            } catch (_: AnalyticsException) {
                // Computed again during the visit
            }
        }
        pending.clear()
    }

    fun isResolved(key: Key): Boolean = key in resolved

    operator fun get(key: Key): String? = resolved[key]
}
