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

import android.support.v4.app.FragmentManager;
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
    private final FragmentManager fragmentManager;

    public RowViewAdapter(FragmentManager childFragmentManager) {
        dataEntities = new ArrayList<>();
        rowViews = new ArrayList<>();

        rowViews.add(Type.TEXT.ordinal(), new EditTextRowView());
        rowViews.add(Type.DATE.ordinal(), new DatePickerRowView());
        rowViews.add(Type.TRUE_ONLY.ordinal(), new CheckBoxRowView());
        rowViews.add(Type.AUTO_COMPLETE.ordinal(), new AutoCompleteRowView());
        rowViews.add(Type.COORDINATES.ordinal(), new CoordinateRowView());
        rowViews.add(Type.BOOLEAN.ordinal(), new RadioButtonRowView());
        rowViews.add(Type.INTEGER.ordinal(), new EditTextRowView());
        rowViews.add(Type.NUMBER.ordinal(), new EditTextRowView());
        rowViews.add(Type.LONG_TEXT.ordinal(), new EditTextRowView());
        rowViews.add(Type.INTEGER_NEGATIVE.ordinal(), new EditTextRowView());
        rowViews.add(Type.INTEGER_ZERO_OR_POSITIVE.ordinal(), new EditTextRowView());
        rowViews.add(Type.INTEGER_POSITIVE.ordinal(), new EditTextRowView());
        rowViews.add(Type.GENDER.ordinal(), new RadioButtonRowView());
        rowViews.add(Type.INDICATOR.ordinal(), new EditTextRowView());
        rowViews.add(Type.EVENT_DATE.ordinal(), new DatePickerRowView());
        rowViews.add(Type.ENROLLMENT_DATE.ordinal(), new DatePickerRowView());
        rowViews.add(Type.FILE.ordinal(), new EditTextRowView());
        rowViews.add(Type.INCIDENT_DATE.ordinal(), new DatePickerRowView());

        fragmentManager = childFragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return rowViews.get(viewType).onCreateViewHolder(fragmentManager,
                LayoutInflater.from(parent.getContext()), parent, Type.values()[viewType]);
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

    public void swap(List<DataEntity> dataEntities) {
        this.dataEntities.clear();

        if (dataEntities != null) {
            this.dataEntities.addAll(dataEntities);
        }

        notifyDataSetChanged();
    }
}
