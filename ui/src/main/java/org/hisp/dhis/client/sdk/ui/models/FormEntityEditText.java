package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FormEntityEditText extends FormEntityString {
    private final String hint;
    private final InputType inputType;

    public FormEntityEditText(String id, String label, String hint, InputType inputType) {
        super(id, label);
        this.hint = hint;
        this.inputType = isNull(inputType, "inputType must not be null");
    }

    public FormEntityEditText(String id, String label, InputType inputType) {
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

    @NonNull
    public InputType getInputType() {
        return inputType;
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
