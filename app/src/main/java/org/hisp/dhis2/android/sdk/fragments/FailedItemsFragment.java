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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.MessageEvent;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis2.android.sdk.utils.FailedItemsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that can be used to display a list of Data Entry items like Events that have failed
 * to upload.
 * @author Simen Skogly Russnes on 26.02.15.
 */
public class FailedItemsFragment extends Fragment {

    private static final String CLASS_TAG = "FailedItemsFragment";

    private ListView listView;
    private List<FailedItem> failedItems;
    private FailedItem selectedFailedItem;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.rootView = inflater.inflate(R.layout.fragment_failed_items,
                container, false);
        setupUi(rootView);
        return rootView;
    }

    public void setupUi(View rootView) {
        listView = (ListView) rootView.findViewById(R.id.list_view_failed_items);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showItemOptions(position);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditItemFragment(failedItems.get(position));
            }
        });

        setupListAdapter();
    }

    public void setupListAdapter() {
        failedItems = Dhis2.getInstance().getDataValueController().getFailedItems();
        if(failedItems == null || failedItems.isEmpty() ) return;

        ArrayList<String[]> values = new ArrayList<String[]>();
        for( FailedItem failedItem: failedItems ) {
            String[] value = new String[3];
            value[0] = failedItem.itemType;
            value[1] = failedItem.importSummary.status;
            value[2] = failedItem.importSummary.description;
            values.add(value);
        }
        FailedItemsListAdapter listAdapter = new FailedItemsListAdapter( getActivity(), values );
        listView.setAdapter(listAdapter);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showEditItemFragment(failedItems.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * deprecated. soon removed.
     * Signals the main activity to show edit item fragment for selected item
     * @param failedItem
     */
    public void showEditItemFragment(FailedItem failedItem) {
        //selectedFailedItem = failedItem;
        //MessageEvent event = new MessageEvent(BaseEvent.EventType.showEditItemFragment);
        //Dhis2Application.bus.post(event);
    }

    /**
     * Shows a dialog with options of what to do with the selected item.
     * @param itemIndex
     */
    public void showItemOptions(final int itemIndex) {
        AlertDialog.Builder builder;

        View layout = getActivity().getLayoutInflater().inflate(R.layout.listoptionsdialog, null);

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        final AlertDialog dialog = builder.create();

        ListView list = (ListView) layout.findViewById(R.id.listoptionsdialog_listview);
        String[] list_options = new String[]{"Edit", "Delete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list_options);
        list.setAdapter( adapter );
        list.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            public void onItemClick( AdapterView<?> parent, View view, int position, long id )
            {
                if(position == 0) {
                    showEditItemFragment(failedItems.get(itemIndex));
                } else if(position == 1) {
                    confirmDeleteFailedItem(itemIndex);
                }
                dialog.dismiss();
            }
        } );

        dialog.show();

    }

    public void confirmDeleteFailedItem(final int index) {
        final FailedItem failedItem = failedItems.get(index);
        Dhis2.getInstance().showConfirmDialog(getActivity(), getString(R.string.confirm),
                getString(R.string.confirm_delete_faileditem), getString(R.string.yes_option),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFailedItem(failedItem);
                        setupListAdapter();
                    }
                });
    }

    public void deleteFailedItem(FailedItem failedItem) {
        if(failedItem.itemType.equals(FailedItem.EVENT)) {
            Event event = Dhis2.getInstance().getDataValueController().getEvent(failedItem.itemId);
            if(event!=null) {
                event.delete(false);
            }
        }
        failedItem.delete(false);
    }

    public FailedItem getSelectedFailedItem() {
        return selectedFailedItem;
    }


}
