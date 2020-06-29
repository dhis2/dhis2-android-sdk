/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.program.programindicatorengine;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hisp.dhis.android.core.program.programindicatorengine.DateUtils.getMediumDateString;
import static org.hisp.dhis.android.core.program.programindicatorengine.StringUtils.isEmpty;

/**
 * This class has been copied/pasted from Android Tracker Capture SDK. Non-compatible methods have been commented out.
 */
/*
 * Defines a set of functions that can be used in expressions in
 * {@link org.hisp.dhis.android.sdk.persistence.models.ProgramRule}s
 * and {@link org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator}s
 * Please note that {@link VariableService#initialize(Enrollment, Event)} needs to be called before
 * the functions in this class are called.
 */

@SuppressWarnings("PMD.GodClass")
public final class ExpressionFunctions {
    public static final String NAMESPACE = "d2";

    private ExpressionFunctions() {
        // no instances
    }

    public static String addDays(String date, Number daysToAdd) {
        if (date == null || daysToAdd == null) {
            throw new IllegalArgumentException();
        }
        DateTime dateTime = new DateTime(date);
        DateTime newDateTime = dateTime.plusDays(daysToAdd.intValue());
        return getMediumDateString(newDateTime.toDate());
    }

    /*
    public static Boolean hasValue(String variableName) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap()
            .get(variableName);
        boolean valueFound = false;
        if(programRuleVariable != null) {
            if(programRuleVariable.isHasValue()){
                valueFound = true;
            }
        }
        return valueFound;
    }

    public static String lastEventDate(String variableName) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap()
            .get(variableName);
        String valueFound = "";
        if(programRuleVariable != null) {
            if(programRuleVariable.getVariableEventDate() != null) {
                valueFound = programRuleVariable.getVariableEventDate();
            }
        }
        return valueFound;
    }
    */

}
