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
 * Produces the number of full months between the first and second argument.
 * If the second argument date is before the first argument the return value
 * will be the negative number of months between the two dates.
 * The static date format is 'yyyy-MM-dd'.
 */
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
        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 2) {
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
    static Integer monthsBetween(String start, String end) {
        if (isEmpty(start) || isEmpty(end)) {
            return 0;
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        try {
            Calendar startDate = Calendar.getInstance();
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            startDate.setTime(format.parse(start));
            Calendar endDate = Calendar.getInstance();
            endDate.set(Calendar.DAY_OF_MONTH, 1);
            endDate.setTime(format.parse(end));

            int diffYear = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
            return Long.valueOf((diffYear * 12 + endDate.get(Calendar.MONTH) - startDate.get(
                    Calendar.MONTH))).intValue();
        } catch (ParseException parseException) {
            throw new IllegalArgumentException(parseException.getMessage(), parseException);
        }
    }

    private static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }
}
