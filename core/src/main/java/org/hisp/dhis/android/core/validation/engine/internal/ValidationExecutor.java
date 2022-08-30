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

package org.hisp.dhis.android.core.validation.engine.internal;

import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.internal.PeriodHelper;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.hisp.dhis.android.core.validation.ValidationRule;
import org.hisp.dhis.android.core.validation.ValidationRuleExpression;
import org.hisp.dhis.android.core.validation.ValidationRuleOperator;
import org.hisp.dhis.android.core.validation.engine.ValidationResultSideEvaluation;
import org.hisp.dhis.android.core.validation.engine.ValidationResultViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class ValidationExecutor {

    private final ExpressionService expressionService;

    @Inject
    ValidationExecutor(ExpressionService expressionService) {
        this.expressionService = expressionService;
    }

    List<ValidationResultViolation> evaluateRule(ValidationRule rule,
                                                 OrganisationUnit organisationUnit,
                                                 Map<DimensionalItemObject, Double> valueMap,
                                                 Map<String, Constant> constantMap,
                                                 Map<String, Integer> orgunitGroupMap,
                                                 Period period,
                                                 String attributeOptionComboId) {

        List<ValidationResultViolation> violations = new ArrayList<>();

        if (!rule.organisationUnitLevels().isEmpty() &&
                organisationUnit != null &&
                !rule.organisationUnitLevels().contains(organisationUnit.level())) {
            return violations;
        }

        Integer days = PeriodHelper.getDays(period);

        Double leftSideValue = (Double) expressionService.getExpressionValue(rule.leftSide().expression(), valueMap,
                constantMap, orgunitGroupMap, days, rule.leftSide().missingValueStrategy());
        Double rightSideValue = (Double) expressionService.getExpressionValue(rule.rightSide().expression(), valueMap,
                constantMap, orgunitGroupMap, days, rule.rightSide().missingValueStrategy());

        if (isViolation(rule, leftSideValue, rightSideValue)) {
            ValidationResultSideEvaluation leftSide = buildSideResult(leftSideValue, rule.leftSide(), valueMap,
                    constantMap, orgunitGroupMap, days);
            ValidationResultSideEvaluation rightSide = buildSideResult(rightSideValue, rule.rightSide(), valueMap,
                    constantMap, orgunitGroupMap, days);

            violations.add(ValidationResultViolation.builder()
                    .period(period.periodId())
                    .organisationUnitUid(organisationUnit.uid())
                    .attributeOptionComboUid(attributeOptionComboId)
                    .validationRule(rule)
                    .leftSideEvaluation(leftSide)
                    .rightSideEvaluation(rightSide)
                    .build());
        }
        return violations;
    }

    private boolean isViolation(ValidationRule rule, Double leftSide, Double rightSide) {
        if (ValidationRuleOperator.compulsory_pair.equals(rule.operator())) {
            return (leftSide == null) != (rightSide == null);
        }

        if (ValidationRuleOperator.exclusive_pair.equals(rule.operator())) {
            return leftSide != null && rightSide != null;
        }

        Double leftSideValue = leftSide;
        if (leftSide == null) {
            if (rule.leftSide().missingValueStrategy() == MissingValueStrategy.NEVER_SKIP) {
                leftSideValue = 0d;
            } else {
                return false;
            }
        }

        Double rightSideValue = rightSide;
        if (rightSide == null) {
            if (rule.rightSide().missingValueStrategy() == MissingValueStrategy.NEVER_SKIP) {
                rightSideValue = 0d;
            } else {
                return false;
            }
        }

        String test = leftSideValue
                + rule.operator().getMathematicalOperator()
                + rightSideValue;
        return !(Boolean) expressionService.getExpressionValue(test);
    }

    private ValidationResultSideEvaluation buildSideResult(Double value,
                                                           ValidationRuleExpression side,
                                                           Map<DimensionalItemObject, Double> valueMap,
                                                           Map<String, Constant> constantMap,
                                                           Map<String, Integer> orgunitGroupMap,
                                                           Integer days) {
        return ValidationResultSideEvaluation.builder()
                .value(value)
                .dataElementUids(expressionService.getDataElementOperands(side.expression()))
                .displayExpression(expressionService.getExpressionDescription(side.expression(), constantMap))
                .regeneratedExpression(
                        expressionService.regenerateExpression(side.expression(), valueMap, constantMap,
                                orgunitGroupMap, days))
                .build();
    }
}