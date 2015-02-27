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

package org.hisp.dhis2.android.sdk.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.MetaDataController;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.MessageEvent;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis2.android.sdk.utils.views.BoolDataElementView;
import org.hisp.dhis2.android.sdk.utils.views.DataElementAdapterViewAbstract;
import org.hisp.dhis2.android.sdk.utils.views.DatePickerDataElementView;
import org.hisp.dhis2.android.sdk.utils.views.NumberDataElementView;
import org.hisp.dhis2.android.sdk.utils.views.OptionSetDataElementView;
import org.hisp.dhis2.android.sdk.utils.views.TextDataElementView;
import org.hisp.dhis2.android.sdk.utils.views.TrueOnlyDataElementView;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis2.android.sdk.utils.Preconditions.isNull;

/**
 * Fragment that can be used to show and edit data values for an existing local data entry element
 * like an Event, Tracked Entity Instance .. etc.
 * @author Simen Skogly Russnes on 27.02.15.
 */
public class EditItemFragment extends Fragment {

    public static final String CLASS_TAG = "EditItemFragment";

    private Button submitButton;
    private BaseModel editingItem; //todo make generic for other types like Tracked Entity Instance, Enrollment ..
    private LinearLayout elementContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_edit_item,
                container, false);
        setupUi(rootView);
        return rootView;
    }

    public void setupUi(View rootView) {
        if( editingItem == null ) isNull(editingItem, "Item cannot be null");

        submitButton = (Button) rootView.findViewById(R.id.edit_item_submitbutton);

        elementContainer = (LinearLayout) rootView.
                findViewById(R.id.edit_item_elementcontainer);
        setupDataEntryForm(elementContainer);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
                showFailedItemsFragment();
            }
        });
    }

    public void setupDataEntryForm(LinearLayout elementContainer) {

        if(editingItem instanceof Event) {
            Event event = (Event) editingItem;
            for(DataValue dataValue: event.getDataValues()) {
                elementContainer.addView(createDataElementView(dataValue).getView());
            }
        }
        //todo: in future maybe we add different kinds of things like TrackedEntityAttribute ..
    }

    /**
     * saves the changes made to the item that's being edited.
     */
    public void updateItem() {
        if(editingItem instanceof Event) {
            Event event = (Event) editingItem;
            for(DataValue dataValue: event.dataValues) {
                dataValue.update(false);
            }
            event.update(false);
        } else {
            //todo handle cases ..
        }
    }

    public DataElementAdapterViewAbstract createDataElementView(DataValue dataValue) {
        //todo consider making a more generic data entry view that can determine type itself.
        DataElement dataElement = MetaDataController.
                getDataElement(dataValue.dataElement);
        DataElementAdapterViewAbstract dataElementViewAbstract = null;
        String dataType = dataElement.getType();
        if ( dataElement.getOptionSet() != null )
        {
            OptionSet optionSet = MetaDataController.getOptionSet(dataElement.getOptionSet());
            if ( optionSet == null )
            {
                dataElementViewAbstract = new TextDataElementView( getActivity(), dataElement,
                        dataValue, false );
            }
            else
            {
                dataElementViewAbstract = new OptionSetDataElementView( getActivity(),
                        dataElement, dataValue, false );
            }
        }
        else
        {
            if ( dataType.equalsIgnoreCase( DataElement.VALUE_TYPE_BOOL ) )
            {
                dataElementViewAbstract = new BoolDataElementView( getActivity(),
                        dataElement, dataValue, false );
            }
            else if ( dataType.equalsIgnoreCase( DataElement.VALUE_TYPE_DATE ) )
            {
                dataElementViewAbstract = new DatePickerDataElementView( getActivity(),
                        dataElement, dataValue, false );
            }
            else if ( dataType.equalsIgnoreCase( DataElement.VALUE_TYPE_TRUE_ONLY ) )
            {
                dataElementViewAbstract = new TrueOnlyDataElementView( getActivity(),
                        dataElement, dataValue, false );
            }
            else if ( dataType.equalsIgnoreCase( DataElement.VALUE_TYPE_NUMBER ) || dataType.equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
            {
                dataElementViewAbstract = new NumberDataElementView( getActivity(),
                        dataElement, dataValue, false );
            }
            else
            {
                dataElementViewAbstract = new TextDataElementView( getActivity(),
                        dataElement, dataValue, false );
            }
        }
        return dataElementViewAbstract;
    }

    public void setItem(BaseModel object) {
        /* In case the object to be edited is in the FailedItem wrapper class, get the actual item */
        if( object instanceof FailedItem ) {
            FailedItem item = (FailedItem) object;
            Log.e(CLASS_TAG, item.itemType);
            if( item.itemType.equals(FailedItem.EVENT) )
                editingItem = Dhis2.getInstance().getDataValueController().getEvent(item.itemId);
        } else this.editingItem = object;
    }

    public void showFailedItemsFragment() {
        MessageEvent event = new MessageEvent(BaseEvent.EventType.showFailedItemsFragment);
        Dhis2Application.bus.post(event);
    }
}
