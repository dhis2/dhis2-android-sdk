package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

public class KeyValueRow extends NonEditableTextViewRow {

    private String mLabel;

    public KeyValueRow(String label, String value) {
        super(value);
        mLabel = label;
    }

    @Override
    public String getName() {
        return mLabel;
    }

    @Override
    public int getViewType() {
        return 0;
    }
}
