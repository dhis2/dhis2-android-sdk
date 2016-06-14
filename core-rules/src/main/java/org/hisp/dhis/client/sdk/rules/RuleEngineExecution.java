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
import org.hisp.dhis.client.sdk.models.program.ProgramRuleActionType;
import org.hisp.dhis.commons.util.ExpressionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleEngineExecution {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("[A#CV]\\{(\\w+.?\\w*)\\}");

    public static List<RuleEffect> execute(
            List<ProgramRule> rules, RuleEngineVariableValueMap variableValueMap) {

        Collections.sort(rules, ProgramRule.PRIORITY_COMPARATOR);

        ArrayList<RuleEffect> effects = new ArrayList<>();

        // trying to read same rules list causes
        // java.util.ConcurrentModificationException exception?
        for (ProgramRule rule : rules) {
            if (conditionIsTrue(rule.getCondition(), variableValueMap)) {
                for (ProgramRuleAction action : rule.getProgramRuleActions()) {
                    effects.add(createEffect(action, variableValueMap));
                }
            }
        }

        return effects;
    }

    private static String replaceVariables(String expression,
                                           final RuleEngineVariableValueMap variableValueMap) {

        if(expression != null && expression.length() > 0) {
            ArrayList<String> variablesFound = new ArrayList<>();

            Matcher m = VARIABLE_PATTERN.matcher(expression);
            while (m.find()) {
                String variable = expression.substring(m.start(), m.end());
                variablesFound.add(variable);
            }

            for (String variable : variablesFound) {
                String variableName = variable.replace("#{", "").replace("V{", "").replace("A{", "").replace("}", "");
                ProgramRuleVariableValue variableValue = variableValueMap.getProgramRuleVariableValue(
                        variableName);
                if (variableValue != null) {
                    expression = expression.replace(variable, variableValue.toString());
                } else {
                    //TODO Log the problem - the expression contains a variable that is not defined
                    throw new IllegalArgumentException("Variable " + variableName + " found in expression "
                            + expression + ", but is not defined as a variable");
                }
            }
        }

        return expression;
    }

    private static String runDhisFunctions(String expression,
                                    final RuleEngineVariableValueMap variableValueMap) {
        //Called from "runExpression". Only proceed with this logic in case there seems to be dhis function calls: "d2:" is present.
        if(expression != null && expression.contains("d2:")){
            boolean continueLooping = true;
            //Safety harness on 10 loops, in case of unanticipated syntax causing unintencontinued looping
            for(int i = 0; i < 10 && continueLooping; i++ ) {
                boolean expressionUpdated = false;
                boolean brokenExecution = false;
                for (DhisFunction dhisFunction : DhisFunction.getDhisFunctions()){
                    //Select the function call, with any number of parameters inside single quotations, or number parameters witout quotations
                    Pattern regularExFunctionCall = Pattern.compile(dhisFunction.getName() + "\\( *(([\\d/\\*\\+\\-%\\. ]+)|( *'[^']*'))*( *, *(([\\d/\\*\\+\\-%\\. ]+)|'[^']*'))* *\\)");
                    Matcher callsToThisFunction = regularExFunctionCall.matcher(expression);
                    while(callsToThisFunction.find()) {
                        String callToThisFunction = callsToThisFunction.group();

                        //Separate out parameters - Remove the function name and paranthesis:
                        List<String> parameters = new ArrayList<>(2);
                        Pattern justParametersPattern = Pattern.compile("(^[^\\(]+\\()|\\)");
                        Matcher justParametersMatcher = justParametersPattern.matcher(callToThisFunction);
                        String justParameters = justParametersMatcher.replaceAll("");

                        //Then split into single parameters:
                        Pattern splitParametersPattern = Pattern.compile("(('[^']+')|([^,]+))");
                        Matcher splitParametersMatcher = splitParametersPattern.matcher(justParameters);

                        while(splitParametersMatcher.find()) {
                            parameters.add(splitParametersMatcher.group());
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
                            String executionResult = dhisFunction.execute(parameters, variableValueMap, expression);
                            expression = expression.replace(callToThisFunction, executionResult);
                        }

                    }
                    //We only want to continue looping until we made a successful replacement,
                    //and there is still occurrences of "d2:" in the code. In cases where d2: occur outside
                    //the expected d2: function calls, one unneccesary iteration will be done and the
                    //successfulExecution will be false coming back here, ending the loop. The last iteration
                    //should be zero to marginal performancewise.
                    if(expressionUpdated && expression.contains("d2:")) {
                        continueLooping = true;
                    } else {
                        continueLooping = false;
                    }

                }
            }
        }

        return expression;

    }

    private static String evaluateExpression(String expression) {
        if(expression != null && expression.length() > 0) {
            try {
                Object response = ExpressionUtils.evaluate(expression, null);
                expression = response.toString();
            } catch (JexlException jxlException) {
                jxlException.printStackTrace();
            }
        }
        return expression;
    }

    private static String runExpression(String expression,
                                        RuleEngineVariableValueMap variableValueMap) {
        expression = replaceVariables(expression, variableValueMap);
        expression = runDhisFunctions(expression, variableValueMap);
        expression = evaluateExpression(expression);
        return expression;
    }

    /**
     * Evaluates a passed expression from a {@link ProgramRule} to true or false.
     *
     * @param condition
     * @return
     */
    private static boolean conditionIsTrue(String condition,
                                            RuleEngineVariableValueMap variableValueMap) {
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
    private static RuleEffect createEffect(ProgramRuleAction action,
                                           RuleEngineVariableValueMap variableValueMap) {
        RuleEffect effect = new RuleEffect();
        effect.setProgramRule(action.getProgramRule());
        effect.setProgramRuleActionType(action.getProgramRuleActionType());
        effect.setContent(action.getContent());
        //run expressions to evaluate content of data column:
        effect.setData(runExpression(action.getData(), variableValueMap));
        effect.setDataElement(action.getDataElement());
        effect.setProgramIndicator(action.getProgramIndicator());
        effect.setLocation(action.getLocation());
        effect.setProgramStage(action.getProgramStage());
        effect.setProgramStageSection(action.getProgramStageSection());
        effect.setTrackedEntityAttribute(action.getTrackedEntityAttribute());

        if(effect.getProgramRuleActionType() == ProgramRuleActionType.ASSIGN) {
            //in case the action type is assign, it might be needed to update the variable value map:
            if(effect.getContent() != null && effect.getContent().contains("#{")) {
                String variableName = effect.getContent().replace("#{","").replace("}","");
                ProgramRuleVariableValue valueObject = variableValueMap.getProgramRuleVariableValue(variableName);
                if(valueObject != null) {
                    valueObject.setValueString(effect.getData());
                }
            }
        }

        return effect;
    }
}
