package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;

public class CheckBoxRowView implements IRowView {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater, ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(EditTextRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        return new CheckBoxRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false), type);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, DataEntity dataEntity) {
        CheckBoxRowViewHolder checkBoxRowViewHolder = (CheckBoxRowViewHolder) holder;
    }



    private static class CheckBoxRowViewHolder extends RecyclerView.ViewHolder {

        public CheckBoxRowViewHolder(View itemView, DataEntity.Type type) {
            super(itemView);
        }
    }
}
