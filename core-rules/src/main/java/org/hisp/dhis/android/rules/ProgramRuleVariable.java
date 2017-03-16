package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class ProgramRuleVariable {

    // @NonNull
    // enum ProgramRuleVariableSourceType

    // @NonNull
    // own property of the ProgramRuleVariable
    public abstract String name();

    // @Nullable
    public abstract String programStage();

    // @Nullable
    public abstract String dataElement();

    // @Nullable
    public abstract String trackedEntityAttribute();

    // @Nullable
    public abstract String dataElementValueType();

    // @Nullable
    public abstract String trackedEntityAttributeType();

    // @NonNull
    public abstract Boolean useCodeForOptionSet();

    // @NonNull
    public abstract List<Option> options();

    // possibility to link Constants to ProgramRuleVariables
}
