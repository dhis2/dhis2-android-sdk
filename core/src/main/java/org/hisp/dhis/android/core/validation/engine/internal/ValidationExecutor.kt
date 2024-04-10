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
package org.hisp.dhis.android.core.validation.engine.internal

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.parser.internal.service.ExpressionServiceContext
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.ValidationRuleExpression
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.hisp.dhis.android.core.validation.engine.ValidationResultSideEvaluation
import org.hisp.dhis.android.core.validation.engine.ValidationResultViolation
import org.hisp.dhis.antlr.ParserException
import org.koin.core.annotation.Singleton

@Singleton
internal class ValidationExecutor(private val expressionService: ExpressionService) {
    fun evaluateRule(
        rule: ValidationRule,
        organisationUnit: OrganisationUnit?,
        context: ExpressionServiceContext,
        period: Period,
        attributeOptionComboId: String?,
    ): ValidationResultViolation? {
        return if (shouldSkipOrgunitLevel(rule, organisationUnit)) {
            null
        } else {
            try {
                val leftSideValue = evaluateSide(rule.leftSide(), context)
                val rightSideValue = evaluateSide(rule.rightSide(), context)

                if (isViolation(rule, leftSideValue, rightSideValue)) {
                    val leftSide = buildSideResult(leftSideValue, rule.leftSide(), context)
                    val rightSide = buildSideResult(rightSideValue, rule.rightSide(), context)
                    ValidationResultViolation.builder()
                        .period(period.periodId())
                        .organisationUnitUid(organisationUnit!!.uid())
                        .attributeOptionComboUid(attributeOptionComboId)
                        .validationRule(rule)
                        .leftSideEvaluation(leftSide)
                        .rightSideEvaluation(rightSide)
                        .build()
                } else {
                    null
                }
            } catch (e: ParserException) {
                null
            }
        }
    }

    private fun evaluateSide(side: ValidationRuleExpression, context: ExpressionServiceContext): Double? {
        return expressionService.getExpressionValue(
            expression = side.expression(),
            context = context,
            missingValueStrategy = side.missingValueStrategy(),
            ignoreParseErrors = false,
        ) as Double?
    }

    private fun isViolation(rule: ValidationRule, leftSide: Double?, rightSide: Double?): Boolean {
        return when (rule.operator()) {
            ValidationRuleOperator.compulsory_pair -> leftSide == null != (rightSide == null)
            ValidationRuleOperator.exclusive_pair -> leftSide != null && rightSide != null
            else -> {
                val leftSideValue = leftSide
                    ?: if (rule.leftSide().missingValueStrategy() == MissingValueStrategy.NEVER_SKIP) {
                        0.0
                    } else {
                        null
                    }

                val rightSideValue: Double? = rightSide
                    ?: if (rule.rightSide().missingValueStrategy() == MissingValueStrategy.NEVER_SKIP) {
                        0.0
                    } else {
                        null
                    }

                if (leftSideValue == null || rightSideValue == null) {
                    false
                } else {
                    val test = "$leftSideValue ${rule.operator().mathematicalOperator} $rightSideValue"
                    !(expressionService.getExpressionValue(test) as Boolean)
                }
            }
        }
    }

    private fun buildSideResult(
        value: Double?,
        side: ValidationRuleExpression,
        context: ExpressionServiceContext,
    ): ValidationResultSideEvaluation {
        return ValidationResultSideEvaluation.builder()
            .value(value)
            .dataElementUids(expressionService.getDataElementOperands(side.expression()))
            .displayExpression(expressionService.getExpressionDescription(side.expression(), context.constantMap))
            .regeneratedExpression(expressionService.regenerateExpression(side.expression(), context))
            .build()
    }

    private fun shouldSkipOrgunitLevel(
        rule: ValidationRule,
        organisationUnit: OrganisationUnit?,
    ): Boolean {
        return rule.organisationUnitLevels().isNotEmpty() &&
            organisationUnit != null &&
            !rule.organisationUnitLevels().contains(organisationUnit.level())
    }
}
