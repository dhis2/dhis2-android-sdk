package org.hisp.dhis.client.sdk.ui.rows.datepickerrowview;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.DatePickerDialogFragment;

import java.util.Calendar;

final class DatePickerRowOnButtonClickListener implements View.OnClickListener {
    private static final String EMPTY_STRING = "";

    private final EditText editText;
    private final Calendar calendar;
    private final FragmentManager fragmentManager;
    private final DatePickerRowOnDateSetListener onDateSetListener;

    DatePickerRowOnButtonClickListener(EditText editText, FragmentManager fragmentManager,
                                       DatePickerRowOnDateSetListener onDateSetListener) {
        this.editText = editText;
        this.calendar = Calendar.getInstance();
        this.fragmentManager = fragmentManager;
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.row_date_picker_edit_text ||
                view.getId() == R.id.row_date_picker_button_pick) {
            showDatePicker();
        } else if (view.getId() == R.id.button_clear) {
            editText.setText(EMPTY_STRING);
        } else if (view.getId() == R.id.row_date_picker_button_today) {
            onDateSetListener.onDateSet(null,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            showDatePicker();
        }
    }

    private void showDatePicker() {
        DatePickerDialogFragment datePicker = DatePickerDialogFragment.newInstance(false);
        datePicker.setOnDateSetListener(onDateSetListener);
        datePicker.show(fragmentManager);
    }
}
