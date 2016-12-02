package org.hisp.dhis.client.sdk.ui.rows.edittextrowview;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

final class EditTextRowOnRowClickListener implements View.OnClickListener {
    final EditText editText;
    final InputMethodManager inputMethodManager;

    EditTextRowOnRowClickListener(EditText editText, Context context) {
        this.editText = editText;
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        if(!editText.hasFocus()) {
            editText.requestFocus();
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            editText.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
