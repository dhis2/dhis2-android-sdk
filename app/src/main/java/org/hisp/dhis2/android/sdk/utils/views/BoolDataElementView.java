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

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

/**
 * @author Long Ngo Thanh
 * @author Simen Skogly Russnes
 *
 */
public class BoolDataElementView
    extends DataElementAdapterViewAbstract
    implements OnItemSelectedListener
{

    /**
     * 
     */
    private Spinner dataElementSpinner;

    public BoolDataElementView( Context context, DataElement dataElement, DataValue dataValue,
                                boolean mandatory)
    {
        super( context, dataElement, dataValue, mandatory );
    }

    @Override
    public void onItemSelected( AdapterView<?> parent, View view, int position, long id )
    {
        String value = (String) dataElementSpinner.getItemAtPosition( position );
        dataValue.value = value;
    }

    @Override
    public void onNothingSelected( AdapterView<?> parent )
    {
    }

    @Override
    public View getView()
    {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.listview_item_bool, null );

        TextView tv = (TextView) view.findViewById( R.id.boolNameTextView );
        tv.setText( dataElement.getName() );

        TextView mandatoryEt = (TextView) view.findViewById( R.id.boolMandatoryTextView );
        if ( compulsory ) mandatoryEt.setVisibility( View.VISIBLE );
        else mandatoryEt.setVisibility( View.INVISIBLE );

        dataElementSpinner = (Spinner) view.findViewById( R.id.boolValueSpinner );
        dataElementSpinner.setOnItemSelectedListener( this );
        updateSpinner();

        return view;
    }

    private void updateSpinner()
    {
        if ( dataValue == null || dataValue.value == null || dataValue.value.isEmpty() )
            dataElementSpinner.setSelection( 0 );
        else if ( dataValue.value.equals( "true" ))
            dataElementSpinner.setSelection( 1 );
        else if ( dataValue.value.equals( "false" ))
            dataElementSpinner.setSelection( 2 );
    }

    /**
     * @return the dataElementSpinner
     */
    public Spinner getDataElementSpinner()
    {
        return dataElementSpinner;
    }

    /**
     * @param dataElementSpinner the dataElementSpinner to set
     */
    public void setDataElementSpinner( Spinner dataElementSpinner )
    {
        this.dataElementSpinner = dataElementSpinner;
    }
}
