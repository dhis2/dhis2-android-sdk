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

package org.hisp.dhis.android.sdk.ui.adapters;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataValueAdapter extends AbsAdapter<Row> {

    private static final String CLASS_TAG = DataValueAdapter.class.getSimpleName();

    private Map<String, Integer> dataElementsToRowIndexMap;
    private final FragmentManager mFragmentManager;
    private Map<String, Boolean> hiddenDataElementRows;
    private Map<String, String> warningDataElementRows;
    private Map<String, String> errorDataElementRows;

    public DataValueAdapter(FragmentManager fragmentManager,
                            LayoutInflater inflater) {
        super(inflater);
        mFragmentManager = fragmentManager;
        hiddenDataElementRows = new HashMap<>();
        warningDataElementRows = new HashMap<>();
        errorDataElementRows = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getData() != null) {
            Row dataEntryRow = getData().get(position);
            String id = dataEntryRow.getItemId();
            dataEntryRow.setWarning(warningDataElementRows.get(id));
            dataEntryRow.setError(errorDataElementRows.get(id));
            View view = dataEntryRow.getView(mFragmentManager, getInflater(), convertView, parent);
            view.setVisibility(View.VISIBLE); //in case recycling invisible view
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
            view.postInvalidate();
            view.setId(position);
            if(hiddenDataElementRows.containsKey(id)) {
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.postInvalidate();
                view.setVisibility(View.GONE);
            }
            return view;
        } else {
            return null;
        }
    }

    @Override
    public int getViewTypeCount() {
        return DataEntryRowTypes.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        if (getData() != null) {
            return getData().get(position).getViewType();
        } else {
            return 0;
        }
    }

    public View getView(String dataElement, View convertView, ViewGroup parent) {
        return getView(dataElementsToRowIndexMap.get(dataElement), convertView, parent);
    }

    @Override
    public void swapData(List<Row> data) {
        boolean notifyAdapter = mData != data;
        mData = data;
        if (dataElementsToRowIndexMap == null)
            dataElementsToRowIndexMap = new HashMap<>();
        else {
            dataElementsToRowIndexMap.clear();
        }
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                Row dataEntryRow = mData.get(i);
                BaseValue baseValue = dataEntryRow.getValue();
                if (baseValue instanceof DataValue) {
                    dataElementsToRowIndexMap.put(((DataValue) baseValue).getDataElement(), i);
                }
            }
        }

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }

    public void hideIndex(String dataElement) {
        if(hiddenDataElementRows == null) {
            hiddenDataElementRows = new HashMap<>();
        }
        hiddenDataElementRows.put(dataElement, true);
    }

    public void resetHiding() {
        if (mData == null) return;
        if(hiddenDataElementRows != null) {
            hiddenDataElementRows.clear();
        }
    }

    public void showWarningOnIndex(String dataElement, String warning) {
        if(warningDataElementRows == null) {
            warningDataElementRows = new HashMap<>();
        }
        warningDataElementRows.put(dataElement, warning);
    }

    public void resetWarnings() {
        if (mData == null) return;
        if(warningDataElementRows != null) {
            warningDataElementRows.clear();
        }
    }

    public void showErrorOnIndex(String dataElement, String warning) {
        if(errorDataElementRows == null) {
            errorDataElementRows = new HashMap<>();
        }
        errorDataElementRows.put(dataElement, warning);
    }

    public void resetErrors() {
        if (mData == null) return;
        if(errorDataElementRows != null) {
            errorDataElementRows.clear();
        }
    }

    public void hideAll() {
        for(String dataElement : dataElementsToRowIndexMap.keySet()) {
            hideIndex(dataElement);
        }
    }

    public int getIndex(String dataElement) {
        if (dataElementsToRowIndexMap != null && dataElementsToRowIndexMap.containsKey(dataElement)) {
            return dataElementsToRowIndexMap.get(dataElement);
        }
        else return -1;
    }
}
