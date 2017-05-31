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
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.DatePickerDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityDate;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.RaisedButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;


public class DatePickerRowView implements RowView {
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // we need fragment manager to display DatePickerDialogFragment
    private final FragmentManager fragmentManager;

    public DatePickerRowView(FragmentManager fragmentManager) {
        this.fragmentManager = isNull(fragmentManager, "fragmentManager must not be null");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new DatePickerRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_datepicker, parent, false), fragmentManager);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityDate entity = (FormEntityDate) formEntity;
        ((DatePickerRowViewHolder) viewHolder).update(entity);
    }

    private static class DatePickerRowViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewLabel;
        public final EditText editText;

        public final RaisedButton datePickerButtonNow;
        public final RaisedButton datePickerButton;
        public final ImageButton clearButton;

        public final OnValueChangedListener onValueChangedListener;
        public final OnDateSetListener onDateSetListener;
        public final OnButtonClickListener onButtonClickListener;

        public DatePickerRowViewHolder(View itemView, FragmentManager fragmentManager) {
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

            onValueChangedListener = new OnValueChangedListener();
            onDateSetListener = new OnDateSetListener(editText);
            onButtonClickListener = new OnButtonClickListener(
                    editText, fragmentManager, onDateSetListener);

            editText.addTextChangedListener(onValueChangedListener);
            editText.setOnClickListener(onButtonClickListener);

            clearButton.setOnClickListener(onButtonClickListener);
            datePickerButton.setOnClickListener(onButtonClickListener);
            datePickerButtonNow.setOnClickListener(onButtonClickListener);
        }

        public void update(FormEntityDate formEntity) {
            // update callbacks with current entities
            onValueChangedListener.setDataEntity(formEntity);
            textViewLabel.setText(formEntity.getLabel());
            editText.setText(formEntity.getValue());
        }
    }

    private static class OnValueChangedListener extends AbsTextWatcher {
        private FormEntityDate dataEntity;

        public void setDataEntity(FormEntityDate dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
                dataEntity.setValue(editable.toString(), true);
            }
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {
        private static final String EMPTY_STRING = "";

        private final EditText editText;
        private final Calendar calendar;
        private final FragmentManager fragmentManager;
        private final OnDateSetListener onDateSetListener;

        public OnButtonClickListener(EditText editText, FragmentManager fragmentManager,
                                     OnDateSetListener onDateSetListener) {
            this.editText = editText;
            this.calendar = Calendar.getInstance();
            this.fragmentManager = fragmentManager;
            this.onDateSetListener = onDateSetListener;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.row_date_picker_edit_text ||
                    view.getId() == R.id.row_date_picker_button_pick) {
                DatePickerDialogFragment datePicker = DatePickerDialogFragment.newInstance(false);
                datePicker.setOnDateSetListener(onDateSetListener);
                datePicker.show(fragmentManager);
            } else if (view.getId() == R.id.button_clear) {
                editText.setText(EMPTY_STRING);
            } else if (view.getId() == R.id.row_date_picker_button_today) {
                onDateSetListener.onDateSet(null,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
    }

    private static class OnDateSetListener implements DatePickerDialog.OnDateSetListener {
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
}
