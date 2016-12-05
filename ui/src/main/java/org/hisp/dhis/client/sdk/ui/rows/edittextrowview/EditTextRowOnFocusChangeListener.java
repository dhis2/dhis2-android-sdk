package org.hisp.dhis.client.sdk.ui.rows.edittextrowview;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import static android.text.TextUtils.isEmpty;

final class EditTextRowOnFocusChangeListener implements View.OnFocusChangeListener {
    private final TextInputLayout textInputLayout;
    private final EditText editText;
    private CharSequence hint;

    EditTextRowOnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
        this.textInputLayout = inputLayout;
        this.editText = editText;
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            textInputLayout.setHint(hint);
        } else {
            if (!isEmpty(editText.getText().toString())) {
                textInputLayout.setHint(null);
            }
        }
    }
}
