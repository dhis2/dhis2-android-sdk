package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.Arrays;
import java.util.List;

@AutoValue
abstract class ProgramRuleVariableValue {

    // current value of the ProgramRuleVariable instance
    abstract String value();

    // all the value candidates for this variable
    abstract List<String> valueCandidates();

    // enum
    abstract String valueType();

    // true if the value has been set explicitly
    abstract Boolean hasValue();

    //
    ProgramRuleVariableValue assignValue(String dataValue) {
        return create(dataValue, valueType(), true);
    }

    static ProgramRuleVariableValue create(String dataValue, String valueType, Boolean hasValue) {
        return create(dataValue, Arrays.asList(dataValue), valueType, hasValue);
    }

    static ProgramRuleVariableValue create(String dataValue,
            List<String> candidates, String valueType, Boolean hasValue) {
        /*
                     value = StringUtils.strip(value,"'");
        if (type == ValueType.TEXT
        || type == ValueType.LONG_TEXT
        || type == ValueType.EMAIL
        || type == ValueType.PHONE_NUMBER
        || type == ValueType.DATE
        || type == ValueType.DATETIME) {
            return "'" + value + "'";
        } else if (type == ValueType.INTEGER
                || type == ValueType.INTEGER_POSITIVE
                || type == ValueType.INTEGER_NEGATIVE
                || type == ValueType.INTEGER_ZERO_OR_POSITIVE
                || type == ValueType.NUMBER
                || type == ValueType.PERCENTAGE
                || type == ValueType.BOOLEAN
                || type == ValueType.TRUE_ONLY) {
            return value;
        } else {
            //TODO: Log the problem.
            return value;
        }

        */
        return new AutoValue_ProgramRuleVariableValue(dataValue, candidates, valueType, hasValue);
    }
}
