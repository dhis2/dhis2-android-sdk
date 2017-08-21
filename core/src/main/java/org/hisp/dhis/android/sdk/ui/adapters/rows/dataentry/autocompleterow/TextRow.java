package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import android.widget.TextView;

import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;

public abstract class TextRow extends Row {
    protected TextView.OnEditorActionListener mOnEditorActionListener;

    public void setOnEditorActionListener(
            TextView.OnEditorActionListener onEditorActionListener) {
        mOnEditorActionListener = onEditorActionListener;
    }
}
