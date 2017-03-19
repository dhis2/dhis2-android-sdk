package org.hisp.dhis.android.rules;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

final class RuleExecution implements Callable<List<RuleEffect>> {
    private final Map<String, ProgramRuleVariableValue> variableValueMap;

    RuleExecution(@Nonnull Map<String, ProgramRuleVariableValue> variableValueMap) {
        this.variableValueMap = variableValueMap;
    }

    @Override
    public List<RuleEffect> call() throws Exception {
        return null;
    }
}
