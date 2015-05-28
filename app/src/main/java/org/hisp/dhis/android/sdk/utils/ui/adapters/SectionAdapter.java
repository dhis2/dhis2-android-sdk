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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.fragments.dataentry.DataEntryFragmentSection;

import java.util.ArrayList;
import java.util.List;

public final class SectionAdapter extends AbsAdapter<DataEntryFragmentSection> {
    private static final String DROPDOWN = "dropDown";
    private static final String NON_DROPDOWN = "nonDropDown";
    /**
     * Copy of original mData because some rows may be removed if they are hidden by program rules
     */
    private List<DataEntryFragmentSection> mDataOriginalCopy;

    public SectionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(DROPDOWN)) {
            view = getInflater().inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag(DROPDOWN);
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getData().get(position).getLabel());

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(NON_DROPDOWN)) {
            view = getInflater().inflate(R.layout.
                    toolbar_spinner_item_actionbar, parent, false);
            view.setTag(NON_DROPDOWN);
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getData().get(position).getLabel());
        return view;
    }

    @Override
    public void swapData(List<DataEntryFragmentSection> data) {
        boolean notifyAdapter = mData != data;
        mData = data;
        if(mData == null) {
            mDataOriginalCopy = null;
        } else {
            mDataOriginalCopy = new ArrayList<>(mData);
        }

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }

    /**
     * Flags a section to be hidden
     * @param id
     */
    public void hideSection(String id) {
        if(mData==null) return;
        for(DataEntryFragmentSection section: mData) {
            if(section.getId()!=null && section.getId().equals(id)) {
                section.setHidden(true);
                mData.remove(section);
                return;
            }
        }
    }

    public void resetHiding() {
        if(mData==null) return;
        for(DataEntryFragmentSection s: mData) {
            s.setHidden(false);
        }
        mData = new ArrayList<>(mDataOriginalCopy);
    }
}
