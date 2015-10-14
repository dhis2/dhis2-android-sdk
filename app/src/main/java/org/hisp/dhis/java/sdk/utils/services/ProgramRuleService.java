/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.java.sdk.utils.services;

import org.apache.commons.jexl2.JexlException;
import org.hisp.dhis.java.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.java.sdk.core.models.DataValue;
import org.hisp.dhis.java.sdk.core.models.Event;
import org.hisp.dhis.java.sdk.core.models.ProgramRule;
import org.hisp.dhis.java.sdk.core.models.ProgramRuleVariable;
import org.hisp.dhis.java.sdk.utils.support.ExpressionUtils;
import org.hisp.dhis.java.sdk.utils.support.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
public class ProgramRuleService {

    private static final String CLASS_TAG = ProgramRuleService.class.getSimpleName();

    private static final Pattern CONDITION_PATTERN = Pattern.compile("#\\{(.+)\\}");

    public static boolean evaluate(final String condition, Event event) {

        StringBuffer buffer = new StringBuffer();

        Matcher matcher = CONDITION_PATTERN.matcher(condition);

        while (matcher.find()) {

            String variableName = matcher.group(1);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable == null) {
                return false;
            }
            DataValue dataValue = null;
            if (event.getDataValues() != null) {
                for (DataValue dv : event.getDataValues()) {
                    if (dv.getDataElement().equals(programRuleVariable.getDataElement())) {
                        dataValue = dv;
                        break;
                    }
                }
            }

            String value;
            if (dataValue != null) {
                value = dataValue.getValue();
            } else {
                return false;
            }

            if (!isEmpty(value)) {
                value = '\'' + value + '\'';
            } else {
                value = "''";
            }
            matcher.appendReplacement(buffer, value);
        }

        String conditionReplaced = TextUtils.appendTail(matcher, buffer);
        boolean isTrue = false;
        try
        {
             isTrue = ExpressionUtils.isTrue(conditionReplaced, null);
        }
        catch(JexlException jxlException)
        {
            jxlException.printStackTrace();
        }
        return isTrue;
    }

    public static List<String> getDataElementsInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        List<String> dataElementsInRule = new ArrayList<>();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null) {
                dataElementsInRule.add(programRuleVariable.getDataElement());
            }
        }

        return dataElementsInRule;
    }
}
