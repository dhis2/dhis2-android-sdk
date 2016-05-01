/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.rows;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.models.DataEntityDate;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.RaisedButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class DatePickerRowView implements RowView {
    private static final String TAG = DatePickerRowView.class.getSimpleName();
    private final FragmentManager fragmentManager;

    public DatePickerRowView(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new DatePickerRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_datepicker, parent, false), fragmentManager);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, DataEntity dataEntity) {
        DataEntityDate entity = (DataEntityDate) dataEntity;
        ((DatePickerRowViewHolder) viewHolder).update(entity);
    }

    private static class DatePickerRowViewHolder extends RecyclerView.ViewHolder {
        public final TextInputLayout textInputLayout;
        public final EditText displayValueEditText;
        public final TextView textViewLabel;

        public final RaisedButton datePickerButtonNow;
        public final RaisedButton datePickerButton;
        public final ImageButton clearButton;

        public final OnValueChangedListener onValueChangedListener;
        public final OnFocusChangeListener onFocusChangeListener;
        public final OnDateSetListener onDateSetListener;

        public final OnButtonClickListener onButtonClickListener;

        public DatePickerRowViewHolder(View itemView, FragmentManager fragmentManager) {
            super(itemView);

            // Buttons
            datePickerButtonNow = (RaisedButton) itemView
                    .findViewById(R.id.row_date_picker_button_today);
            datePickerButton = (RaisedButton) itemView
                    .findViewById(R.id.row_date_picker_button_pick);
            clearButton = (ImageButton) itemView.findViewById(R.id.button_clear);


            // TextViews
            textInputLayout = (TextInputLayout) itemView
                    .findViewById(R.id.row_date_picker_text_input_layout);
            textViewLabel = (TextView) itemView.findViewById(R.id.textview_row_label);
            displayValueEditText = (EditText) itemView
                    .findViewById(R.id.row_date_picker_edit_text);

            textInputLayout.setHint(displayValueEditText.getContext()
                    .getString(R.string.enter_date));
            displayValueEditText.setInputType(InputType.TYPE_CLASS_DATETIME);

            onValueChangedListener = new OnValueChangedListener();
            onDateSetListener = new OnDateSetListener(displayValueEditText);
            onFocusChangeListener = new OnFocusChangeListener(textInputLayout,
                    displayValueEditText);
            onButtonClickListener = new OnButtonClickListener(displayValueEditText,
                    onDateSetListener, fragmentManager);

            displayValueEditText.addTextChangedListener(onValueChangedListener);
            displayValueEditText.setOnFocusChangeListener(onFocusChangeListener);

            clearButton.setOnClickListener(onButtonClickListener);
            datePickerButton.setOnClickListener(onButtonClickListener);
            datePickerButtonNow.setOnClickListener(onButtonClickListener);
            displayValueEditText.setOnClickListener(onButtonClickListener);
        }

        public void update(DataEntityDate dataEntity) {
            CharSequence hint = !isEmpty(dataEntity.getValue()) ? null :
                    onFocusChangeListener.getHint();

            onValueChangedListener.setDataEntity(dataEntity);
            //onDateSetListener.setDataEntity(dataEntity);

            textInputLayout.setHint(hint);
            textViewLabel.setText(dataEntity.getLabel());
            displayValueEditText.setText(dataEntity.getValue());
        }
    }

    private static class OnValueChangedListener extends AbsTextWatcher {
        private DataEntityDate dataEntity;

        public void setDataEntity(DataEntityDate dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
                dataEntity.setValue(editable.toString());
            }
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {
        private static final String EMPTY_FIELD = "";

        private final EditText editText;
        private final OnDateSetListener onDateSetListener;
        private final FragmentManager fragmentManager;
        private final Calendar calendar;

        public OnButtonClickListener(EditText editText, OnDateSetListener onDateSetListener,
                                     FragmentManager fragmentManager) {
            this.editText = editText;
            this.onDateSetListener = onDateSetListener;
            this.fragmentManager = fragmentManager;
            this.calendar = Calendar.getInstance();
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.row_date_picker_edit_text ||
                    view.getId() == R.id.row_date_picker_button_pick) {
                DatePickerDialogFragment.newInstance(false,
                        onDateSetListener).show(fragmentManager, TAG);
            } else if (view.getId() == R.id.button_clear) {
                editText.setText(EMPTY_FIELD);
            } else if (view.getId() == R.id.row_date_picker_button_today) {
                onDateSetListener.onDateSet(null, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
    }

    private static class OnFocusChangeListener implements View.OnFocusChangeListener {
        private final TextInputLayout textInputLayout;
        private final EditText editText;
        private final CharSequence hint;

        public OnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
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

    private static class OnDateSetListener implements DatePickerDialog.OnDateSetListener {
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private final EditText editText;

        public OnDateSetListener(EditText editText) {
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

    public static class DatePickerDialogFragment extends DialogFragment {
        private static final String ARG_ALLOW_DATES_IN_FUTURE = "arg:allowDatesInFuture";
        private DatePickerDialog.OnDateSetListener onDateSetListener;

        public static DialogFragment newInstance(boolean allowDatesInFuture,
                                                 DatePickerDialog.OnDateSetListener
                                                         onDateSetListener) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(ARG_ALLOW_DATES_IN_FUTURE, allowDatesInFuture);

            DatePickerDialogFragment fragment = new DatePickerDialogFragment();
            fragment.setArguments(arguments);
            fragment.setOnDateSetListener(onDateSetListener);

            return fragment;
        }

        private void setOnDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener) {
            this.onDateSetListener = dateSetListener;
        }

        private boolean isAllowDatesInFuture() {
            return getArguments().getBoolean(ARG_ALLOW_DATES_IN_FUTURE, false);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), onDateSetListener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            if (!isAllowDatesInFuture()) {
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
            return datePickerDialog;
        }
    }
}
