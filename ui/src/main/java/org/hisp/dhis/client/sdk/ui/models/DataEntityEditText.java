package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class DataEntityEditText extends DataEntity {
    private final String hint;
    private final InputType inputType;
    private String value;

    public DataEntityEditText(String id, String label, String hint, InputType inputType) {
        super(id, label);
        this.hint = isNull(hint, "hint must not be null");
        this.inputType = isNull(inputType, "inputType must not be null");
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.EDITTEXT;
    }

    public String getHint() {
        return hint;
    }

    public String getValue() {
        return value;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum InputType {
        TEXT,
        LONG_TEXT,
        NUMBER,
        INTEGER,
        INTEGER_NEGATIVE,
        INTEGER_ZERO_OR_POSITIVE,
        INTEGER_POSITIVE,
    }
}
