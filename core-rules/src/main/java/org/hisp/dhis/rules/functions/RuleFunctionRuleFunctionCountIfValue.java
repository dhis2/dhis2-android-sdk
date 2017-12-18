package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionRuleFunctionCountIfValue extends RuleFunction {
    static final String D2_COUNT_IF_VALUE = "d2:countIfValue";

    @Nonnull
    static RuleFunctionRuleFunctionCountIfValue create() {
        return new RuleFunctionRuleFunctionCountIfValue();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(countIfValue(arguments.get(0), valueMap));
    }

    /**
     * Function which will return count value for given variableName
     *
     * @param variableName variable name.
     * @param valueMap list of variables.
     * @return return the count of values
     */
    private static Integer countIfValue(String variableName, Map<String, RuleVariableValue> valueMap) {
        RuleVariableValue ruleVariableValue =valueMap.get(variableName);
        Integer count = 0;
        if(ruleVariableValue != null) {
            if(ruleVariableValue.value()!=null) {
                String valueToCompare = ruleVariableValue.value().replaceFirst("'","");
                if(valueToCompare.lastIndexOf("'")==valueToCompare.length()-1){
                    valueToCompare=valueToCompare.substring(0,valueToCompare.length()-1);
                }
                if(ruleVariableValue.candidates() != null) {
                    for(String candidateValue : ruleVariableValue.candidates()){
                        if (candidateValue != null && candidateValue.equals(valueToCompare)) {
                            count++;
                        }
                    }
                } else {
                    if(valueToCompare.equals(ruleVariableValue.value())) {
                        return 1;
                    }
                }
            }
        }
        return count;
    }

}
