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

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.settings.CustomIntent
import org.hisp.dhis.android.core.settings.CustomIntentContext
import org.hisp.dhis.android.core.settings.CustomIntentService
import org.hisp.dhis.android.core.user.internal.UserStore
import org.hisp.dhis.lib.expression.Expression
import org.hisp.dhis.lib.expression.ExpressionMode
import org.hisp.dhis.lib.expression.spi.AndroidCustomIntentVariable
import org.hisp.dhis.lib.expression.spi.ExpressionData
import org.koin.core.annotation.Singleton

@Singleton
internal class CustomIntentServiceImpl(
    private val userStore: UserStore,
    private val orgunitStore: OrganisationUnitStore,
) : CustomIntentService {
    override fun evaluateRequestParams(
        customIntent: CustomIntent,
        context: CustomIntentContext,
    ): Single<Map<String, Any?>> {
        return rxSingle { evaluateRequestParamsInternal(customIntent, context) }
    }

    override fun blockingEvaluateRequestParams(
        customIntent: CustomIntent,
        context: CustomIntentContext,
    ): Map<String, Any?> {
        return runBlocking { evaluateRequestParamsInternal(customIntent, context) }
    }

    private suspend fun evaluateRequestParamsInternal(
        customIntent: CustomIntent,
        context: CustomIntentContext,
    ): Map<String, Any?> {
        val user = userStore.selectFirst()
        val orgunit = context.orgunitUid?.let { orgunitStore.selectByUid(it) }

        val programVariables = buildMap {
            user?.uid()?.let { put(AndroidCustomIntentVariable.user_id.name, it) }
            user?.username()?.let { put(AndroidCustomIntentVariable.user_username.name, it) }
            orgunit?.uid()?.let { put(AndroidCustomIntentVariable.orgunit_id.name, it) }
            orgunit?.path()?.let { put(AndroidCustomIntentVariable.orgunit_path.name, it) }
            orgunit?.code()?.let { put(AndroidCustomIntentVariable.orgunit_code.name, it) }
        }

        val data = ExpressionData(
            programRuleVariableValues = mapOf(),
            programVariableValues = programVariables,
            supplementaryValues = mapOf(),
            dataItemValues = mapOf(),
            namedValues = mapOf(),
        )

        return customIntent.request()?.arguments()?.associate { argument ->
            val expression = Expression(argument.value(), ExpressionMode.ANDROID_CUSTOM_INTENT_EXPRESSION, false)
            val expressionValue = expression.evaluate({ _ -> null }, data)

            val argumentValue = when (expressionValue) {
                is Double ->
                    if (expressionValue.rem(1) == 0.0) {
                        expressionValue.toInt()
                    } else {
                        expressionValue
                    }

                else -> expressionValue
            }

            argument.key() to argumentValue
        } ?: emptyMap()
    }
}
