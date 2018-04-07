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

package org.hisp.dhis.android.sdk.utils.support.math;

import static org.hisp.dhis.android.sdk.utils.support.DateUtils.getMediumDateString;
import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.utils.services.VariableService;
import org.hisp.dhis.android.sdk.utils.support.ExpressionUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines a set of functions that can be used in expressions in {@link org.hisp.dhis.android.sdk.persistence.models.ProgramRule}s
 * and {@link org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator}s
 * Please note that {@link VariableService#initialize(Enrollment, Event)} needs to be called before
 * the functions in this class are called.
 */
public class ExpressionFunctions {
    public static final String CLASS_TAG = ExpressionFunctions.class.getSimpleName();
    public static final String NAMESPACE = "d2";

    /**
     * Function which will return zero if the argument is a negative number.
     *
     * @param value the value, must be a number.
     * @return a Double.
     */
    public static Double zing(Number value) {
        if (value == null) {
            return null;
        }

        return Math.max(0d, value.doubleValue());
    }

    /**
     * Function which will return one if the argument is zero or a positive
     * number, and zero if not.
     *
     * @param value the value, must be a number.
     * @return a Double.
     */
    public static Double oizp(Number value) {
        if (value == null) {
            return null;
        }

        return (value.doubleValue() >= 0d) ? 1d : 0d;
    }    
    
    /**
     * Function which will return the count of zero or positive values among the
     * given argument values.
     * 
     * @param values the arguments.
     * @return an Integer.
     */
    public static Integer zpvc(Number... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Argument is null or empty: " + values);
        }
        
        int count = 0;
        
        for (Number value : values) {
            if (value != null && value.doubleValue() >= 0d) {
                count++;
            }
        }
        
        return count;        
    }

    /**
     * Functions which will return the true value if the condition is true, false
     * value if not.
     * 
     * @param condititon the condition.
     * @param trueValue the true value.
     * @param falseValue the false value.
     * @return an Object.
     */
    public static Object condition(String condititon, Object trueValue, Object falseValue) {
        return ExpressionUtils.isTrue(condititon, null) ? trueValue : falseValue;        
    }
    
    /**
     * Function which will return the number of days between the two given dates.
     * 
     * @param start the start date. 
     * @param end the end date.
     * @return number of days between dates.
     */
    public static Integer daysBetween(String start, String end) throws ParseException {
        if(isEmpty(start) || isEmpty(end)) {
            return 0;
        }
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);
        return new Long((endDate.getMillis() - startDate.getMillis()) / 86400000).intValue();
    }

    public static Integer weeksBetween(String start, String end) throws ParseException {
        if(isEmpty(start) || isEmpty(end)) {
            return 0;
        }
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);
        int weeks = new Long((endDate.getMillis() - startDate.getMillis()) / (86400000 * 7)).intValue();
        return weeks;
    }

    public static Integer monthsBetween(String start, String end)  throws ParseException {
        if(isEmpty(start) || isEmpty(end)) {
            return 0;
        }
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);
        int months = Months.monthsBetween(startDate.withDayOfMonth(1), endDate.withDayOfMonth(1)).getMonths();
        return months;
    }

    public static Integer yearsBetween(String start, String end) {
        if(isEmpty(start) || isEmpty(end)) {
            return 0;
        }
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);
        int years = Years.yearsBetween(startDate.withDayOfMonth(1).withMonthOfYear(1), endDate.withDayOfMonth(1).withMonthOfYear(1)).getYears();
        return years;
    }

    public static Integer floor(Number value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        return new Double(Math.floor(value.doubleValue())).intValue();
    }

    public static Integer modulus(Number dividend, Number divisor) {
        if (dividend == null || divisor == null) {
            throw new IllegalArgumentException();
        }
        int rest = dividend.intValue() % divisor.intValue();
        return rest;
    }

    public static String concatenate(Object... values) {
        String returnString = "";
        for(Object value : values) {
            returnString += String.valueOf(value);;
        }
        return returnString;
    }

    public static String addDays(String date, Number daysToAdd) {
        if (date == null || daysToAdd == null) {
            throw new IllegalArgumentException();
        }
        DateTime dateTime = new DateTime(date);
        DateTime newDateTime = dateTime.plusDays(daysToAdd.intValue());
        return getMediumDateString(newDateTime.toDate());
    }

    public static Integer count(String variableName) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(variableName);
        Integer count = 0;
        if(programRuleVariable != null) {
            if(programRuleVariable.isHasValue()) {
                if(programRuleVariable.getAllValues() != null) {
                    count = programRuleVariable.getAllValues().size();
                } else {
                    //If there is a value found for the variable, the count is 1 even if there is no list of alternate values
                    //This happens for variables of "DATAELEMENT_CURRENT_STAGE" and "TEI_ATTRIBUTE"
                    count = 1;
                }
            }
        }
        return count;
    }

    public static Integer countIfZeroPos(String variableName) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(variableName);

        Integer count = 0;

        if(programRuleVariable != null) {
            if( programRuleVariable.isHasValue() ) {
                if(programRuleVariable.getAllValues() != null && programRuleVariable.getAllValues().size() > 0) {
                    for(int i = 0; i < programRuleVariable.getAllValues().size(); i++) {
                        Double value = getVariableValue(programRuleVariable, programRuleVariable.getAllValues().get(i));
                        if(value != null && value >= 0.0) {
                            count++;
                        }
                    }
                } else {
                    //The variable has a value, but no list of alternates. This means we only compare the elements real value
                    Double value = getVariableValue(programRuleVariable, programRuleVariable.getVariableValue());
                    if(value != null && value >= 0.0) {
                        count = 1;
                    }
                }
            }
        }

        return count;
    }

    public static Integer countIfValue(String variableName, String textToCompare) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(variableName);

        Integer count = 0;
        if(programRuleVariable != null) {

            if( programRuleVariable.isHasValue() ) {
                if( programRuleVariable.getAllValues() != null ) {
                    for(int i = 0; i < programRuleVariable.getAllValues().size(); i++) {
                        if(textToCompare.equals(programRuleVariable.getAllValues().get(i))) {
                            count++;
                        }
                    }
                } else {
                    //The variable has a value, but no list of alternates. This means we compare the standard variablevalue
                    if(textToCompare.equals(programRuleVariable.getVariableValue())) {
                        count = 1;
                    }
                }
            }
        }

        return count;
    }

    public static Double ceil(double value) {
        Double ceiled = Math.ceil(value);
        return ceiled;
    }

    public static Long round(double value) {
        Long rounded = Math.round(value);
        return rounded;
    }

    public static Boolean hasValue(String variableName) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(variableName);
        boolean valueFound = false;
        if(programRuleVariable != null) {
            if(programRuleVariable.isHasValue()){
                valueFound = true;
            }
        }
        return valueFound;
    }

    public static String lastEventDate(String variableName) {
        ProgramRuleVariable programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(variableName);
        String valueFound = "";
        if(programRuleVariable != null) {
            if(programRuleVariable.getVariableEventDate() != null) {
                valueFound = programRuleVariable.getVariableEventDate();
            }
        }
        return valueFound;
    }

    public static Boolean validatePattern(String inputToValidate, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(inputToValidate);
        boolean matchFound = matcher.matches();
        return matchFound;
    }

    public static Boolean validatePattern(long inputToValidate, String patternString) {
        String inputString = Long.toString(inputToValidate);
        return validatePattern(inputString, patternString);
    }

    /**
     * Return a substring from the beginning of a string up to a given length.
     *
     * @param inputString input value.
     * @param length of the substring.
     * @return the left substring.
     */
    public static String left(String inputString, int length) {
        if (inputString == null)
            return "";
        int safeLength = Math.min(Math.max(0, length), inputString.length());
        return inputString.substring(0, safeLength);
    }

    /**
     * Return a substring of the end of a string up to a given length.
     *
     * @param inputString input value.
     * @param length of the substring.
     * @return the right substring.
     */
    public static String right(String inputString, int length) {
        if (inputString == null)
            return "";
        int safeLength = Math.min(Math.max(0, length), inputString.length());
        return inputString.substring(inputString.length() - safeLength);
    }

    /**
     * Return the length of a given string.
     *
     * @param inputString input value.
     * @return the length of the string
     */
    public static Integer length(String inputString) {
        return inputString == null ? 0 : inputString.length();
    }

    /**
     * Split a string given a separator and get the nth item.
     *
     * @param inputString input value.
     * @param splitString separator value.
     * @param fieldIndex item index to get from the split.
     * @return the field after split.
     */
    public static String split(String inputString, String splitString, int fieldIndex) {
        if (inputString == null || splitString == null)
            return "";
        String[] fields = inputString == null ? new String[0] : inputString.split(Pattern.quote(splitString));
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[fieldIndex] : "";
    }

    /**
     * Return a substring from a start index up to an end index (not included).
     *
     * @param inputString input value.
     * @param startIndex start index.
     * @param endIndex end index (not included)
     * @return the substring.
     */
    public static String substring(String inputString, int startIndex, int endIndex) {
        if (inputString == null)
            return "";
        int safeStartIndex = Math.min(Math.max(0, startIndex), inputString.length());
        int safeEndIndex = Math.min(Math.max(0, endIndex), inputString.length());
        return inputString.substring(safeStartIndex, safeEndIndex);
    }

    private static Double getVariableValue(ProgramRuleVariable programRuleVariable, String evaluated) {
        if (evaluated == null)
            return null;
        try {
            String value = VariableService.processSingleValue(evaluated, programRuleVariable.getVariableType());
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
