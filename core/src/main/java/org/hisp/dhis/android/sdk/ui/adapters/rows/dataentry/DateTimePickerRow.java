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

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimePickerRow extends Row {
    private static final String EMPTY_FIELD = "";
    final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private final boolean mAllowDatesInFuture;
    Context mContext;

    public DateTimePickerRow(String label, boolean mandatory, String warning, BaseValue value,
            boolean allowDatesInFuture) {
        mAllowDatesInFuture = allowDatesInFuture;
        mLabel = label;
        mMandatory = mandatory;
        mValue = value;
        mWarning = warning;

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
                    R.layout.listview_row_datepicker, container, false);
//            detailedInfoButton = root.findViewById(R.id.detailed_info_button_layout);

            holder = new DatePickerRowHolder(root, inflater.getContext(), mAllowDatesInFuture);


            holder.mAlertDialog = holder.createDialog(mContext, holder.pickerInvoker, mValue);
            root.setTag(holder);
            view = root;
        }

        if (!isEditable()) {
            holder.clearButton.setEnabled(false);
            holder.pickerInvoker.setEnabled(false);
        } else {
            holder.clearButton.setEnabled(true);
            holder.pickerInvoker.setEnabled(true);
        }
//        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));

        holder.updateViews(mLabel, holder, mValue);
