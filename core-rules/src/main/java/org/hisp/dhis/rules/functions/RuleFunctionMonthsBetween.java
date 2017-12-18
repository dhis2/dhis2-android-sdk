package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionMonthsBetween extends RuleFunction {
    static final String D2_MONTHS_BETWEEN = "d2:monthsBetween";

    @Nonnull
    static RuleFunctionMonthsBetween create() {
        return new RuleFunctionMonthsBetween();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(monthsBetween(arguments.get(0), arguments.get(1)));
    }
    /**
     * Function which will return the number of months between the two given dates.
     *
     * @param start the start date.
     * @param end   the end date.
     * @return number of days between dates.
     */
    @SuppressWarnings("PMD.UnnecessaryWrapperObjectCreation")
    private static Integer monthsBetween(String start, String end) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(DATE_PATTERN);

        try {
            Calendar startDate = Calendar.getInstance();
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            startDate.setTime(format.parse(start));
            Calendar endDate = Calendar.getInstance();
            endDate.set(Calendar.DAY_OF_MONTH, 1);
            endDate.setTime(format.parse(end));

            int diffYear = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
            return Long.valueOf((diffYear * 12 + endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH))).intValue();
        } catch (ParseException parseException) {
            throw new RuntimeException(parseException);
        }
    }
}
