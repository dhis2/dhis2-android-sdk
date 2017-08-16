/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import static android.text.TextUtils.isEmpty;

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
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class EventDatePickerRow extends AbsDatePickerRow {
    private static final String TAG = "EventDatePickerRow";
    private final Event mEvent;
    private final boolean mAllowDatesInFuture;

    public EventDatePickerRow(String label, Event event, boolean allowDatesInFuture) {
        this.mAllowDatesInFuture = allowDatesInFuture;
        mLabel = label;
        mEvent = event;
        checkNeedsForDescriptionButton();

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
//            detailedInfoButton = root.findViewById(R.id.detailed_info_button_layout); // need to keep reference
            holder = new DatePickerRowHolder(root, inflater.getContext(), mAllowDatesInFuture);

            root.setTag(holder);
            view = root;
        }

        if (!isEditable()) {
            holder.clearButton.setEnabled(false);
            holder.textLabel.setEnabled(false); //change color
            holder.pickerInvoker.setEnabled(false);
        } else {
            holder.clearButton.setEnabled(true);
            holder.textLabel.setEnabled(true);
            holder.pickerInvoker.setEnabled(true);
        }
//        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));
        holder.updateViews(mLabel, mEvent);
//
//        if(isDetailedInfoButtonHidden())
//        {
//
//            holder.detailedInfoButton.setVisibility(View.INVISIBLE);
//        }
//        else {
//            holder.detailedInfoButton.setVisibility(View.VISIBLE);
//        }


        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.EVENT_DATE.ordinal();
    }

    private class DatePickerRowHolder {
        final TextView textLabel;
        final TextView pickerInvoker;
        final ImageButton clearButton;
//        final View detailedInfoButton;
        final DateSetListener dateSetListener;
        final OnEditTextClickListener invokerListener;
        final ClearButtonListener clearButtonListener;

        public DatePickerRowHolder(View root, Context context, boolean allowDatesInFuture) {
            textLabel = (TextView) root.findViewById(R.id.text_label);
            pickerInvoker = (TextView) root.findViewById(R.id.date_picker_text_view);
            clearButton = (ImageButton) root.findViewById(R.id.clear_text_view);
//            this.detailedInfoButton = detailedInfoButton;

            dateSetListener = new DateSetListener(pickerInvoker);
            invokerListener = new OnEditTextClickListener(context, dateSetListener,
                    allowDatesInFuture, pickerInvoker);
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

    private static class ClearButtonListener implements OnClickListener {
        private final TextView textView;
        private Event event;
        private DataValue value;

        public ClearButtonListener(TextView textView) {
            this.textView = textView;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        @Override
        public void onClick(View view) {
            textView.setText(EMPTY_FIELD);
            event.setEventDate(null);
            if(value == null) value = new DataValue();
            value.setValue(EMPTY_FIELD);
            Dhis2Application.getEventBus().post(new RowValueChangedEvent(value, DataEntryRowTypes.EVENT_DATE.toString()));
        }
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private static final String DATE_FORMAT = "YYYY-MM-dd";
        private final TextView textView;
        private Event event;
        private DataValue value;

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
            if (value == null) value = new DataValue();

            if (event.getEventDate() != null)
                value.setValue(event.getEventDate());

            String newValue = date.toString(DATE_FORMAT);
            textView.setText(newValue);

            if (!newValue.equals(value.getValue())) {
                value.setValue(newValue);
                event.setEventDate(value.getValue());
                Dhis2Application.getEventBus().post(new RowValueChangedEvent(value, DataEntryRowTypes.EVENT_DATE.toString()));
            }
        }
    }
}
