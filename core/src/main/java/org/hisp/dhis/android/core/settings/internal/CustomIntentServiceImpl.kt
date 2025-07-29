/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.settings.CustomIntent
import org.hisp.dhis.android.core.settings.CustomIntentContext
import org.hisp.dhis.android.core.settings.CustomIntentService
import org.hisp.dhis.lib.expression.Expression
import org.hisp.dhis.lib.expression.ExpressionMode
import org.hisp.dhis.lib.expression.spi.ExpressionData
import org.koin.core.annotation.Singleton

@Singleton
internal class CustomIntentServiceImpl : CustomIntentService {
    override fun evaluateRequestParams(customIntent: CustomIntent, context: CustomIntentContext): Map<String, Any?> {
        val programVariables = mutableMapOf<String, Any>().apply {
            context.programUid?.let { put("program_id", it) }
            context.programStageUid?.let { put("program_stage_id", it) }
        }

        val data = ExpressionData(
            programRuleVariableValues = mapOf(),
            programVariableValues = programVariables,
            supplementaryValues = mapOf(),
            dataItemValues = mapOf(),
            namedValues = mapOf(),
        )

        return customIntent.request()?.arguments()?.associate { argument ->
            val expression = Expression(argument.value(), ExpressionMode.PROGRAM_INDICATOR_EXPRESSION, false)
            val expressionValue = expression.evaluate({ _ -> null }, data)

            val argumentValue = when (expressionValue) {
                is Double ->
                    if (expressionValue.rem(1).equals(0.0)) expressionValue.toInt()
                    else expressionValue

                else -> expressionValue
            }

            argument.key() to argumentValue
        } ?: emptyMap()
    }
}