//        if(isDetailedInfoButtonHidden()) {
//            holder.detailedInfoButton.setVisibility(View.INVISIBLE);
//        }
//        else {
//            holder.detailedInfoButton.setVisibility(View.VISIBLE);
//        }

        if (mWarning == null) {
            holder.warningLabel.setVisibility(View.GONE);
        } else {
            holder.warningLabel.setVisibility(View.VISIBLE);
            holder.warningLabel.setText(mWarning);
        }

        if (mError == null) {
            holder.errorLabel.setVisibility(View.GONE);
        } else {
            holder.errorLabel.setVisibility(View.VISIBLE);
            holder.errorLabel.setText(mError);
        }

        if (!mMandatory) {
            holder.mandatoryIndicator.setVisibility(View.GONE);
        } else {
            holder.mandatoryIndicator.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.TIME.ordinal();
    }


    private class DatePickerRowHolder {
        final TextView textLabel;
        final TextView mandatoryIndicator;
        final TextView warningLabel;
        final TextView errorLabel;
        final TextView pickerInvoker;
        final ImageButton clearButton;
        //        final View detailedInfoButton;
        AlertDialog mAlertDialog;
        OnEditTextClickListener invokerListener;
        final ClearButtonListener clearButtonListener;
        boolean allowDatesInFuture;
        private View dialogView;
        private Button cancel;
        private Button okButton;
        private DatePicker datePicker;
        private TimePicker timePicker;

        public DatePickerRowHolder(View root, Context context, boolean allowDatesInFuture) {
            textLabel = (TextView) root.findViewById(R.id.text_label);
            mandatoryIndicator = (TextView) root.findViewById(R.id.mandatory_indicator);
            warningLabel = (TextView) root.findViewById(R.id.warning_label);
            errorLabel = (TextView) root.findViewById(R.id.error_label);
            pickerInvoker = (TextView) root.findViewById(R.id.date_picker_text_view);
            clearButton = (ImageButton) root.findViewById(R.id.clear_text_view);
            this.allowDatesInFuture = allowDatesInFuture;
//            this.detailedInfoButton = detailedInfoButton;

            mContext = context;


            dialogView = View.inflate(mContext, R.layout.time_date_picker, null);
            datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
            timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
            cancel = (Button) dialogView.findViewById(R.id.cancel);
            okButton = (Button) dialogView.findViewById(R.id.ok_button);
            clearButtonListener = new ClearButtonListener(pickerInvoker);

            clearButton.setOnClickListener(clearButtonListener);
        }

        private AlertDialog createDialog(Context context, final TextView pickerInvoker,
                final BaseValue baseValue) {
            final String VALUE_FORMAT = "%s-%s-%sT%s:%s";
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            timePicker.setIs24HourView(true);
            if (baseValue != null && baseValue.getValue()!=null && !baseValue.getValue().equals("")) {
                timePicker.setCurrentHour(getDateType(baseValue, Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(getDateType(baseValue, Calendar.MINUTE));
                datePicker.init(getDateType(baseValue, Calendar.YEAR),
                        getDateType(baseValue, Calendar.MONTH),
                        getDateType(baseValue, Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                                System.out.println("TimeDatePiker onDateChanged");
                                saveValue(timePicker, VALUE_FORMAT, datePicker, pickerInvoker,
                                        baseValue);
                            }
                        });
            }else{
                Calendar calendar = Calendar.getInstance();
                datePicker.init(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                                System.out.println("TimeDatePiker onDateChanged");
                                saveValue(timePicker, VALUE_FORMAT, datePicker, pickerInvoker,
                                        baseValue);
                            }
                        });
            }
            //Fix datepicker width
            try {
                LinearLayout linearLayout = ((LinearLayout) datePicker.getChildAt(0));
                if (linearLayout != null) {
                    linearLayout.setLayoutParams(
                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            okButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveValue(timePicker, VALUE_FORMAT, datePicker, pickerInvoker,
                            baseValue);
                    alertDialog.dismiss();
                }
            });
            alertDialog.setView(dialogView);
            return alertDialog;
        }

        public void updateViews(String label,
                DatePickerRowHolder holder,
                BaseValue baseValue) {
            if (mValue != null && mValue.getValue()!=null && !mValue.getValue().equals("")) {
                holder.datePicker.updateDate(getDateType(mValue, Calendar.YEAR),
                        getDateType(mValue, Calendar.MONTH),
                        getDateType(mValue, Calendar.DAY_OF_MONTH));
                holder.timePicker.setCurrentHour(getDateType(mValue, Calendar.HOUR_OF_DAY));
                holder.timePicker.setCurrentMinute(getDateType(mValue, Calendar.MINUTE));
            }
            clearButtonListener.setBaseValue(baseValue);

            textLabel.setText(label);

            pickerInvoker.setText(getValueToRender(baseValue.getValue()));


            invokerListener = new OnEditTextClickListener(mAlertDialog, allowDatesInFuture,
                    pickerInvoker, mValue);
            pickerInvoker.setOnClickListener(invokerListener);
        }

        private String getValueToRender (String value){
            if (value == null || value.isEmpty())
                return null;

            final String DATE_FORMAT_RENDER = "yyyy-MM-dd HH:mm";

            SimpleDateFormat toDateFormat = new SimpleDateFormat(DATE_FORMAT);
            SimpleDateFormat toStringFormat = new SimpleDateFormat(DATE_FORMAT_RENDER);
            Date date;
            String dateToRender = "";
            try {
                date = toDateFormat.parse(value);
                dateToRender = toStringFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return dateToRender;
        }

        private void saveValue(TimePicker timePicker, String DATE_FORMAT, DatePicker datePicker,
                TextView textView, BaseValue value) {
            String newValue = getFormattedValue(timePicker, DATE_FORMAT, datePicker);
            String textValue = getValueToRender(newValue);

            System.out.println("TimeDatePiker Saving value:" + newValue);

            if (!newValue.equals(value.getValue())) {
                textView.setText(textValue);
                value.setValue(newValue);
                Dhis2Application.getEventBus()
                        .post(new RowValueChangedEvent(value, DataEntryRowTypes.TIME.toString()));
            }
        }

        private String getFormattedValue(TimePicker timePicker, String DATE_FORMAT,
                DatePicker datePicker) {
            return String.format(DATE_FORMAT,
                    datePicker.getYear(), getFixedString(datePicker.getMonth() + 1), getFixedString(
                            datePicker.getDayOfMonth()),
                    getFixedString(timePicker.getCurrentHour()),
                    getFixedString(timePicker.getCurrentMinute()));
        }
    }

    private class OnEditTextClickListener implements OnClickListener {
        private final AlertDialog mAlertDialog;
        private final boolean allowDatesInFuture;
        TextView pickerInvoker;
        BaseValue baseValue;

        public OnEditTextClickListener(AlertDialog alertDialog, boolean allowDatesInFuture,
                TextView pickerInvoker, BaseValue baseValue) {
            this.mAlertDialog = alertDialog;
            this.allowDatesInFuture = allowDatesInFuture;
            this.pickerInvoker = pickerInvoker;
            this.baseValue = baseValue;
        }

        @Override
        public void onClick(View view) {
            mAlertDialog.show();
        }

    }

    private static class ClearButtonListener implements OnClickListener {
        private final TextView textView;
        private BaseValue value;

        public ClearButtonListener(TextView textView) {
            this.textView = textView;
        }

        public void setBaseValue(BaseValue value) {
            this.value = value;
        }

        @Override
        public void onClick(View view) {
            textView.setText(EMPTY_FIELD);
            value.setValue(EMPTY_FIELD);
            Dhis2Application.getEventBus()
                    .post(new RowValueChangedEvent(value, DataEntryRowTypes.TIME.toString()));
        }
    }

    private int getDateType(BaseValue value, int type) {
        Calendar calendar = getDateFromValue(value);
        return calendar.get(type);
    }

    private Calendar getDateFromValue(BaseValue value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(value.getValue());
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    private String getFixedString(int number) {
        if (String.valueOf(number).length() == 1) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }
}
