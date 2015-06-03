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

package org.hisp.dhis.android.sdk.utils.ui.adapters;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import org.hisp.dhis.android.sdk.utils.ui.adapters.rows.dataentry.DataEntryRow;
import org.hisp.dhis.android.sdk.utils.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataValueAdapter extends AbsAdapter<DataEntryRow> {

    private static final String CLASS_TAG = DataValueAdapter.class.getSimpleName();

    private Map<String, Integer> dataElementsToRowIndexMap;
    private final FragmentManager mFragmentManager;

    public DataValueAdapter(FragmentManager fragmentManager,
                            LayoutInflater inflater) {
        super(inflater);
        mFragmentManager = fragmentManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getData() != null) {
            DataEntryRow dataEntryRow = getData().get(position);
            View view = dataEntryRow.getView(mFragmentManager, getInflater(), convertView, parent);
            view.setVisibility(View.VISIBLE); //in case recycling invisible view
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
            view.setId(position);
            if (dataEntryRow.isHidden()) {
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
    public void swapData(List<DataEntryRow> data) {
        boolean notifyAdapter = mData != data;
        mData = data;
        if (dataElementsToRowIndexMap == null)
            dataElementsToRowIndexMap = new HashMap<>();
        else {
            dataElementsToRowIndexMap.clear();
        }
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                DataEntryRow dataEntryRow = mData.get(i);
                BaseValue baseValue = dataEntryRow.getBaseValue();
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
        hideIndex(getIndex(dataElement));
    }

    public void hideIndex(int pos) {
        if (pos < 0) return;
        getData().get(pos).setHidden(true);
    }

    public void resetHiding() {
        if (mData == null) return;
        for (DataEntryRow row : mData) {
            row.setHidden(false);
        }
    }

    public int getIndex(String dataElement) {
        if (dataElementsToRowIndexMap.containsKey(dataElement))
            return dataElementsToRowIndexMap.get(dataElement);
        else return -1;
    }
}
