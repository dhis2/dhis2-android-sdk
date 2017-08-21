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

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public abstract class AbsDatePickerRow extends Row {

    public static final String EMPTY_FIELD = "";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TAG = "AbsEnrollDatePickerRow";

    public AbsDatePickerRow() {

        checkNeedsForDescriptionButton();
    }


    @Override
    public abstract View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container);


    protected static class OnEditTextClickListener implements View.OnClickListener {
        private final Context context;
        private final DatePickerDialog.OnDateSetListener listener;
        private TextView dateText;
        private boolean allowDatesInFuture;

        public OnEditTextClickListener(Context context,
                DatePickerDialog.OnDateSetListener listener, boolean allowDatesInFuture,
                TextView dateText) {
            this.context = context;
            this.listener = listener;
            this.dateText = dateText;
            this.allowDatesInFuture = allowDatesInFuture;
        }

        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            if (!dateText.getText().toString().isEmpty()) {
                try {
                    calendar.setTime(
                            simpleDateFormat.parse(dateText.getText().toString()));
                } catch (ParseException e) {
                    Log.e(TAG, "Invalid date format, can't parse to put in the picker");
                    e.printStackTrace();
                }
            }
            DatePickerDialog picker = new DatePickerDialog(context, listener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            if(!allowDatesInFuture) {
            picker.getDatePicker().setMaxDate(DateTime.now().getMillis());
            }

            picker.show();
        }
    }
}
