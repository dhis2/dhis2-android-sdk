package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleEffect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

final class RuleExecution implements Callable<List<RuleEffect>> {
    private final Map<String, RuleVariableValue> valueMap;

    RuleExecution(@Nonnull Map<String, RuleVariableValue> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public List<RuleEffect> call() throws Exception {
        return null;
    }
}
