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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.analyticexpressionengine

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.koin.core.annotation.Singleton

@Singleton
internal class AnalyticExpressionEngine(
    private val visitor: CommonExpressionVisitor,
) {

    suspend fun evaluate(
        expression: String,
    ): Any? {
        resolveItemValues(expression)
        return CommonParser.visit(expression, visitor)
    }

    /**
     * Visits the expression once to collect its data items, with placeholder values, and computes
     * them here, where the caller's connection is available. Items the collecting visit does not
     * reach, like a branch of `if()` taken only with the real values, are computed during the visit.
     */
    @Suppress("TooGenericExceptionCaught")
    private suspend fun resolveItemValues(expression: String) {
        val itemValues = visitor.indicatorContext?.itemValues ?: return
        val collector = CommonExpressionVisitor(visitor.scope).also { it.days = visitor.days }

        itemValues.collecting = true
        try {
            CommonParser.visit(expression, collector)
        } catch (_: Exception) {
            // The visit below reports it
        } finally {
            itemValues.collecting = false
        }
        itemValues.resolve()
    }
}
