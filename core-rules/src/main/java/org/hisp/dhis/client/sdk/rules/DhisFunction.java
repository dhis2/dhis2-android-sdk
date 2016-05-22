package org.hisp.dhis.client.sdk.rules;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by markusbekken on 20.05.2016.
 */
abstract class DhisFunction {
    private static List<DhisFunction> dhisFunctions = Arrays.asList(
        new DhisFunction("d2:daysBetween", 2) {
            @Override
            public String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression) {
                return "-1";
            }
        },
        new DhisFunction("d2:weeksBetween", 2) {
            @Override
            public String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression) {
                return null;
            }
        },
        new DhisFunction("d2:monthsBetween", 2) {
            @Override
            public String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression) {
                return null;
            }
        },
        new DhisFunction("d2:yearsBetween", 2) {
            @Override
            public String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression) {
                return null;
            }
        },
        new DhisFunction("d2:floor", 1) {
            @Override
            public String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression) {
                Integer floored = (int) NumberUtils.toDouble(parameters.get(0));
                return floored.toString();
            }
        }/*,
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
                public String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression) {
                    String variableName = parameters.get(0).replace("'","");
                    ProgramRuleVariableValue variable = valueMap.getProgramRuleVariableValue(variableName);
                    if(variable != null) {
                        return variable.hasValue() ? "true" : "false";
                    }
                    else {
                        return "false";
                    }
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
    //TODO: Implement the rest of the functions

    public static List<DhisFunction> getDhisFunctions() {
        return dhisFunctions;
    }

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


    private String name;
    private Integer parameters;

    public DhisFunction(String name, Integer parameters){
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public Integer getParameters() {
        return parameters;
    }

    public abstract String execute(List<String> parameters, RuleEngineVariableValueMap valueMap, String expression);
}