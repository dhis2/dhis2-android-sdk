package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;


import static org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.AbsEnrollmentDatePickerRow
        .EMPTY_FIELD;

import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

public class URLEditTextRow extends Row {
    private static String rowTypeTemp;
    protected Integer mErrorStringId;

    protected TextView.OnEditorActionListener mOnEditorActionListener;

    public void setOnEditorActionListener(
            TextView.OnEditorActionListener onEditorActionListener) {
        mOnEditorActionListener = onEditorActionListener;
    }

    public URLEditTextRow(String label, boolean mandatory, String warning,
            BaseValue baseValue,
            boolean b, DataEntryRowTypes rowType,
            Event event) {
        mLabel = label;
        mMandatory = mandatory;
        mWarning = warning;
        mValue = baseValue;
        mRowType = rowType;

        if (!DataEntryRowTypes.URL.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        checkNeedsForDescriptionButton();
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.URL.ordinal();
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
            View convertView, ViewGroup container) {
        View view;
        final ValueEntryHolder holder;

        if (convertView != null && convertView.getTag() instanceof ValueEntryHolder) {
            view = convertView;
            holder = (ValueEntryHolder) view.getTag();
            holder.listener.onRowReused();
        } else {
            View root = inflater.inflate(R.layout.listview_row_edit_text, container, false);
            TextView label = (TextView) root.findViewById(R.id.text_label);
            TextView mandatoryIndicator = (TextView) root.findViewById(R.id.mandatory_indicator);
            TextView warningLabel = (TextView) root.findViewById(R.id.warning_label);
            TextView errorLabel = (TextView) root.findViewById(R.id.error_label);
            EditText editText = (EditText) root.findViewById(R.id.edit_text_row);
//            detailedInfoButton = root.findViewById(R.id.detailed_info_button_layout);

            editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            editText.setHint(R.string.enter_url);
            editText.setSingleLine(true);

            URLWatcher listener = new URLWatcher(editText, errorLabel);
            listener.setRow(this);
            rowTypeTemp = mRowType.toString();
            listener.setRowType(rowTypeTemp);
            holder = new ValueEntryHolder(label, mandatoryIndicator, warningLabel, errorLabel, editText, listener);
            holder.listener.setBaseValue(mValue);
            holder.editText.addTextChangedListener(listener);

            root.setTag(holder);
            view = root;
        }

        //when recycling views we don't want to keep the focus on the edittext
        //holder.editText.clearFocus();

        if (!isEditable()) {
            holder.editText.setEnabled(false);
        } else {
            holder.editText.setEnabled(true);
        }

        holder.textLabel.setText(mLabel);
        holder.listener.setBaseValue(mValue);
//        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));

        holder.editText.setText(mValue.getValue());
        holder.editText.setSelection(holder.editText.getText().length());

//        if(isDetailedInfoButtonHidden()) {
//            holder.detailedInfoButton.setVisibility(View.INVISIBLE);
//        }
//        else {
//            holder.detailedInfoButton.setVisibility(View.VISIBLE);
//        }

        if (mWarning == null) {
            holder.warningLabel.setVisibility(View.GONE);
        } else {
            holder.warningLabel.setVisibility(View.VISIBLE);
            holder.warningLabel.setText(mWarning);
        }

        if (mErrorStringId == null) {
            holder.errorLabel.setVisibility(View.GONE);
        } else {
            holder.errorLabel.setVisibility(View.VISIBLE);
            holder.errorLabel.setText(mErrorStringId);
        }

        if (!mMandatory) {
            holder.mandatoryIndicator.setVisibility(View.GONE);
        } else {
            holder.mandatoryIndicator.setVisibility(View.VISIBLE);
        }

        holder.editText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    private class URLWatcher extends OnTextChangeListener{
        final private EditText mEditText;
        final private TextView mErrorLabel;


        public URLWatcher(EditText editText, TextView errorLabel) {
            super();
            mEditText = editText;
            mErrorLabel = errorLabel;
        }

        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            String text = mEditText.getText().toString();
            if(validateURL(text)){
                setError(null);
            }else {
                setError(R.string.error_url);
            }
        }

        private void setError(Integer stringId) {
            if(stringId == null) {
                mError = null;
                mErrorLabel.setVisibility(View.GONE);
                mErrorLabel.setText("");
            }else{
                mErrorLabel.setVisibility(View.VISIBLE);
                mErrorLabel.setText(stringId);
                mError = mErrorLabel.getText().toString();
            }
            mErrorStringId = stringId;
        }
    }

    public class ValueEntryHolder {
        final TextView textLabel;
        final TextView mandatoryIndicator;
        final TextView warningLabel;
        final TextView errorLabel;
        final EditText editText;
        //        final View detailedInfoButton;
        final OnTextChangeListener listener;

        public ValueEntryHolder(TextView textLabel,
                TextView mandatoryIndicator, TextView warningLabel,
                TextView errorLabel, EditText editText,
                OnTextChangeListener listener) {
            this.textLabel = textLabel;
            this.mandatoryIndicator = mandatoryIndicator;
            this.warningLabel = warningLabel;
            this.errorLabel = errorLabel;
            this.editText = editText;
//            this.detailedInfoButton = detailedInfoButton;
            this.listener = listener;
        }

    }


    class OnTextChangeListener extends AbsTextWatcher {
        private BaseValue value;
        Row row;
        String rowType;
        public void setRowType(String type){
            rowType = type;
        }
        public void setRow(Row row) {
            this.row = row;
        }

        public void setBaseValue(BaseValue value) {
            this.value = value;
        }

        public void onRowReused() {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String newValue = s != null ? s.toString() : EMPTY_FIELD;
            if (!newValue.equals(value.getValue()) &&  validateURL(newValue)) {
                value.setValue(newValue);
                RowValueChangedEvent rowValueChangeEvent = new RowValueChangedEvent(value, rowType);
                rowValueChangeEvent.setRow(row);
                Dhis2Application.getEventBus().post(rowValueChangeEvent);
            }
        }
    }

    public boolean validateURL(String url) {
        String regExp = "^(http|https)://[a-z0-9]+([-.][a-z0-9]+)*[.][a-z]{2,6}(:[0-9]{1,5})?([/].*)?$";
        if(url.matches(regExp) || url.length()==0){
            return true;
        }else{
            return false;
        }
    }
}