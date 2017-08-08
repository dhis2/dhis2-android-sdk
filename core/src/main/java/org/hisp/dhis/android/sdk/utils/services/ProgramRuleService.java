/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.utils.services;

import android.util.Log;

import org.apache.commons.jexl2.JexlException;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType;
import org.hisp.dhis.android.sdk.utils.support.ExpressionUtils;
import org.hisp.dhis.android.sdk.utils.support.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hisp.dhis.android.sdk.utils.support.ExpressionUtils.isBoolean;
import static org.hisp.dhis.android.sdk.utils.support.ExpressionUtils.isNumeric;

public class ProgramRuleService {

    private static final String CLASS_TAG = ProgramRuleService.class.getSimpleName();

    private static final Pattern CONDITION_PATTERN = Pattern.compile("([#AV])\\{(.+?)\\}");

    // Regex to match text within single quotes. Disallowed chars: space: ' ' and single quote: '
    // do no match on quotes with empty content: ''
    private static final Pattern CONDITION_PATTERN_SINGLE_QUOTES = Pattern.compile("'([^' ]+)'");

    private static ProgramRuleService programRuleService;

    public static ProgramRuleService getInstance() {
        return programRuleService;
    }

    public static ProgramRuleService getProgramRuleService() {
        return programRuleService;
    }

    public static void setProgramRuleService(ProgramRuleService programRuleService) {
        ProgramRuleService.programRuleService = programRuleService;
    }

    static {
        programRuleService = new ProgramRuleService();
    }

    /**
     * Evaluates a passed expression from a {@link ProgramRule} to true or false.
     * Please note that {@link VariableService#initialize(Enrollment, Event)} must be called prior
     * to calling this method.
     *
     * @param condition
     * @return
     */
    public static boolean evaluate(final String condition) {
        String conditionReplaced = getReplacedCondition(condition);
        boolean isTrue = false;
        try {
            isTrue = ExpressionUtils.isTrue(conditionReplaced, null);
        } catch (JexlException jxlException) {
            jxlException.printStackTrace();
        }
        return isTrue;
    }

    /**
     * Returns a condition with replaced values for {@link ProgramRuleVariable}s.
     * Please note that {@link VariableService#initialize(Enrollment, Event)} must be called prior
     * to calling this method.
     *
     * @param condition
     * @return
     */
    public static String getReplacedCondition(String condition) {
        if (condition == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();

        Matcher matcher = CONDITION_PATTERN.matcher(condition);

        while (matcher.find()) {
            String value;
            String variablePrefix = matcher.group(1);
            String variableName = matcher.group(2);
            value = VariableService.getReplacementForProgramRuleVariable(variableName);

            if (isNumericAndStartsWithDecimalSeparator(value)) {
                value = String.format("0%s", value);
            } else if (isNumericAndEndsWithDecimalSeparator(value)) {
                value = String.format("%s0", value);
            }

            if (!isNumeric(value) && !isBoolean(value)) {
                value = '\'' + value + '\'';
            }
            matcher.appendReplacement(buffer, value);
        }

        return TextUtils.appendTail(matcher, buffer);
    }

    private static boolean isNumericAndStartsWithDecimalSeparator(String value) {
        return value.startsWith(".") && isNumeric(value.substring(1, value.length()));
    }

    private static boolean isNumericAndEndsWithDecimalSeparator(String value) {
        return value.endsWith(".") && isNumeric(value.substring(0, value.length() - 1));
    }

    /**
     * Calculates and returns the value of a passed condition from a {@link ProgramRuleAction} or
     * {@link ProgramRuleVariable}.
     * Please note that {@link VariableService#initialize(Enrollment, Event)} must be called prior
     * to calling this method.
     *
     * @param condition
     * @return
     */
    public static String getCalculatedConditionValue(String condition) {
        String conditionReplaced = getReplacedCondition(condition);
        Object result = ExpressionUtils.evaluate(conditionReplaced, null);
        String stringResult = String.valueOf(result);
        return stringResult;
    }

    /**
     * Returns a list of Uids of {@link DataElement}s contained in the given {@link ProgramRule}.
     * Please note that {@link VariableService#initialize(Enrollment, Event)} must be called prior
     * to calling this method.
     *
     * @param programRule
     * @return
     */
    public static List<String> getDataElementsInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();
        List<String> dataElementsInRule = getDataElementsInSingleQuotes(condition);

        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        while (matcher.find()) {
            String variableName = matcher.group(2);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null && programRuleVariable.getDataElement() != null) {
                dataElementsInRule.add(programRuleVariable.getDataElement());
            }
        }

        for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
            if (programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.ASSIGN) && programRuleAction.getContent() != null) {
                String programRuleVariableName = programRuleAction.getContent().substring(2, programRuleAction.getContent().length() - 1);
                ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(programRuleVariableName);
                if (programRuleVariable != null && programRuleVariable.getDataElement() != null) {
                    dataElementsInRule.add(programRuleVariable.getDataElement());
                }
            }
            if (programRuleAction.getDataElement() != null) {
                dataElementsInRule.add(programRuleAction.getDataElement());
            }
        }

        return dataElementsInRule;
    }

    private static List<String> getDataElementsInSingleQuotes(String condition) {

        List<String> dataElementsInRule = new ArrayList<>();

        Matcher matcher = CONDITION_PATTERN_SINGLE_QUOTES.matcher(condition);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null && programRuleVariable.getDataElement() != null) {
                dataElementsInRule.add(programRuleVariable.getDataElement());
            }
        }
        return dataElementsInRule;
    }

    /**
     * Returns a list of Uids of {@link TrackedEntityAttribute}s contained in the given {@link ProgramRule}.
     * Please note that {@link VariableService#initialize(Enrollment, Event)} must be called prior
     * to calling this method.
     *
     * @param programRule
     * @return
     */
    public static List<String> getTrackedEntityAttributesInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();

        List<String> trackedEntityAttributesInRule = getTrackedEntityAttributesInSingleQuotes(condition);

        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        while (matcher.find()) {
            String variableName = matcher.group(2);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null && programRuleVariable.getTrackedEntityAttribute() != null) {
                trackedEntityAttributesInRule.add(programRuleVariable.getTrackedEntityAttribute());
            }
        }

        for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
            if (programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.ASSIGN) && programRuleAction.getContent() != null) {
                String programRuleVariableName = programRuleAction.getContent().substring(2, programRuleAction.getContent().length() - 1);
                ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(programRuleVariableName);
                if (programRuleVariable != null && programRuleVariable.getTrackedEntityAttribute() != null) {
                    trackedEntityAttributesInRule.add(programRuleVariable.getTrackedEntityAttribute());
                }
            }
        }

        return trackedEntityAttributesInRule;
    }

    private static List<String> getTrackedEntityAttributesInSingleQuotes(String condition) {

        List<String> trackedEntityAttributesInRule = new ArrayList<>();

        Matcher matcher = CONDITION_PATTERN_SINGLE_QUOTES.matcher(condition);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null && programRuleVariable.getTrackedEntityAttribute() != null) {
                trackedEntityAttributesInRule.add(programRuleVariable.getTrackedEntityAttribute());
            }
        }
        return trackedEntityAttributesInRule;
    }
}
