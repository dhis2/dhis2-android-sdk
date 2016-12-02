package org.hisp.dhis.client.sdk.ui.rows.datepickerrowview;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityDate;
import org.hisp.dhis.client.sdk.ui.utils.FormUtils;
import org.hisp.dhis.client.sdk.ui.views.RaisedButton;

final class DatePickerRowViewHolder extends RecyclerView.ViewHolder {
    final TextView textViewLabel;
    final EditText editText;
    final LinearLayout rootLinearLayout;

    final RaisedButton datePickerButtonNow;
    final RaisedButton datePickerButton;
    final ImageButton clearButton;

    final DatePickerRowOnValueChangedListener onValueChangedListener;
    final DatePickerRowOnDateSetListener onDateSetListener;
    final DatePickerRowOnButtonClickListener onButtonClickListener;

    DatePickerRowViewHolder(View itemView, FragmentManager fragmentManager) {
        super(itemView);

        // TextViews
        textViewLabel = (TextView) itemView
                .findViewById(R.id.textview_row_label);
        editText = (EditText) itemView
                .findViewById(R.id.row_date_picker_edit_text);

        // Buttons
        datePickerButtonNow = (RaisedButton) itemView
                .findViewById(R.id.row_date_picker_button_today);
        datePickerButton = (RaisedButton) itemView
                .findViewById(R.id.row_date_picker_button_pick);
        clearButton = (ImageButton) itemView
                .findViewById(R.id.button_clear);

        onValueChangedListener = new DatePickerRowOnValueChangedListener();
        onDateSetListener = new DatePickerRowOnDateSetListener(editText);
        onButtonClickListener = new DatePickerRowOnButtonClickListener(
                editText, fragmentManager, onDateSetListener);

        editText.addTextChangedListener(onValueChangedListener);
        editText.setOnClickListener(onButtonClickListener);

        rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.root_linear_layout);
        rootLinearLayout.setOnClickListener(onButtonClickListener);

        clearButton.setOnClickListener(onButtonClickListener);
        datePickerButton.setOnClickListener(onButtonClickListener);
        datePickerButtonNow.setOnClickListener(onButtonClickListener);
    }

    public void update(FormEntityDate formEntity) {
        // update callbacks with current entities
        onValueChangedListener.setDataEntity(formEntity);
        textViewLabel.setText(FormUtils.getFormEntityLabel(formEntity));
        editText.setText(formEntity.getValue());

        if (formEntity.isLocked()) {
            editText.setEnabled(false);
            editText.setClickable(false);
            editText.setLongClickable(false);

            datePickerButton.setClickable(false);
            datePickerButton.setEnabled(false);

            datePickerButtonNow.setClickable(false);
            datePickerButtonNow.setEnabled(false);

            clearButton.setClickable(false);
            clearButton.setEnabled(false);

            rootLinearLayout.setEnabled(false);
        } else {
            editText.setEnabled(true);
            editText.setClickable(true);
            editText.setLongClickable(true);

            datePickerButton.setClickable(true);
            datePickerButton.setEnabled(true);

            datePickerButtonNow.setClickable(true);
            datePickerButtonNow.setEnabled(true);

            clearButton.setEnabled(true);
            clearButton.setClickable(true);

            rootLinearLayout.setEnabled(true);
        }
    }
}
