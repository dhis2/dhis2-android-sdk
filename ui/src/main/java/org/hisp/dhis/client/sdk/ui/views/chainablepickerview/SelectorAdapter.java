/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for RecyclerView using {@link Picker} as child elements
 */
public class SelectorAdapter extends RecyclerView.Adapter<SelectorViewHolder> {

    private List<Picker> pickers;
    private List<SelectorViewHolder> selectorViewHolders = new ArrayList<>();

    @Override
    public SelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickerview,
                parent, false);
        SelectorViewHolder selectorViewHolder = new SelectorViewHolder(itemView);
        selectorViewHolders.add(selectorViewHolder);
        return selectorViewHolder;
    }

    @Override
    public void onBindViewHolder(SelectorViewHolder holder, int position) {
        Picker picker = pickers.get(position);
        holder.picker = picker;
        holder.adapter.swapData(picker.getPickableItems());
        holder.adapter.notifyDataSetChanged();
        holder.autoCompleteTextView.setOnItemClickListener(picker.getListener());
        holder.autoCompleteTextView.setHint(picker.getHint());

        TextWatcher previousTextWatcher = holder.textWatcher;
        if(previousTextWatcher != null) {
            holder.autoCompleteTextView.removeTextChangedListener(previousTextWatcher);
        }

        IPickable pickedItem = picker.getPickedItem();
        if(pickedItem != null) {
            holder.autoCompleteTextView.setText(pickedItem.toString());
        } else {
            holder.autoCompleteTextView.setText("");
        }
        holder.autoCompleteTextView.addTextChangedListener(picker);
        holder.autoCompleteTextView.setOnFocusChangeListener(picker.getOnFocusChangeListener());
        holder.textWatcher = picker;
    }

    @Override
    public int getItemCount() {
        return pickers.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recycle();
    }

    public void recycle() {
        for(Picker picker : pickers) {
            picker.recycle();
        }
        pickers.clear();
    }

    public void setPickers(List<Picker> pickers) {
        this.pickers = pickers;
    }

    public List<SelectorViewHolder> getSelectorViewHolders() {
        return selectorViewHolders;
    }
}
