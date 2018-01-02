package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Produces a date based on the first argument date, adding the second argument number of days.
 */
final class RuleFunctionAddDays extends RuleFunction {
    static final String D2_ADD_DAYS = "d2:addDays";

    @Nonnull
    static RuleFunctionAddDays create() {
        return new RuleFunctionAddDays();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(addDays(arguments.get(0), arguments.get(1)));
    }

    @SuppressWarnings("PMD.UnnecessaryWrapperObjectCreation")
    private String addDays(String date, String days) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        try {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(format.parse(date));
            dateCalendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
            return format.format(dateCalendar.getTime());
        } catch (ParseException parseException) {
            throw new IllegalArgumentException(parseException.getMessage(), parseException);
        }
    }
}
