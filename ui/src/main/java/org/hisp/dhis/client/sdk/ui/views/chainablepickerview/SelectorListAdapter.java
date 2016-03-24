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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for filtered items in a AutoCompleteTextView, using {@link IPickable}
 */
public class SelectorListAdapter extends BaseAdapter implements Filterable {

    private List<IPickable> allItems;
    private List<IPickable> filteredItems;

    public void swapData(List<IPickable> items) {
        boolean notifyAdapter = items != allItems;
        allItems = items;
        filteredItems = allItems;

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }

    private void swapFilteredData(List<IPickable> items) {
        boolean notifyAdapter = items != filteredItems;
        filteredItems = items;

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public IPickable getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectorListViewHolder holder;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .picker_list_view_item, null);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            holder = new SelectorListViewHolder(textView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (SelectorListViewHolder) convertView.getTag();
        }
        holder.textView.setText(filteredItems.get(position).toString());
        return view;
    }

    @Override
    public Filter getFilter() {
        return new PickableFilter();
    }

    private class PickableFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<IPickable> values = new ArrayList<>();
            if (constraint != null && constraint.toString().length() > 0) {
                String filter = constraint.toString().toLowerCase();
                for (int i = 0; i < allItems.size(); i++) {
                    IPickable item = allItems.get(i);
                    String itemValue = item.toString().toLowerCase();
                    if (itemValue.contains(filter)) {
                        values.add(item);
                    }
                }
            } else {
                synchronized (this) {
                    values.addAll(allItems);
                }
            }
            filterResults.count = values.size();
            filterResults.values = values;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint == null || constraint.equals("")) {
                swapFilteredData(allItems);
            } else if (results.count == 0) {
                swapFilteredData(new ArrayList<IPickable>());
            } else {
                filteredItems = (ArrayList<IPickable>) results.values;
                swapFilteredData(filteredItems);
            }
            notifyDataSetChanged();
        }
    }

    private class SelectorListViewHolder {
        TextView textView;

        public SelectorListViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
