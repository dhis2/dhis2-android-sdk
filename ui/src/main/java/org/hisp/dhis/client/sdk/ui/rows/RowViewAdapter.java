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

package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.models.DataEntity.Type;

import java.util.ArrayList;
import java.util.List;

public class RowViewAdapter extends Adapter<ViewHolder> {
    private final List<DataEntity> dataEntities;
    private final List<IRowView> rowViews;

    public RowViewAdapter() {
        dataEntities = new ArrayList<>();
        rowViews = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return rowViews.get(viewType).onCreateViewHolder(LayoutInflater.from(parent.getContext()),
                parent, Type.values()[viewType]);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        rowViews.get(holder.getItemViewType()).onBindViewHolder(holder, getItem(position));
    }

    @Override
    public int getItemCount() {
        return dataEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) != null ? getItem(position).getType().ordinal() : -1;
    }

    private DataEntity getItem(int position) {
        return dataEntities.size() > position ? dataEntities.get(position) : null;
    }

    private IRowView onCreateRowView(int viewType) {
        DataEntity.Type type = Type.values()[viewType];

        if (EditTextRowView.class.equals(RowViewTypeMatcher.matchToRowView(type))) {
            return new EditTextRowView();
        }

        return null;
    }

    public void update(List<DataEntity> dataEntities) {
        this.dataEntities.clear();
        this.rowViews.clear();

        if (dataEntities != null) {
            this.dataEntities.addAll(dataEntities);

            for (DataEntity dataEntity : this.dataEntities) {
                int type = dataEntity.getType().ordinal();
                if (rowViews.size() <= type || rowViews.get(type) == null) {
                    rowViews.add(type, onCreateRowView(type));
                }
            }
        }

        notifyDataSetChanged();
    }
}
