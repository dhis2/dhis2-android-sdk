package org.hisp.dhis.client.sdk.ui.rows;

import android.text.InputFilter;
import android.text.Spanned;

public class EditTextRow extends Row {

    protected static int LONG_TEXT_LINE_COUNT = 3;
    private static String EMPTY_FIELD = "";

    public EditTextRow(String label, boolean mandatory, String warning, String baseValue, RowType rowType) {

        this.label = label;
        this.isMandatory = mandatory;
        this.warning = warning;
        this.value = baseValue;
        this.rowType = rowType;

        if (!RowType.TEXT.equals(rowType) &&
                !RowType.LONG_TEXT.equals(rowType) &&
                !RowType.NUMBER.equals(rowType) &&
                !RowType.INTEGER.equals(rowType) &&
                !RowType.INTEGER_NEGATIVE.equals(rowType) &&
                !RowType.INTEGER_ZERO_OR_POSITIVE.equals(rowType) &&
                !RowType.INTEGER_POSITIVE.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
    }

    @Override
    public RowType getRowType() {
        return rowType;
    }

    protected static class NegInpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) != '-')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    protected static class PosOrZeroFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spStart, int spEnd) {

            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            if ((spn.length() > 0) && (spStart == 0)
                    && (str.length() > 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    protected static class PosFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }
}
