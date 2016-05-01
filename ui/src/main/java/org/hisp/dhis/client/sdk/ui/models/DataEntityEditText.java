package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class DataEntityEditText extends DataEntity {
    private final String hint;
    private final InputType inputType;

    private OnValueChangeListener<String> onValueChangeListener;
    private String value;

    public DataEntityEditText(String id, String label, String hint, InputType inputType) {
        super(id, label);
        this.hint = hint;
        this.inputType = isNull(inputType, "inputType must not be null");
    }

    public DataEntityEditText(String id, String label, InputType inputType) {
        this(id, label, null, inputType);
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

    public OnValueChangeListener<String> getOnValueChangeListener() {
        return onValueChangeListener;
    }

    public void setOnValueChangeListener(OnValueChangeListener<String> onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
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
