package org.hisp.dhis.client.sdk.ui.rows;

import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import static android.text.TextUtils.isEmpty;

public final class EditTextRowView implements IRowView {

    public EditTextRowView() {
        // explicit empty constructor
    }

    @Override
    public ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater,
                                         ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(EditTextRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        return new EditTextRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false), type);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, DataEntity entity) {
        ((EditTextRowViewHolder) holder).update(entity);
    }

    private static class EditTextRowViewHolder extends RecyclerView.ViewHolder {
        private static final int LONG_TEXT_LINE_COUNT = 3;

        public final TextView textViewLabel;
        public final TextInputLayout textInputLayout;
        public final EditText editText;

        public final OnFocusChangeListener focusChangeListener;
        public final OnValueChangedListener onValueChangedListener;

        public EditTextRowViewHolder(View itemView, DataEntity.Type type) {
            super(itemView);

            textViewLabel = (TextView) itemView
                    .findViewById(R.id.textview_row_label);
            textInputLayout = (TextInputLayout) itemView
                    .findViewById(R.id.edittext_row_textinputlayout);
            editText = (EditText) itemView
                    .findViewById(R.id.edittext_row_edittext);

            if (!configureViews(type)) {
                throw new IllegalArgumentException("unsupported view type");
            }

            focusChangeListener = new OnFocusChangeListener(textInputLayout, editText);
            onValueChangedListener = new OnValueChangedListener();

            editText.setOnFocusChangeListener(focusChangeListener);
            editText.addTextChangedListener(onValueChangedListener);
        }

        public void update(DataEntity entity) {
            onValueChangedListener.setDataEntity(entity);
            textViewLabel.setText(entity.getLabel());
            editText.setText(entity.getValue());

            CharSequence hint = !isEmpty(entity.getValue()) ? null : focusChangeListener.getHint();
            textInputLayout.setHint(hint);
        }

        private boolean configureViews(DataEntity.Type entityType) {
            switch (entityType) {
                case TEXT:
                    return configure(entityType, R.string.enter_text,
                            InputType.TYPE_CLASS_TEXT, true);
                case LONG_TEXT:
                    return configure(entityType, R.string.enter_long_text,
                            InputType.TYPE_CLASS_TEXT, false);
                case NUMBER:
                    return configure(entityType, R.string.enter_number,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                                    InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER:
                    return configure(entityType, R.string.enter_integer,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER_NEGATIVE:
                    return configure(entityType, R.string.enter_negative_integer,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER_ZERO_OR_POSITIVE:
                    return configure(entityType, R.string.enter_positive_integer_or_zero,
                            InputType.TYPE_CLASS_NUMBER, true);
                case INTEGER_POSITIVE:
                    return configure(entityType, R.string.enter_positive_integer,
                            InputType.TYPE_CLASS_NUMBER, true);
                default:
                    return false;
            }
        }

        private boolean configure(DataEntity.Type type, @StringRes int hint, int inputType,
                                  boolean singleLine) {
            textInputLayout.setHint(editText.getContext().getString(hint));
            editText.setInputType(inputType);
            editText.setSingleLine(singleLine);
            editText.setFilters(new InputFilter[]{
                    new ValueFilter(type)
            });

            if (!singleLine) {
                editText.setLines(LONG_TEXT_LINE_COUNT);
            }

            return true;
        }
    }

    private static class OnValueChangedListener extends AbsTextWatcher {
        private DataEntity dataEntity;

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
                dataEntity.updateValue(editable.toString());
            }
        }
    }

    private static class OnFocusChangeListener implements View.OnFocusChangeListener {
        private final TextInputLayout textInputLayout;
        private final EditText editText;
        private final CharSequence hint;

        public OnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
            this.textInputLayout = inputLayout;
            this.editText = editText;
            this.hint = textInputLayout.getHint();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                textInputLayout.setHint(hint);
            } else {
                if (!isEmpty(editText.getText().toString())) {
                    textInputLayout.setHint(null);
                }
            }
        }

        public CharSequence getHint() {
            return hint;
        }
    }

    private static class ValueFilter implements InputFilter {
        private final DataEntity.Type type;

        public ValueFilter(DataEntity.Type type) {
            this.type = type;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // perform validation
            // if (entity.validateValue(source)) {
            //     return "";
            // }

            return source;
        }
    }
}
