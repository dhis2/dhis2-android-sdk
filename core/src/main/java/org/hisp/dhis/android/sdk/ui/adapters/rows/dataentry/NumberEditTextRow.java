package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;


import static org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.AbsDatePickerRow
        .EMPTY_FIELD;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class NumberEditTextRow extends TextRow {
    private static String rowTypeTemp;

    public NumberEditTextRow(String label, boolean mandatory, String warning,
            BaseValue baseValue,
            DataEntryRowTypes rowType) {
        mLabel = label;
        mMandatory = mandatory;
        mWarning = warning;
        mValue = baseValue;
        mRowType = rowType;

        if (!DataEntryRowTypes.NUMBER.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        checkNeedsForDescriptionButton();
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.NUMBER.ordinal();
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

            editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                    InputType.TYPE_NUMBER_FLAG_DECIMAL |
                    InputType.TYPE_NUMBER_FLAG_SIGNED);
            editText.setHint(R.string.enter_number);
            editText.setFilters(new InputFilter[]{new NumberFilter()});
            editText.setOnFocusChangeListener(new OnNumberFocusChangeListener(editText));
            editText.setSingleLine(true);

            OnTextChangeListener listener = new OnTextChangeListener();
            listener.setRow(this);
            listener.setRowType(rowTypeTemp);
            holder = new ValueEntryHolder(label, mandatoryIndicator, warningLabel, errorLabel,
                    editText, listener);
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

    private class OnNumberFocusChangeListener implements View.OnFocusChangeListener {
        private EditText mEditText;

        public OnNumberFocusChangeListener(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String text = mEditText.getText().toString();
                text = trimLeftZeroes(text);
                if (mEditText.getText() != null && text.endsWith(".")) {
                    text = removeLastChar(text);
                } else if (text.contains(".")) {
                    text = fixDecimals(text);
                }
                setText(text);
            }
        }

        @NonNull
        private String fixDecimals(String text) {
            int pointPosition = text.indexOf(".");
            String removeZeroes = text.substring(pointPosition + 1,
                    text.length());
            removeZeroes = trimDecimalsRightZeroes(removeZeroes);
            text = text.substring(0, pointPosition + 1) + removeZeroes;
            text = removeIncorrectDecimals(text);
            return text;
        }

        @NonNull
        private String removeIncorrectDecimals(String text) {
            if (text.endsWith(".0")) {
                text = text.substring(0, text.indexOf(".0"));
            }
            return text;
        }

        @NonNull
        private String removeLastChar(String text) {
            return text.substring(0, text.length() - 1);
        }

        private void setText(String substring) {
            //The edittext clear() should be called to avoid infinite loop listening the focus event
            mEditText.getText().clear();
            mEditText.append(substring);
        }

        private String trimLeftZeroes(String text) {
            if (text.startsWith("0") && text.length() > 1) {
                if (text.contains(".")) {
                    String decimals = text.substring(text.indexOf("."),
                            text.length());
                    text = new Integer(text.substring(0, text.indexOf("."))).toString() + decimals;
                } else {
                    text = new Integer(text).toString();
                }
            }
            return text;
        }

        private String trimDecimalsRightZeroes(String text) {
            if (text.endsWith("0") && text.length() > 1) {
                return trimDecimalsRightZeroes(removeLastChar(text));
            }
            return text;
        }
    }

    private static class NumberFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                Spanned spn, int spnStart, int spnEnd) {

            if (ifStartsWithPointReturnEmpty(str, spnStart, spnEnd)) return EMPTY_FIELD;

            CharSequence x = ifStartsWithZeroesReturnEmpty(str, spn);
            if (x != null) return x;

            return str;
        }

        @Nullable
        private CharSequence ifStartsWithZeroesReturnEmpty(CharSequence str, Spanned spn) {
            if ((str.length() > 0) && (str.charAt(0) == '0' && spn.length() > 0 && spn.charAt(0)
                    == '0')) {
                if (spn.length() > 1 && spn.toString().contains(".")) {
                    return str;
                }
                return EMPTY_FIELD;
            }
            return null;
        }

        private boolean ifStartsWithPointReturnEmpty(CharSequence str, int spnStart, int spnEnd) {
            if (str.length() > 0 && str.charAt(0) == '.' && spnStart == 0 && spnEnd == 1) {
                return true;
            }
            return false;
        }
    }
}
