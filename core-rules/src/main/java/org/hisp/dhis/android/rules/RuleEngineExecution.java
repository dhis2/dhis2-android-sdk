package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Rule;
import org.hisp.dhis.android.rules.models.RuleEffect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

class RuleEngineExecution implements Callable<List<RuleEffect>> {

    @Nonnull
    private final List<Rule> rules;

    @Nonnull
    private final Map<String, RuleVariableValue> valueMap;

    RuleEngineExecution(@Nonnull List<Rule> rules,
            @Nonnull Map<String, RuleVariableValue> valueMap) {
        this.rules = rules;
        this.valueMap = valueMap;
    }

    @Override
    public List<RuleEffect> call() throws Exception {
        return null;
    }
}
