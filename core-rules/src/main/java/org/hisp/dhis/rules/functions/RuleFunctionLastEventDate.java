package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;
import org.hisp.dhis.rules.models.RuleEvent;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionLastEventDate extends RuleFunction {
    static final String D2_LAST_EVENT_DATE = "d2:lastEventDate";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Nonnull
    static RuleFunctionLastEventDate create() {
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

    private static String lastEventDate(String variableName, Map<String, RuleVariableValue>  valueMap) {
        for(RuleVariableValue ruleVariableValue : valueMap.values()){
            if(ruleVariableValue.getTarget() instanceof RuleEvent){
                RuleEvent ruleEvent = (RuleEvent)ruleVariableValue.getTarget();
                if(ruleEvent.event().equals(variableName)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
                    return simpleDateFormat.format(ruleEvent.eventDate());
                }
            }
        }
        return "";
    }
}
