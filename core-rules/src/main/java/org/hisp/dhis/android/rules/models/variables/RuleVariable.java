package org.hisp.dhis.android.rules.models.variables;

import org.hisp.dhis.android.rules.models.Option;

import java.util.List;

import javax.annotation.Nonnull;

public abstract class RuleVariable {

    @Nonnull
    public abstract String name();

    @Nonnull
    public abstract String field();

    @Nonnull
    public abstract ValueType valueType();

    @Nonnull
    public abstract Boolean useCode();

    @Nonnull
    public abstract List<Option> options();
//
//    @Nonnull
//    public abstract RuleVariableValue createValue(@Nullable List<DataValue> dataValues);

//    ToDo: reconsider this property in subclasses
//    @Nullable
//    public abstract String programStage();

//
//    @Nullable
//    public abstract String dataElement();
//
//    @Nullable
//    public abstract ValueType dataElementValueType();
//
//    @Nullable
//    public abstract String trackedEntityAttribute();
//
//    @Nullable
//    public abstract ValueType trackedEntityAttributeType();
//
//    @Nonnull
//    public abstract ProgramRuleVariableSourceType sourceType();
//
//    public static RuleVariable forDataElement(
//            @Nonnull String name,
//            @Nullable String programStage,
//            @Nullable String dataElement,
//            @Nullable ValueType valueType,
//            @Nonnull Boolean useCodeForOptionSet,
//            @Nonnull ProgramRuleVariableSourceType sourceType,
//            @Nonnull List<Option> options) {
//        return new AutoValue_Rule_Variable(name, programStage, dataElement, valueType, null, null,
//                useCodeForOptionSet, sourceType, unmodifiableList(new ArrayList<>(options)));
//    }
//
//    public static RuleVariable forAttribute(
//            @Nonnull String name,
//            @Nullable String programStage,
//            @Nullable String trackedEntityAttribute,
//            @Nullable ValueType valueType,
//            @Nonnull Boolean useCodeForOptionSet,
//            @Nonnull ProgramRuleVariableSourceType sourceType,
//            @Nonnull List<Option> options) {
//        return new AutoValue_Rule_Variable(name, programStage, null, null, trackedEntityAttribute,
//                valueType, useCodeForOptionSet, sourceType, unmodifiableList(new ArrayList<>(options)));
//    }
}
