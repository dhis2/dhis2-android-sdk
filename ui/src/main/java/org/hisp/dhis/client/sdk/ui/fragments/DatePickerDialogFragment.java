package org.hisp.dhis.client.sdk.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {
    private static final String TAG = DatePickerDialogFragment.class.getSimpleName();
    private static final String ARG_ALLOW_DATES_IN_FUTURE = "arg:allowDatesInFuture";

    @Nullable
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public static DatePickerDialogFragment newInstance(boolean allowDatesInFuture) {
        Bundle arguments = new Bundle();
        arguments.putBoolean(ARG_ALLOW_DATES_IN_FUTURE, allowDatesInFuture);

        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        if (!isAllowDatesInFuture()) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        return datePickerDialog;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    public void setOnDateSetListener(@Nullable DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

    private boolean isAllowDatesInFuture() {
        return getArguments().getBoolean(ARG_ALLOW_DATES_IN_FUTURE, false);
    }
}