package org.hisp.dhis.android.rules;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import static org.hisp.dhis.android.rules.ProgramRuleVariableValue.create;

final class ValueMapFactory {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    // environment variables
    private static final String VAR_CURRENT_DATE = "current_date";
    private static final String VAR_EVENT_DATE = "event_date";

    // format of the date
    private final SimpleDateFormat dateFormat;

    // context for execution
    private final List<ProgramRuleVariable> programRuleVariables;
    private final List<Event> events;

    ValueMapFactory(List<ProgramRuleVariable> variables, List<Event> events) {
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        this.programRuleVariables = variables;
        this.events = events;
    }

    @Nonnull
    Map<String, ProgramRuleVariableValue> build(@Nonnull Event currentEvent) {
        Map<String, ProgramRuleVariableValue> valueMap = new HashMap<>();

        // add environment variables
        valueMap.put(VAR_CURRENT_DATE, create(dateFormat.format(new Date()), "date_type", true));
        valueMap.put(VAR_EVENT_DATE, create(dateFormat.format(currentEvent.eventDate()), "date_type", true));

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }
}
