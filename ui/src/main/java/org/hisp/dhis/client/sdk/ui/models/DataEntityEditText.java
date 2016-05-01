package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    public String getHint() {
        return hint;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    @NonNull
    public InputType getInputType() {
        return inputType;
    }

    public void setValue(@Nullable String value) {
        if (onValueChangeListener != null && !isValueSame(value)) {
            onValueChangeListener.onValueChanged(getId(), value);
        }

        this.value = value;
    }

    @Nullable
    public OnValueChangeListener<String> getOnValueChangeListener() {
        return onValueChangeListener;
    }

    public void setOnValueChangeListener(@Nullable OnValueChangeListener<String> changeListener) {
        this.onValueChangeListener = changeListener;
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

    private boolean isValueSame(@Nullable String value) {
        if (this.value != null) {
            return this.value.equals(value);
        } else {
            return value == null;
        }
    }
}
