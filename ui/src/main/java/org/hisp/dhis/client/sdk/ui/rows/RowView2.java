package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.models.DataEntity;

public interface RowView2 {

    ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup container);

    void bindViewHolder(ViewHolder viewHolder, DataEntity dataEntity);
}