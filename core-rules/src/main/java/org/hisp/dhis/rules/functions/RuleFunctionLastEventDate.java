package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class RuleFunctionLastEventDate extends RuleFunction {
    static final String D2_LAST_EVENT_DATE = "d2:lasteventdate";
    private static final String ENV_VAR_EVENT_DATE = "event_date";

    @Nonnull
    public static RuleFunctionLastEventDate create() {
        return new RuleFunctionLastEventDate();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        return lastEventDate(arguments.get(0), valueMap);
    }

    /**
     * Get last event date for a given variable name
     *
     * @param variableName name of the variable
     * @param variableName list of values
     * @return the date as string.
     */

    public static String lastEventDate(String variableName, Map<String, RuleVariableValue>  valueMap) {
        if(valueMap.containsKey(variableName)){
            if(valueMap.containsKey(ENV_VAR_EVENT_DATE)){
                return valueMap.get(ENV_VAR_EVENT_DATE).value().replace("'","");
            }
        }
        return "";
    }
}
