package org.hisp.dhis.android.sdk.ui.views;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDatePickerDialog extends DatePickerDialog {

    private CharSequence title;
    private TextView customTextView;

    public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year,
            int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            addTitleIfIsNecessary();
        }
    }

    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    @Override
    public void setCustomTitle(View customTitleView) {
        super.setCustomTitle(customTitleView);
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle(title);
    }

    private void addTitleIfIsNecessary() {
        try {
            if (!((TextView) ((LinearLayout) ((LinearLayout) ((LinearLayout) getDatePicker()
                    .getParent().getParent().getParent()).getChildAt(
                    0)).getChildAt(0)).getChildAt(1)).getText().equals(title)) {
                customTextView = new TextView(getContext());
                customTextView.setText(title);
                ((LinearLayout) getDatePicker().getParent().getParent().getParent()).addView(
                        customTextView, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}