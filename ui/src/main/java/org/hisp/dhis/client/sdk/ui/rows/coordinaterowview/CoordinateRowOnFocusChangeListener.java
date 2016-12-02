package org.hisp.dhis.client.sdk.ui.rows.coordinaterowview;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import static android.text.TextUtils.isEmpty;

final class CoordinateRowOnFocusChangeListener implements View.OnFocusChangeListener {
    private final TextInputLayout textInputLayout;
    private final EditText editText;
    private final CharSequence hint;

    CoordinateRowOnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
        this.textInputLayout = inputLayout;
        this.editText = editText;
        this.hint = textInputLayout.getHint();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            textInputLayout.setHint(hint);
        } else {
            if (!isEmpty(editText.getText().toString())) {
                textInputLayout.setHint(null);
            }
        }
    }

    public CharSequence getHint() {
        return hint;
    }
}
