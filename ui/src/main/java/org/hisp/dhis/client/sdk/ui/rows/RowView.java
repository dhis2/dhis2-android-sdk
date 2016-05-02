package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.models.FormEntity;

public interface RowView {

    ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent);

    void onBindViewHolder(ViewHolder viewHolder, FormEntity formEntity);
}