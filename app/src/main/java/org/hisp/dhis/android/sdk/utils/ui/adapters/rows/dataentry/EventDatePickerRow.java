/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.utils.ui.adapters.rows.dataentry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import static android.text.TextUtils.isEmpty;

public class EventDatePickerRow implements DataEntryRow {
    private static final String EMPTY_FIELD = "";
    private static final String DATE_FORMAT = "YYYY-MM-dd";

    private final String mLabel;
    private final Event mEvent;

    private boolean mHidden = false;
    private boolean editable = true;

    public EventDatePickerRow(String label, Event event) {
        mLabel = label;
        mEvent = event;
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        DatePickerRowHolder holder;

        if (convertView != null && convertView.getTag() instanceof DatePickerRowHolder) {
            view = convertView;
            holder = (DatePickerRowHolder) view.getTag();
        } else {
            View root = inflater.inflate(
                    R.layout.listview_row_event_datepicker, container, false);
            holder = new DatePickerRowHolder(root, inflater.getContext());

            root.setTag(holder);
            view = root;
        }

        if(!isEditable())
        {
            holder.clearButton.setEnabled(false);
            holder.textLabel.setEnabled(false); //change color
        }
        else
        {
            holder.clearButton.setEnabled(true);
            holder.textLabel.setEnabled(true);
        }

        holder.updateViews(mLabel, mEvent);
        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.EVENT_DATE.ordinal();
    }

    @Override
    public BaseValue getBaseValue() {
        return null;
    }

    private static class DatePickerRowHolder {
        final TextView textLabel;
        final TextView pickerInvoker;
        final ImageButton clearButton;
        final DateSetListener dateSetListener;
        final OnEditTextClickListener invokerListener;
        final ClearButtonListener clearButtonListener;

        public DatePickerRowHolder(View root, Context context) {
            textLabel = (TextView) root.findViewById(R.id.text_label);
            pickerInvoker = (TextView) root.findViewById(R.id.date_picker_text_view);
            clearButton = (ImageButton) root.findViewById(R.id.clear_text_view);

            dateSetListener = new DateSetListener(pickerInvoker);
            invokerListener = new OnEditTextClickListener(context, dateSetListener);
            clearButtonListener = new ClearButtonListener(pickerInvoker);

            clearButton.setOnClickListener(clearButtonListener);
            pickerInvoker.setOnClickListener(invokerListener);
        }

        public void updateViews(String label, Event event) {
            dateSetListener.setEvent(event);
            clearButtonListener.setEvent(event);

            String eventDate = null;
            if (event != null && event.getEventDate() != null
                    && !isEmpty(event.getEventDate())) {
                DateTime eventDateTime = DateTime.parse(event.getEventDate());
                eventDate = eventDateTime.toString(DATE_FORMAT);
            }

            textLabel.setText(label);
            pickerInvoker.setText(eventDate);
        }
    }

    private static class OnEditTextClickListener implements OnClickListener {
        private final Context context;
        private final DateSetListener listener;

        public OnEditTextClickListener(Context context,
                                       DateSetListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            LocalDate currentDate = new LocalDate();
            DatePickerDialog picker = new DatePickerDialog(context, listener,
                    currentDate.getYear(), currentDate.getMonthOfYear() - 1, currentDate.getDayOfMonth());
            picker.getDatePicker().setMaxDate(DateTime.now().getMillis());
            picker.show();
        }
    }

    private static class ClearButtonListener implements OnClickListener {
        private final TextView textView;
        private Event event;

        public ClearButtonListener(TextView textView) {
            this.textView = textView;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        @Override
        public void onClick(View view) {
            textView.setText(EMPTY_FIELD);
            event.setEventDate(EMPTY_FIELD);
        }
    }

    private static class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private static final String DATE_FORMAT = "YYYY-MM-dd";
        private final TextView textView;
        private Event event;
        private BaseValue value;
        public DateSetListener(TextView textView) {
            this.textView = textView;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);

            if(event.getEventDate() != null)
                value.setValue(event.getEventDate());

            String newValue = date.toString(DATE_FORMAT);
            textView.setText(newValue);

            if(!newValue.equals(value.getValue()))
            {
                value.setValue(newValue);
                event.setEventDate(value.getValue());
                Dhis2Application.getEventBus().post(new RowValueChangedEvent(value));
            }

        }
    }

    @Override
    public boolean isHidden() {
        return mHidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        mHidden = hidden;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
