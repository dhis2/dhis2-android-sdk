package org.hisp.dhis.client.sdk.ui.rows;

import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.models.DataEntity.Type;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public final class EditTextRowView implements IRowView {
    private final DataEntity dataEntity;

    public EditTextRowView(DataEntity dataEntity) {
        isNull(dataEntity, "row entity must not be null");

        this.dataEntity = dataEntity;

        if (!RowViewTypeMatcher.matchToRowView(dataEntity
                .getType()).equals(EditTextRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new EditTextRowViewHolder(inflater.inflate(
                R.layout.row_edit_text, parent, false), dataEntity);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder) {
        ((EditTextRowViewHolder) holder).textInputLayout.setHint(dataEntity.getLabel());
        ((EditTextRowViewHolder) holder).editText.setHint(dataEntity.getValue());
    }

    @Override
    public Type getRowType() {
        return dataEntity.getType();
    }

    @Override
    public DataEntity getDataEntity() {
        return dataEntity;
    }

    private static class EditTextRowViewHolder extends RecyclerView.ViewHolder {
        private static final int LONG_TEXT_LINE_COUNT = 3;

        public final TextInputLayout textInputLayout;
        public final EditText editText;

        public EditTextRowViewHolder(View itemView, DataEntity dataEntity) {
            super(itemView);

            textInputLayout = (TextInputLayout) itemView
                    .findViewById(R.id.edit_text_row_text_input_layout);
            editText = (EditText) itemView
                    .findViewById(R.id.edit_text_row_edit_text);

            if (!configureViews(dataEntity)) {
                throw new IllegalArgumentException("unsupported view type");
            }
        }

        private boolean configureViews(DataEntity entity) {
            switch (entity.getType()) {
                case TEXT:
                    return configure(entity, R.string.enter_text,
                            InputType.TYPE_CLASS_TEXT, true);
                case LONG_TEXT:
                    return configure(entity, R.string.enter_long_text,
                            InputType.TYPE_CLASS_TEXT, false);
                case NUMBER:
                    return configure(entity, R.string.enter_number,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                                    InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER:
                    return configure(entity, R.string.enter_integer,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER_NEGATIVE:
                    //editText.setFilters(new InputFilter[]{new EditTextRowView.NegInpFilter()});
                    return configure(entity, R.string.enter_negative_integer,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER_ZERO_OR_POSITIVE:
                    // editText.setFilters(new InputFilter[]{new EditTextRowView.PosOrZeroFilter()});
                    return configure(entity, R.string.enter_positive_integer_or_zero,
                            InputType.TYPE_CLASS_NUMBER, true);
                case INTEGER_POSITIVE:
                    // editText.setFilters(new InputFilter[]{new EditTextRowView.PosFilter()});
                    return configure(entity, R.string.enter_positive_integer,
                            InputType.TYPE_CLASS_NUMBER, true);
                default:
                    return false;
            }
        }

        private boolean configure(DataEntity entity, @StringRes int hint, int inputType, boolean singleLine) {
            textInputLayout.setHint(editText.getContext().getString(hint));
            editText.setInputType(inputType);
            editText.setSingleLine(singleLine);
            editText.setFilters(new InputFilter[]{
                    new ValueFilter(entity)
            });

            if (!singleLine) {
                editText.setLines(LONG_TEXT_LINE_COUNT);
            }

            return true;
        }
    }

    private static class ValueFilter implements InputFilter {
        private final DataEntity entity;

        public ValueFilter(DataEntity entity) {
            this.entity = entity;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (entity.validateValue(source)) {
                return "";
            }

            return source;
        }
    }
//
//    protected static class NegInpFilter implements InputFilter {
//
//        @Override
//        public CharSequence filter(CharSequence str, int start, int end,
//                                   Spanned spn, int spnStart, int spnEnd) {
//
//            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) != '-')) {
//                return EMPTY_FIELD;
//            }
//
//            return str;
//        }
//    }
//
//    protected static class PosOrZeroFilter implements InputFilter {
//
//        @Override
//        public CharSequence filter(CharSequence str, int start, int end,
//                                   Spanned spn, int spStart, int spEnd) {
//
//            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
//                return EMPTY_FIELD;
//            }
//
//            if ((spn.length() > 0) && (spStart == 0)
//                    && (str.length() > 0) && (str.charAt(0) == '0')) {
//                return EMPTY_FIELD;
//            }
//
//            return str;
//        }
//    }
//
//    protected static class PosFilter implements InputFilter {
//
//        @Override
//        public CharSequence filter(CharSequence str, int start, int end,
//                                   Spanned spn, int spnStart, int spnEnd) {
//
//            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) == '0')) {
//                return EMPTY_FIELD;
//            }
//
//            return str;
//        }
//    }
}
