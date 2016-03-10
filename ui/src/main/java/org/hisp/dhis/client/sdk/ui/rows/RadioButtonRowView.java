package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.IDataEntity;


public class RadioButtonRowView implements IRowView {
    private static final String EMPTY_FIELD = "";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater, ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(RadioButtonRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        return new RadioButtonRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_radiobutton, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, IDataEntity dataEntity) {
        RadioButtonRowViewHolder radioButtonRowViewHolder = (RadioButtonRowViewHolder) holder;
        DataEntity entity = (DataEntity) dataEntity;
        radioButtonRowViewHolder.onCheckedChangedListener.setDataEntity(entity);
        radioButtonRowViewHolder.labelTextView.setText(entity.getLabel());
        if(TRUE.equals(dataEntity.getValue())) {
            radioButtonRowViewHolder.firstRadioButton.setChecked(true);
        }
        else if(FALSE.equals(dataEntity.getValue())) {
            radioButtonRowViewHolder.secondRadioButton.setChecked(true);
        }
        else {
            radioButtonRowViewHolder.radioGroup.clearCheck();
        }

    }

    private static class RadioButtonRowViewHolder extends RecyclerView.ViewHolder {
        public final TextView labelTextView;
        public final RadioGroup radioGroup;
        public final RadioButton firstRadioButton;
        public final RadioButton secondRadioButton;
        public final OnCheckedChangedListener onCheckedChangedListener;

        public RadioButtonRowViewHolder(View itemView) {
            super(itemView);
            labelTextView = (TextView) itemView
                    .findViewById(R.id.textview_row_label);
            radioGroup = (RadioGroup) itemView
                    .findViewById(R.id.radiogroup_radiobutton_row);
            firstRadioButton = (RadioButton) itemView
                    .findViewById(R.id.first_radiobutton_radiobutton_row);
            secondRadioButton = (RadioButton) itemView
                    .findViewById(R.id.second_radiobutton_radiobutton_row);

            firstRadioButton.setText(itemView.getContext().getString(R.string.yes));
            secondRadioButton.setText(itemView.getContext().getString(R.string.no));
            onCheckedChangedListener = new OnCheckedChangedListener();
            radioGroup.setOnCheckedChangeListener(onCheckedChangedListener);
        }
    }

    private static class OnCheckedChangedListener implements RadioGroup.OnCheckedChangeListener {
        private DataEntity dataEntity;

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            String newValue = "";

            if(checkedId == R.id.first_radiobutton_radiobutton_row) {
                newValue = TRUE;
            }
            else if (checkedId == R.id.second_radiobutton_radiobutton_row) {
                newValue = FALSE;
            }
            else {
                newValue = EMPTY_FIELD;
            }
            dataEntity.updateValue(newValue);
        }
    }
}
