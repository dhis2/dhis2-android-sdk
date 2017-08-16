package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;


import static org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.AbsDatePickerRow
        .EMPTY_FIELD;

import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow.TextRow;

public class IntegerPositiveEditTextRow extends TextRow {
    private static String rowTypeTemp;

    public IntegerPositiveEditTextRow(String label, boolean mandatory, String warning,
            BaseValue baseValue,
            DataEntryRowTypes rowType) {
        mLabel = label;
        mMandatory = mandatory;
        mWarning = warning;
        mValue = baseValue;
        mRowType = rowType;

        if (!DataEntryRowTypes.INTEGER_POSITIVE.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        checkNeedsForDescriptionButton();
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.INTEGER_POSITIVE.ordinal();
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

            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint(R.string.enter_positive_integer);
            editText.setFilters(new InputFilter[]{new PosFilter()});
            editText.setSingleLine(true);

            OnTextChangeListener listener = new OnTextChangeListener();
            listener.setRow(this);
            listener.setRowType(rowTypeTemp);
            holder = new ValueEntryHolder(label, mandatoryIndicator, warningLabel, errorLabel, editText, listener);
            holder.listener.setBaseValue(mValue);
            holder.editText.addTextChangedListener(listener);

            rowTypeTemp = mRowType.toString();
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

        if (mError == null) {
            holder.errorLabel.setVisibility(View.GONE);
        } else {
            holder.errorLabel.setVisibility(View.VISIBLE);
            holder.errorLabel.setText(mError);
        }

        if (!mMandatory) {
            holder.mandatoryIndicator.setVisibility(View.GONE);
        } else {
            holder.mandatoryIndicator.setVisibility(View.VISIBLE);
        }

        holder.editText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }


    private static class PosFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    public class MinMaxInputFilter implements InputFilter {
        /**
         * Minimum allowed value for the input.
         * Null means there is no minimum limit.
         */
        private Integer minAllowed;

        /**
         * Maximum allowed value for the input.
         * Null means there is no maximum limit.
         */
        private Integer maxAllowed;

        public MinMaxInputFilter(Integer min){
            this.minAllowed=min;
        }

        public MinMaxInputFilter(Integer min, Integer max){
            this(min);
            this.maxAllowed=max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                // Remove the string out of destination that is to be replaced
                String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
                // Add the new string in
                newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
                if(newVal.length()>1 && newVal.startsWith("0")) {
                    return "";
                }
                int input = Integer.parseInt(newVal);
                if (inRange(input)) {
                    return null;
                }
            }catch (NumberFormatException nfe) {
            }
            return "";
        }

        /**
         * Checks if the value is between the specified range.
         *
         * @param value
         * @return
         */
        public boolean inRange(Integer value){
            boolean isMinOk=true;
            boolean isMaxOk=true;
            //No bounds -> ok
            if(minAllowed==null && maxAllowed==null){
                return true;
            }
            //Check minimum
            if(minAllowed!=null){
                if(value==null){
                    isMinOk=false;
                }else{
                    isMinOk=minAllowed<=value;
                }
            }
            //Check maximum
            if(maxAllowed!=null){
                if(value==null){
                    isMaxOk=false;
                }else{
                    isMaxOk=value<=maxAllowed;
                }
            }
            return isMinOk && isMaxOk;
        }
    }

}
