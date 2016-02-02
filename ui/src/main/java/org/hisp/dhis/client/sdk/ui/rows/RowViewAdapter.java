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

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.models.DataEntity.Type;

import java.util.List;

public class RowViewAdapter extends Adapter<ViewHolder> {
    private final Context context;
    private List<IRowView> rowViews;

    public RowViewAdapter(Context context, List<IRowView> rowViews) {
        this.context = context;
        this.rowViews = rowViews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Type type = Type.values()[viewType];
        if (RowViewTypeMatcher.matchToRowView(type).equals(EditTextRowView.class)) {
            return null;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        rowViews.get(position).onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return rowViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        return rowViews.get(position).getRowType().ordinal();
    }

    public void update(List<IRowView> rowViews) {
        boolean notifyAdapter = this.rowViews != rowViews;
        this.rowViews = rowViews;

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }
}
