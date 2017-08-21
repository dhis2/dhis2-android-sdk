package org.hisp.dhis.rules.functions;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.rules.RuleVariableValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

@AutoValue
abstract class RuleFunctionDaysBetween extends RuleFunction {
    static final String D2_DAYS_BETWEEN = "d2:daysBetween";

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            @Nonnull Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(daysBetween(arguments.get(0), arguments.get(1)));
    }

    @Nonnull
    public static RuleFunctionDaysBetween create() {
        return new AutoValue_RuleFunctionDaysBetween();
    }

    /**
     * Function which will return the number of days between the two given dates.
     *
     * @param start the start date.
     * @param end   the end date.
     * @return number of days between dates.
     */
    @SuppressWarnings("PMD.UnnecessaryWrapperObjectCreation")
    static Integer daysBetween(String start, String end) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(DATE_PATTERN);

        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);

            return Long.valueOf((endDate.getTime() - startDate.getTime()) / 86400000).intValue();
        } catch (ParseException parseException) {
            throw new RuntimeException(parseException);
        }
    }
}
