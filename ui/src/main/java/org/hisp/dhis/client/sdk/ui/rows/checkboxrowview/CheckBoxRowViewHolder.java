package org.hisp.dhis.client.sdk.ui.rows.checkboxrowview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityCheckBox;
import org.hisp.dhis.client.sdk.ui.utils.FormUtils;

import static org.hisp.dhis.client.sdk.ui.rows.checkboxrowview.CheckBoxRowView.EMPTY_FIELD;
import static org.hisp.dhis.client.sdk.ui.rows.checkboxrowview.CheckBoxRowView.TRUE;


final class CheckBoxRowViewHolder extends RecyclerView.ViewHolder {
    final CheckBox checkBox;
    final TextView textViewLabel;
    final CheckBoxRowOnCheckBoxListener onCheckBoxListener;

    CheckBoxRowViewHolder(View itemView) {
        super(itemView);

        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_row_checkbox);
        textViewLabel = (TextView) itemView.findViewById(R.id.textview_row_label);

        onCheckBoxListener = new CheckBoxRowOnCheckBoxListener();
        checkBox.setOnCheckedChangeListener(onCheckBoxListener);

        CheckBoxRowViewOnRowClickListener rowClickListener = new CheckBoxRowViewOnRowClickListener(checkBox);
        itemView.setOnClickListener(rowClickListener);
    }

    public void update(FormEntityCheckBox dataEntity) {
        textViewLabel.setText(FormUtils.getFormEntityLabel(dataEntity));
        onCheckBoxListener.setDataEntity(dataEntity);

        if (EMPTY_FIELD.equals(dataEntity.getValue())) {
            checkBox.setChecked(false);
        } else if (TRUE.equals(dataEntity.getValue())) {
            checkBox.setChecked(true);
        }

        if (dataEntity.isLocked()) {
            checkBox.setClickable(false);
            checkBox.setEnabled(false);
        } else {
            checkBox.setClickable(true);
            checkBox.setEnabled(true);
        }
    }
}
