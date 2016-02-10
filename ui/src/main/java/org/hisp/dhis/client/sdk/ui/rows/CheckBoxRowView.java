package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;

public class CheckBoxRowView implements IRowView {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater, ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(CheckBoxRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        return new CheckBoxRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_checkbox, parent, false), type);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, DataEntity dataEntity) {
        CheckBoxRowViewHolder checkBoxRowViewHolder = (CheckBoxRowViewHolder) holder;

        checkBoxRowViewHolder.textViewLabel.setText(dataEntity.getLabel());
        checkBoxRowViewHolder.onCheckBoxListner.setDataEntity(dataEntity);
        checkBoxRowViewHolder.checkBox.setOnCheckedChangeListener(checkBoxRowViewHolder.onCheckBoxListner);
    }



    private static class CheckBoxRowViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textViewLabel;
        OnCheckBoxListener onCheckBoxListner;

        public CheckBoxRowViewHolder(View itemView, DataEntity.Type type) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_row_checkbox);
            textViewLabel = (TextView) itemView.findViewById(R.id.checkbox_row_label);

            onCheckBoxListner = new OnCheckBoxListener();
        }
    }

    private static class OnCheckBoxListener implements CompoundButton.OnCheckedChangeListener {
        private DataEntity dataEntity;
        private static final String TRUE = "true";
        private static final String EMPTY_FIELD = "";

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String newValue;
            if(isChecked) {
                newValue = TRUE;
            } else {
                newValue = EMPTY_FIELD;
            }

            if(!newValue.toString().equals(dataEntity.getValue())) {
                dataEntity.updateValue(newValue);
            }
        }
    }


}
