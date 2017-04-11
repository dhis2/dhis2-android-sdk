package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleDataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

final class Utils {

    @Nonnull
    static List<String> values(@Nonnull List<RuleDataValue> ruleDataValues) {
        List<String> values = new ArrayList<>(ruleDataValues.size());
        for (RuleDataValue ruleDataValue : ruleDataValues) {
            values.add(ruleDataValue.value());
        }
        return Collections.unmodifiableList(values);
    }
}
