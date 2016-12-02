package org.hisp.dhis.client.sdk.ui.rows.datepickerrowview;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.hisp.dhis.client.sdk.ui.rows.datepickerrowview.DatePickerRowView.DATE_FORMAT;

final class DatePickerRowOnDateSetListener implements DatePickerDialog.OnDateSetListener {
    private final EditText editText;

    DatePickerRowOnDateSetListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String newValue = simpleDateFormat.format(calendar.getTime());
        editText.setText(newValue);
    }
}
