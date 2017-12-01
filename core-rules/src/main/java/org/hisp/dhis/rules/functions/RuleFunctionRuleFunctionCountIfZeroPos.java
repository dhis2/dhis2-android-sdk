package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class RuleFunctionRuleFunctionCountIfZeroPos extends RuleFunction {
    static final String D2_COUNTIFZEROPOS = "d2:countifzeropos";

    @Nonnull
    public static RuleFunctionRuleFunctionCountIfZeroPos create() {
        return new RuleFunctionRuleFunctionCountIfZeroPos();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(countIfZeroPos(arguments.get(0), valueMap));
    }

    /**
     * Function which will return count if zero pos values for given variableName
     *
     * @param variableName variable name.
     * @param valueMap list of variables.
     * @return return the count of zero pos
     */
    public static Integer countIfZeroPos(String variableName, Map<String, RuleVariableValue> valueMap) {
        RuleVariableValue ruleVariableValue =valueMap.get(variableName);
        Integer count = 0;
        if(ruleVariableValue != null) {
            if(ruleVariableValue.value()!=null) {
                if(ruleVariableValue.candidates() != null) {
                    for(String candidateValue : ruleVariableValue.candidates()){
                        try {
                            if (candidateValue != null && Integer.parseInt(candidateValue) >= 0.0) {
                                count++;
                            }
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    if(ruleVariableValue.value() != null && Integer.parseInt(ruleVariableValue.value()) >= 0.0) {
                        return 1;
                    }
                }
            }
        }
        return count;
    }

}
