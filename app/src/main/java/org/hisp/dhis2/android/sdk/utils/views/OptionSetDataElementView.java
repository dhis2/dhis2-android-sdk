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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Option;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;

public class OptionSetDataElementView
    extends DataElementAdapterViewAbstract
    implements OnItemSelectedListener
{
    
    /**
     * 
     */
    private Spinner spinner;

    /**
     * 
     */
    private OptionSet optionSet;

    /**
     * 
     */
    private ArrayAdapter<String> adapter;
    
    public OptionSetDataElementView( Context context, DataElement dataElement, DataValue dataValue,
                                     boolean mandatory)
    {
        super( context, dataElement, dataValue, mandatory );
        }

    @Override
    public void onItemSelected( AdapterView<?> parent, View view, int position, long id )
    {
        String please_select = this.getContext().getString( R.string.please_select );
        String value = (String) spinner.getItemAtPosition( position );
        this.dataValue.value = value;
    }

    @Override
    public void onNothingSelected( AdapterView<?> parent )
    {
    }

    @Override
    public View getView()
    {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.listview_item_optionset, null );

        TextView tv = (TextView) view.findViewById( R.id.optionSetNameTextView );
        tv.setText( dataElement.getName() );

        TextView mandatoryEt = (TextView) view.findViewById( R.id.optionSetMandatoryTextView );
        if ( compulsory ) mandatoryEt.setVisibility( View.VISIBLE );
        else mandatoryEt.setVisibility( View.INVISIBLE );

        spinner = (Spinner) view.findViewById( R.id.optionSetValueSpinner );
        spinner.setOnItemSelectedListener( this );

        this.optionSet = MetaDataController.getOptionSet(dataElement.getOptionSet());

        List<Option> options = optionSet.getOptions();
        List<String> optionNames = new ArrayList<String>();
        for(Option option: options) {
            optionNames.add(option.getName());
        }

        //options.add( this.getContext().getString( R.string.please_select ) );

        adapter = new ArrayAdapter<String>( this.getContext(),
                R.layout.spinner_item, optionNames );

        spinner.setAdapter( adapter );
        if( dataValue!=null )
        spinner.setSelection( findAdapterIndex( dataValue.value ) );

        return view;
    }

    private int findAdapterIndex( String str )
    {
        if ( str != null && !str.equals( "" ) )
        {
            for ( int i = 0; i < adapter.getCount(); i++ )
            {
                String s1 = adapter.getItem( i );
                if ( str.equals( s1 ) )
                {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @return
     */
    public Spinner getSpinner()
    {
        return spinner;
    }

    /**
     * 
     * @param spinner
     */
    public void setSpinner( Spinner spinner )
    {
        this.spinner = spinner;
    }

    /**
     * 
     * @return
     */
    public OptionSet getOptionSet()
    {
        return optionSet;
    }

    /**
     * 
     * @param optionSet
     */
    public void setOptionSet( OptionSet optionSet )
    {
        this.optionSet = optionSet;
    }
}
