package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Collections.unmodifiableList;

@AutoValue
public abstract class ProgramRuleVariable {

    @Nonnull
    public abstract String name();

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract String dataElement();

    @Nullable
    public abstract ValueType dataElementValueType();

    @Nullable
    public abstract String trackedEntityAttribute();

    @Nullable
    public abstract ValueType trackedEntityAttributeType();

    @Nonnull
    public abstract Boolean useCodeForOptionSet();

    @Nonnull
    public abstract ProgramRuleVariableSourceType sourceType();

    @Nonnull
    public abstract List<Option> options();

    public static ProgramRuleVariable forDataElement(
            @Nonnull String name,
            @Nullable String programStage,
            @Nullable String dataElement,
            @Nullable ValueType valueType,
            @Nonnull Boolean useCodeForOptionSet,
            @Nonnull ProgramRuleVariableSourceType sourceType,
            @Nonnull List<Option> options) {
        return new AutoValue_ProgramRuleVariable(name, programStage, dataElement, valueType, null, null,
                useCodeForOptionSet, sourceType, unmodifiableList(new ArrayList<>(options)));
    }

    public static ProgramRuleVariable forAttribute(
            @Nonnull String name,
            @Nullable String programStage,
            @Nullable String trackedEntityAttribute,
            @Nullable ValueType valueType,
            @Nonnull Boolean useCodeForOptionSet,
            @Nonnull ProgramRuleVariableSourceType sourceType,
            @Nonnull List<Option> options) {
        return new AutoValue_ProgramRuleVariable(name, programStage, null, null, trackedEntityAttribute,
                valueType, useCodeForOptionSet, sourceType, unmodifiableList(new ArrayList<>(options)));
    }
}
