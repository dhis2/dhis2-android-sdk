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

package org.hisp.dhis.client.sdk.rules;

import org.apache.commons.jexl2.JexlException;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.commons.util.ExpressionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleEngineExecution {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("[A#CV]\\{(\\w+.?\\w*)\\}");

    private static List<DhisFunction> dhisFunctions = Arrays.asList(
            new DhisFunction("d2:daysBetween", 2) {
                @Override
                public String execute(String expression) {
                    return "-1";
                }
            },
        new DhisFunction("d2:weeksBetween", 2) {
            @Override
            public String execute(String expression) {
                return null;
            }
        }/*,
        new DhisFunction("d2:monthsBetween", 2),
        new DhisFunction("d2:yearsBetween", 2),
        new DhisFunction("d2:floor", 1),
        new DhisFunction("d2:modulus", 2),
        new DhisFunction("d2:concatenate", null),
        new DhisFunction("d2:addDays", 2),
        new DhisFunction("d2:zing", 1),
        new DhisFunction("d2:oizp", 1),
        new DhisFunction("d2:count", 1),
        new DhisFunction("d2:countIfZeroPos", 1),
        new DhisFunction("d2:countIfValue", 2),
        new DhisFunction("d2:ceil", 1),
        new DhisFunction("d2:round", 1)*/,
        new DhisFunction("d2:hasValue", 1) {
            @Override
            public String execute(String expression) {
                return "true";
            }
        }/*,
        new DhisFunction("d2:lastEventDate", 1),
        new DhisFunction("d2:validatePattern", 2),
        new DhisFunction("d2:addControlDigits", 1),
        new DhisFunction("d2:checkControlDigits", 1),
        new DhisFunction("d2:left", 2),
        new DhisFunction("d2:right", 2),
        new DhisFunction("d2:substring", 3),
        new DhisFunction("d2:split", 3),
        new DhisFunction("d2:length", 1)*/);

    /**
     * else if(dhisFunction.getName() === "d2:daysBetween") {
     var firstdate = $filter('trimquotes')(parameters[0]);
     var seconddate = $filter('trimquotes')(parameters[1]);
     firstdate = moment(firstdate);
     seconddate = moment(seconddate);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'days'));
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:weeksBetween") {
     var firstdate = $filter('trimquotes')(parameters[0]);
     var seconddate = $filter('trimquotes')(parameters[1]);
     firstdate = moment(firstdate);
     seconddate = moment(seconddate);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'weeks'));
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:monthsBetween") {
     var firstdate = $filter('trimquotes')(parameters[0]);
     var seconddate = $filter('trimquotes')(parameters[1]);
     firstdate = moment(firstdate);
     seconddate = moment(seconddate);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'months'));
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:yearsBetween") {
     var firstdate = $filter('trimquotes')(parameters[0]);
     var seconddate = $filter('trimquotes')(parameters[1]);
     firstdate = moment(firstdate);
     seconddate = moment(seconddate);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'years'));
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:floor") {
     var floored = Math.floor(parameters[0]);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, floored);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:modulus") {
     var dividend = Number(parameters[0]);
     var divisor = Number(parameters[1]);
     var rest = dividend % divisor;
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, rest);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:concatenate") {
     var returnString = "'";
     for (var i = 0; i < parameters.length; i++) {
     returnString += parameters[i];
     }
     returnString += "'";
     expression = expression.replace(callToThisFunction, returnString);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:addDays") {
     var date = $filter('trimquotes')(parameters[0]);
     var daystoadd = $filter('trimquotes')(parameters[1]);
     var newdate = DateUtils.format( moment(date, CalendarService.getSetting().momentFormat).add(daystoadd, 'days') );
     var newdatestring = "'" + newdate + "'";
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, newdatestring);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:zing") {
     var number = parameters[0];
     if( number < 0 ) {
     number = 0;
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, number);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:oizp") {
     var number = parameters[0];
     var output = 1;
     if( number < 0 ) {
     output = 0;
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, output);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:count") {
     var variableName = parameters[0];
     var variableObject = variablesHash[variableName];
     var count = 0;
     if(variableObject)
     {
     if(variableObject.hasValue){
     if(variableObject.allValues)
     {
     count = variableObject.allValues.length;
     } else {
     //If there is a value found for the variable, the count is 1 even if there is no list of alternate values
     //This happens for variables of "DATAELEMENT_CURRENT_STAGE" and "TEI_ATTRIBUTE"
     count = 1;
     }
     }
     }
     else
     {
     $log.warn("could not find variable to count: " + variableName);
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, count);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:countIfZeroPos") {
     var variableName = $filter('trimvariablequalifiers') (parameters[0]);
     var variableObject = variablesHash[variableName];

     var count = 0;
     if(variableObject)
     {
     if( variableObject.hasValue ) {
     if(variableObject.allValues && variableObject.allValues.length > 0)
     {
     for(var i = 0; i < variableObject.allValues.length; i++)
     {
     if(variableObject.allValues[i] >= 0) {
     count++;
     }
     }
     }
     else {
     //The variable has a value, but no list of alternates. This means we only compare the elements real value
     if(variableObject.variableValue >= 0) {
     count = 1;
     }
     }
     }
     }
     else
     {
     $log.warn("could not find variable to countifzeropos: " + variableName);
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, count);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:countIfValue") {
     var variableName = parameters[0];
     var variableObject = variablesHash[variableName];

     var valueToCompare = VariableService.processValue(parameters[1],variableObject.variableType);

     var count = 0;
     if(variableObject)
     {
     if( variableObject.hasValue )
     {
     if( variableObject.allValues )
     {
     for(var i = 0; i < variableObject.allValues.length; i++)
     {
     if(valueToCompare === variableObject.allValues[i]) {
     count++;
     }
     }
     } else {
     //The variable has a value, but no list of alternates. This means we compare the standard variablevalue
     if(valueToCompare === variableObject.variableValue) {
     count = 1;
     }
     }

     }
     }
     else
     {
     $log.warn("could not find variable to countifvalue: " + variableName);
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, count);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:ceil") {
     var ceiled = Math.ceil(parameters[0]);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, ceiled);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:round") {
     var rounded = Math.round(parameters[0]);
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, rounded);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:hasValue") {
     var variableName = parameters[0];
     var variableObject = variablesHash[variableName];
     var valueFound = false;
     if(variableObject)
     {
     if(variableObject.hasValue){
     valueFound = true;
     }
     }
     else
     {
     $log.warn("could not find variable to check if has value: " + variableName);
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, valueFound);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:lastEventDate") {
     var variableName = parameters[0];
     var variableObject = variablesHash[variableName];
     var valueFound = "''";
     if(variableObject)
     {
     if(variableObject.variableEventDate){
     valueFound = VariableService.processValue(variableObject.variableEventDate, 'DATE');
     }
     else {
     $log.warn("no last event date found for variable: " + variableName);
     }
     }
     else
     {
     $log.warn("could not find variable to check last event date: " + variableName);
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, valueFound);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:validatePattern") {
     var inputToValidate = parameters[0].toString();
     var pattern = parameters[1];
     var regEx = new RegExp(pattern,'g');
     var match = inputToValidate.match(regEx);

     var matchFound = false;
     if(match !== null && inputToValidate === match[0]) {
     matchFound = true;
     }

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, matchFound);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:addControlDigits") {

     var baseNumber = parameters[0];
     var baseDigits = baseNumber.split('');
     var error = false;

     var firstDigit = 0;
     var secondDigit = 0;

     if(baseDigits && baseDigits.length < 10 ) {
     var firstSum = 0;
     var baseNumberLength = baseDigits.length;
     //weights support up to 9 base digits:
     var firstWeights = [3,7,6,1,8,9,4,5,2];
     for(var i = 0; i < baseNumberLength && !error; i++) {
     firstSum += parseInt(baseDigits[i]) * firstWeights[i];
     }
     firstDigit = firstSum % 11;

     //Push the first digit to the array before continuing, as the second digit is a result of the
     //base digits and the first control digit.
     baseDigits.push(firstDigit);
     //Weights support up to 9 base digits plus first control digit:
     var secondWeights = [5,4,3,2,7,6,5,4,3,2];
     var secondSum = 0;
     for(var i = 0; i < baseNumberLength + 1 && !error; i++) {
     secondSum += parseInt(baseDigits[i]) * secondWeights[i];
     }
     secondDigit = secondSum % 11;

     if(firstDigit === 10) {
     $log.warn("First control digit became 10, replacing with 0");
     firstDigit = 0;
     }
     if(secondDigit === 10) {
     $log.warn("Second control digit became 10, replacing with 0");
     secondDigit = 0;
     }
     }
     else
     {
     $log.warn("Base nuber not well formed(" + baseNumberLength + " digits): " + baseNumber);
     }

     if(!error) {
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, baseNumber + firstDigit + secondDigit);
     expressionUpdated = true;
     }
     else
     {
     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, baseNumber);
     expressionUpdated = true;
     }
     }
     else if(dhisFunction.name === "d2:checkControlDigits") {
     $log.warn("checkControlDigits not implemented yet");

     //Replace the end evaluation of the dhis function:
     expression = expression.replace(callToThisFunction, parameters[0]);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:left") {
     var string = String(parameters[0]);
     var numChars = string.length < parameters[1] ? string.length : parameters[1];
     var returnString =  string.substring(0,numChars);
     returnString = VariableService.processValue(returnString, 'TEXT');
     expression = expression.replace(callToThisFunction, returnString);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:right") {
     var string = String(parameters[0]);
     var numChars = string.length < parameters[1] ? string.length : parameters[1];
     var returnString =  string.substring(string.length - numChars, string.length);
     returnString = VariableService.processValue(returnString, 'TEXT');
     expression = expression.replace(callToThisFunction, returnString);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:substring") {
     var string = String(parameters[0]);
     var startChar = string.length < parameters[1] - 1 ? -1 : parameters[1];
     var endChar = string.length < parameters[2] ? -1 : parameters[2];
     if(startChar < 0 || endChar < 0) {
     expression = expression.replace(callToThisFunction, "''");
     expressionUpdated = true;
     } else {
     var returnString =  string.substring(startChar, endChar);
     returnString = VariableService.processValue(returnString, 'TEXT');
     expression = expression.replace(callToThisFunction, returnString);
     expressionUpdated = true;
     }
     }
     else if(dhisFunction.name === "d2:split") {
     var string = String(parameters[0]);
     var splitArray = string.split(parameters[1]);
     var returnPart = "";
     if (splitArray.length >= parameters[2]) {
     returnPart = splitArray[parameters[2]];
     }
     returnPart = VariableService.processValue(returnPart, 'TEXT');
     expression = expression.replace(callToThisFunction, returnPart);
     expressionUpdated = true;
     }
     else if(dhisFunction.name === "d2:length") {
     expression = expression.replace(callToThisFunction, String(parameters[0]).length);
     expressionUpdated = true;
     }
     */


    public static List<RuleEffect> execute(
            List<ProgramRule> rules, RuleEngineVariableValueMap variableValueMap) {

        ArrayList<RuleEffect> effects = new ArrayList<>();
        for (ProgramRule rule : rules) {
            if (conditionIsTrue(rule.getCondition(), variableValueMap)) {
                for (ProgramRuleAction action : rule.getProgramRuleActions()) {
                    effects.add(createEffect(action));
                }
            }
        }

        return effects;
    }

    private static String replaceVariables(String expression,
                                           final RuleEngineVariableValueMap variableValueMap) {

        ArrayList<String> variablesFound = new ArrayList<>();

        Matcher m = VARIABLE_PATTERN.matcher(expression);
        while (m.find()) {
            String variable = expression.substring(m.start(), m.end());
            variablesFound.add(variable);
        }

        for (String variable : variablesFound) {
            String variableName = variable.replace("#{", "").replace("}", "");
            ProgramRuleVariableValue variableValue = variableValueMap.getProgramRuleVariableValue(
                    variableName);
            if(variableValue != null) {
                expression = expression.replace(variable, variableValue.toString());
            } else {
                //TODO Log the problem - the expression contains a variable that is not defined
                throw new IllegalArgumentException("Variable " + variableName + " found in expression "
                                + expression + ", but is not defined as a variable");
            }

        }

        return expression;
    }

    private static String runDhisFunctions(String expression,
                                    final RuleEngineVariableValueMap variableValueMap) {
        //Called from "runExpression". Only proceed with this logic in case there seems to be dhis function calls: "d2:" is present.
        if(expression != null && expression.indexOf("d2:") != -1){
            boolean continueLooping = true;
            //Safety harness on 10 loops, in case of unanticipated syntax causing unintencontinued looping
            for(int i = 0; i < 10 && continueLooping; i++ ) {
                boolean expressionUpdated = false;
                boolean brokenExecution = false;
                for (DhisFunction dhisFunction : dhisFunctions){
                    //Select the function call, with any number of parameters inside single quotations, or number parameters witout quotations
                    Pattern regularExFunctionCall = Pattern.compile(dhisFunction.getName() + "\\( *(([\\d/\\*\\+\\-%\\.]+)|( *'[^']*'))*( *, *(([\\d/\\*\\+\\-%\\.]+)|'[^']*'))* *\\)");
                    Matcher callsToThisFunction = regularExFunctionCall.matcher(expression);
                    while(callsToThisFunction.find()) {
                        String callToThisFunction = callsToThisFunction.group();

                        //Separate out parameters - Remove the function name and paranthesis:
                        List<String> parameters = new ArrayList<>(2);
                        Pattern justParametersPattern = Pattern.compile("(^[^\\(]+\\()|\\)");
                        Matcher justParametersMatcher = justParametersPattern.matcher(callToThisFunction);

                        if(justParametersMatcher.find()) {
                            String justParameters = justParametersMatcher.group();

                            //Then split into single parameters:
                            Pattern splitParametersPattern = Pattern.compile("(('[^']+')|([^,]+))");
                            Matcher splitParametersMatcher = splitParametersPattern.matcher(justParameters);

                            while(splitParametersMatcher.find()) {
                                parameters.add(splitParametersMatcher.group());
                            }
                        }

                        //Show error if no parameters is given and the function requires parameters,
                        //or if the number of parameters is wrong.
                        //But we are only checking parameters where the dhisFunction actually has a
                        //defined set of parameters(concatenate, for example, does not have a fixed number);
                        if(dhisFunction.getParameters() != null
                                && dhisFunction.getParameters() > 0){


                            if(parameters.size() != dhisFunction.getParameters()){
                                throw new IllegalArgumentException( "Wrong number of parameters for function "
                                        + dhisFunction.getName() + ". Expecting " + dhisFunction.getParameters()
                                        + ", found " + parameters.size());
                                //Mark this function call as broken:
                                //brokenExecution = true;
                            }
                        }

                        //In case the function call is nested, the parameter itself contains an expression, run the expression.
                        if(!brokenExecution && parameters.size() > 0) {
                            for (int j = 0; j < parameters.size(); j++) {
                                parameters.set(j, runExpression(parameters.get(j), variableValueMap));
                            }
                        }

                        if(brokenExecution) {
                            //Function call is not possible to evaluate, remove the call:
                            expression = expression.replace(callToThisFunction, "false");
                            expressionUpdated = true;
                        } else {
                            expression = dhisFunction.execute(expression);
                        }

                    }
                    //We only want to continue looping until we made a successful replacement,
                    //and there is still occurrences of "d2:" in the code. In cases where d2: occur outside
                    //the expected d2: function calls, one unneccesary iteration will be done and the
                    //successfulExecution will be false coming back here, ending the loop. The last iteration
                    //should be zero to marginal performancewise.
                    if(expressionUpdated && expression.indexOf("d2:") != -1) {
                        continueLooping = true;
                    } else {
                        continueLooping = false;
                    }

                }
            }
        }

        return expression;

    }

    private static String runExpression(String condition,
                                        final RuleEngineVariableValueMap variableValueMap) {
        condition = replaceVariables(condition, variableValueMap);
        condition = runDhisFunctions(condition, variableValueMap);
        return condition;
    }

    /**
     * Evaluates a passed expression from a {@link ProgramRule} to true or false.
     *
     * @param condition
     * @return
     */
    private static boolean conditionIsTrue(String condition,
                                           final RuleEngineVariableValueMap variableValueMap) {
        condition = runExpression(condition, variableValueMap);
        boolean isTrue = false;
        try {
            isTrue = ExpressionUtils.isTrue(condition, null);
        } catch (JexlException jxlException) {
            jxlException.printStackTrace();
        }
        return isTrue;
    }

    /**
     * Mapping method for creating a {@link RuleEffect} from a {@link ProgramRuleAction} object.
     *
     * @param action
     * @return
     */
    private static RuleEffect createEffect(ProgramRuleAction action) {
        RuleEffect effect = new RuleEffect();
        effect.setProgramRule(action.getProgramRule());
        effect.setProgramRuleActionType(action.getProgramRuleActionType());
        effect.setContent(action.getContent());
        effect.setData(action.getData());
        effect.setDataElement(action.getDataElement());
        effect.setProgramIndicator(action.getProgramIndicator());
        effect.setLocation(action.getLocation());
        effect.setProgramStage(action.getProgramStage());
        effect.setProgramStageSection(action.getProgramStageSection());
        effect.setTrackedEntityAttribute(action.getTrackedEntityAttribute());

        return effect;
    }
}
