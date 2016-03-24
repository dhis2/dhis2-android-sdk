/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.common.utils;

import org.apache.commons.jexl2.JexlException;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleVariableService;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleActionType;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class ProgramRuleUtils {

    private static final Pattern CONDITION_PATTERN = Pattern.compile("([#AV])\\{(.+?)\\}");

    private final IProgramRuleVariableService programRuleVariableService;
    private final VariableUtils variableUtils;
    private final ExpressionUtils expressionUtils;

    public ProgramRuleUtils(IProgramRuleVariableService programRuleVariableService, VariableUtils
            variableUtils, ExpressionUtils expressionUtils) {
        this.programRuleVariableService = programRuleVariableService;
        this.variableUtils = variableUtils;
        this.expressionUtils = expressionUtils;
    }

    /**
     * Evaluates a passed expression from a {@link ProgramRule} to true or false.
     * Please note that {@link VariableUtils#initialize(org.hisp.dhis.client.sdk.models
     * .enrollment.Enrollment,
     * org.hisp.dhis.client.sdk.models.event.Event)} must be called prior
     * to calling this method.
     *
     * @param condition
     * @return
     */
    public boolean evaluate(final String condition) {
        String conditionReplaced = getReplacedCondition(condition);
        boolean isTrue = false;
        try {
            isTrue = expressionUtils.isTrue(conditionReplaced, null);
        } catch (JexlException jxlException) {
            jxlException.printStackTrace();
        }
        return isTrue;
    }

    /**
     * Returns a condition with replaced values for {@link ProgramRuleVariable}s.
     * Please note that {@link VariableUtils#initialize(org.hisp.dhis.client.sdk.models
     * .enrollment.Enrollment,
     * org.hisp.dhis.client.sdk.models.event.Event)} must be called prior
     * to calling this method.
     *
     * @param condition
     * @return
     */
    public String getReplacedCondition(String condition) {
        StringBuffer buffer = new StringBuffer();

        Matcher matcher = CONDITION_PATTERN.matcher(condition);

        while (matcher.find()) {
            String value;
            String variablePrefix = matcher.group(1);
            String variableName = matcher.group(2);
            value = variableUtils.getReplacementForProgramRuleVariable(variableName);
            if (!isNumeric(value) && !Boolean.TRUE.toString().equals(value) && !Boolean.FALSE
                    .toString().equals(value)) {
                value = '\'' + value + '\'';
            }
            matcher.appendReplacement(buffer, value);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Calculates and returns the value of a passed condition from a {@link
     * org.hisp.dhis.client.sdk.models.program.ProgramRuleAction} or
     * {@link ProgramRuleVariable}.
     * Please note that {@link VariableUtils#initialize(org.hisp.dhis.client.sdk.models
     * .enrollment.Enrollment,
     * org.hisp.dhis.client.sdk.models.event.Event)} must be called prior
     * to calling this method.
     *
     * @param condition
     * @return
     */
    public String getCalculatedConditionValue(String condition) {
        String conditionReplaced = getReplacedCondition(condition);
        Object result = expressionUtils.evaluate(conditionReplaced, null);
        String stringResult = String.valueOf(result);
        return stringResult;
    }

    /**
     * Returns a list of Uids of {@link org.hisp.dhis.client.sdk.models.dataelement.DataElement}s
     * contained in the given
     * {@link ProgramRule}.
     * Please note that {@link VariableUtils#initialize(org.hisp.dhis.client.sdk.models
     * .enrollment.Enrollment,
     * org.hisp.dhis.client.sdk.models.event.Event)} must be called prior
     * to calling this method.
     *
     * @param programRule
     * @return
     */
    public List<String> getDataElementsInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        List<String> dataElementsInRule = new ArrayList<>();

        while (matcher.find()) {
            String variableName = matcher.group(2);
            ProgramRuleVariable programRuleVariable = programRuleVariableService.getByName
                    (programRule.getProgram(), variableName);
            if (programRuleVariable != null && programRuleVariable.getDataElement() != null) {
                dataElementsInRule.add(programRuleVariable.getDataElement().getUId());
            }
        }

        for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
            if (programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.ASSIGN)
                    && programRuleAction.getContent() != null) {
                String programRuleVariableName = programRuleAction.getContent().substring(2,
                        programRuleAction.getContent().length() - 1);
                ProgramRuleVariable programRuleVariable = variableUtils.getProgramRuleVariableMap
                        ().get(programRuleVariableName);
                if (programRuleVariable.getDataElement() != null) {
                    dataElementsInRule.add(programRuleVariable.getDataElement().getUId());
                }
            }
            if (programRuleAction.getDataElement() != null) {
                dataElementsInRule.add(programRuleAction.getDataElement().getUId());
            }
        }

        return dataElementsInRule;
    }

    /**
     * Returns a list of Uids of {@link org.hisp.dhis.client.sdk.models.trackedentity
     * .TrackedEntityAttribute}s contained
     * in the given {@link ProgramRule}.
     * Please note that {@link VariableUtils#initialize(org.hisp.dhis.client.sdk.models
     * .enrollment.Enrollment,
     * org.hisp.dhis.client.sdk.models.event.Event)} must be called prior
     * to calling this method.
     *
     * @param programRule
     * @return
     */
    public List<String> getTrackedEntityAttributesInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        List<String> trackedEntityAttributesInRule = new ArrayList<>();

        while (matcher.find()) {
            String variableName = matcher.group(2);
            ProgramRuleVariable programRuleVariable = programRuleVariableService.getByName
                    (programRule.getProgram(), variableName);
            if (programRuleVariable != null && programRuleVariable.getTrackedEntityAttribute() !=
                    null) {
                trackedEntityAttributesInRule.add(programRuleVariable.getTrackedEntityAttribute()
                        .getUId());
            }
        }

        for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
            if (programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.ASSIGN)
                    && programRuleAction.getContent() != null) {
                String programRuleVariableName = programRuleAction.getContent().substring(2,
                        programRuleAction.getContent().length() - 1);
                ProgramRuleVariable programRuleVariable = variableUtils.getProgramRuleVariableMap
                        ().get(programRuleVariableName);
                if (programRuleVariable.getTrackedEntityAttribute() != null) {
                    trackedEntityAttributesInRule.add(programRuleVariable
                            .getTrackedEntityAttribute().getUId());
                }
            }
        }

        return trackedEntityAttributesInRule;
    }
}
