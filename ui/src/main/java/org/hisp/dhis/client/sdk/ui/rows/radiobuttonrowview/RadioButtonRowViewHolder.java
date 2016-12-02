package org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityRadioButtons;
import org.hisp.dhis.client.sdk.ui.utils.FormUtils;

import static org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview.RadioButtonRowView.FALSE;
import static org.hisp.dhis.client.sdk.ui.rows.radiobuttonrowview.RadioButtonRowView.TRUE;


final class RadioButtonRowViewHolder extends RecyclerView.ViewHolder {
    public final TextView labelTextView;
    public final RadioGroup radioGroup;
    public final RadioButton firstRadioButton;
    public final RadioButton secondRadioButton;
    public final RadioButtonRowOnCheckedChangedListener onCheckedChangedListener;

    public RadioButtonRowViewHolder(View itemView) {
        super(itemView);

        labelTextView = (TextView) itemView
                .findViewById(R.id.textview_row_label);
        radioGroup = (RadioGroup) itemView
                .findViewById(R.id.radiogroup_radiobutton_row);
        firstRadioButton = (RadioButton) itemView
                .findViewById(R.id.radiobutton_row_radiobutton_first);
        secondRadioButton = (RadioButton) itemView
                .findViewById(R.id.radiobutton_row_radiobutton_second);

        firstRadioButton.setText(itemView.getContext().getString(R.string.yes));
        secondRadioButton.setText(itemView.getContext().getString(R.string.no));

        onCheckedChangedListener = new RadioButtonRowOnCheckedChangedListener();
        radioGroup.setOnCheckedChangeListener(onCheckedChangedListener);
    }

    public void update(FormEntityRadioButtons dataEntity) {
        onCheckedChangedListener.setDataEntity(dataEntity);
        labelTextView.setText(FormUtils.getFormEntityLabel(dataEntity));

        if (TRUE.equals(dataEntity.getValue())) {
            firstRadioButton.setChecked(true);
        } else if (FALSE.equals(dataEntity.getValue())) {
            secondRadioButton.setChecked(true);
        } else {
            radioGroup.clearCheck();
        }

        if (dataEntity.isLocked()) {
            radioGroup.setEnabled(false);
            radioGroup.setClickable(false);

            firstRadioButton.setEnabled(false);
            firstRadioButton.setClickable(false);

            secondRadioButton.setEnabled(false);
            secondRadioButton.setClickable(false);
        } else {
            radioGroup.setEnabled(true);
            radioGroup.setClickable(true);

            firstRadioButton.setEnabled(true);
            firstRadioButton.setClickable(true);

            secondRadioButton.setEnabled(true);
            secondRadioButton.setClickable(true);
        }
    }
}
