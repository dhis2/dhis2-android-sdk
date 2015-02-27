/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis2.android.sdk.utils.views;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;

public class DatePickerDataElementView
    extends DataElementAdapterViewAbstract
    implements TextWatcher, OnClickListener, OnDateSetListener, DialogInterface.OnClickListener
{
    /**
     * 
     */
    private EditText dataElementEditText;

    /**
     * 
     */
    private DatePickerDialog datePickerDialog;

    public DatePickerDataElementView( Context context, DataElement dataElement, DataValue dataValue,
                                      boolean mandatory)
    {
        super( context, dataElement, dataValue, mandatory );
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after )
    {
        dataValue.value = this.dataElementEditText.getText().toString();
    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count )
    {
        dataValue.value = this.dataElementEditText.getText().toString();
    }

    @Override
    public void afterTextChanged( Editable s )
    {
        dataValue.value = this.dataElementEditText.getText().toString();
    }

    @Override
    public View getView()
    {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.listview_item_datepicker, null );

        TextView tv = (TextView) view.findViewById( R.id.dateNameTextView );
        tv.setText( dataElement.getName() );

        TextView mandatoryEt = (TextView) view.findViewById( R.id.datePickerMandatoryTextView );
        if ( compulsory ) mandatoryEt.setVisibility( View.VISIBLE );
        else mandatoryEt.setVisibility( View.INVISIBLE );

        this.getDatePickerDialog();
        dataElementEditText = (EditText) view.findViewById( R.id.dateEditText );
        dataElementEditText.addTextChangedListener( this );
        dataElementEditText.setClickable( true );
        dataElementEditText.setOnClickListener( this );
        dataElementEditText.setHint( "YYYY-MM-DD" );

        if(dataValue != null && dataValue.value!=null && !dataValue.value.isEmpty())
            dataElementEditText.setText(dataValue.value);

        return view;
    }

    @Override
    public void onClick( View v )
    {
        this.getDatePickerDialog().show();
    }

    public EditText getDataElementEditText()
    {
        return dataElementEditText;
    }

    public void setDataElementEditText( EditText dataElementEditText )
    {
        this.dataElementEditText = dataElementEditText;
    }

    public DatePickerDialog getDatePickerDialog()
    {
        if(getContext()==null) Log.e("ddd", "context is null");
        if ( datePickerDialog == null )
        {
            Calendar calendar = Calendar.getInstance();
            int mYear, mMonth, mDay;
            mYear = calendar.get( Calendar.YEAR );
            mMonth = calendar.get( Calendar.MONTH );
            mDay = calendar.get( Calendar.DAY_OF_MONTH );
            datePickerDialog = new DatePickerDialog( getContext(), this, mYear, mMonth, mDay );
            datePickerDialog.setCancelable( true );
            datePickerDialog.setButton( DialogInterface.BUTTON_NEGATIVE, getContext().getString( R.string.cancel ),
                this );
            datePickerDialog.setButton( DialogInterface.BUTTON_POSITIVE, getContext().getString( R.string.set ), this );
        }
        return datePickerDialog;
    }

    public void setDatePickerDialog( DatePickerDialog datePickerDialog )
    {
        this.datePickerDialog = datePickerDialog;
    }

    @Override
    public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth )
    {

    }

    @Override
    public void onClick( DialogInterface dialog, int which )
    {
        if ( which == DialogInterface.BUTTON_NEGATIVE )
        {
            getDatePickerDialog().cancel();
        }
        else
        {
            DatePicker datePicker = getDatePickerDialog().getDatePicker();

            int year = datePicker.getYear();
            int dayOfMonth = datePicker.getDayOfMonth();
            int monthOfYear = datePicker.getMonth();

            String month = monthOfYear + 1 + "";
            if ( month.length() == 1 )
            {
                month = "0" + month;
            }

            String date = String.valueOf( dayOfMonth );

            if ( date.length() == 1 )
            {
                date = "0" + date;
            }

            dataElementEditText.setText( year + "-" + month + "-" + date );
        }

    }

}
