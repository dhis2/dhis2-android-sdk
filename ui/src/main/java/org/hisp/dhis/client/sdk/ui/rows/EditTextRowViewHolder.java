package org.hisp.dhis.client.sdk.ui.rows;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import org.hisp.dhis.client.sdk.ui.R;

public class EditTextRowViewHolder extends RecyclerView.ViewHolder {
    TextInputLayout textInputLayout;
    EditText editText;

    public EditTextRowViewHolder(View itemView) {
        super(itemView);
        textInputLayout = (TextInputLayout) itemView.findViewById(R.id.edit_text_row_text_input_layout);
        editText = (EditText) itemView.findViewById(R.id.edit_text_row_edit_text);
    }
}
