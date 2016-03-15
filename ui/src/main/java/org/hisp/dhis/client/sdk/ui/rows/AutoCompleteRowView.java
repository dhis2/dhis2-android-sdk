package org.hisp.dhis.client.sdk.ui.rows;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.dialogs.AutoCompleteDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.DefaultPickable;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.IPickable;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class AutoCompleteRowView implements IRowView {
    private ArrayList<IPickable> options;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater, ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(AutoCompleteRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        return new AutoCompleteRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_autocomplete, parent, false), type, fragmentManager);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, DataEntity dataEntity) {
        AutoCompleteRowViewHolder autoCompleteRowViewHolder = (AutoCompleteRowViewHolder) holder;

        autoCompleteRowViewHolder.textViewLabel.setText(dataEntity.getLabel());
        autoCompleteRowViewHolder.optionText.setText(dataEntity.getValue());
        autoCompleteRowViewHolder.onClearListener.setDataEntity(dataEntity);
        autoCompleteRowViewHolder.onEditTextClickedListener.setOptions(options);
        autoCompleteRowViewHolder.onOptionSelectedListener.setDataEntity(dataEntity);

        autoCompleteRowViewHolder.setOnOptionSelectedListener();
    }

    public void setOptions(ArrayList<IPickable> options) {
        this.options = options;
    }

    private static class AutoCompleteRowViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewLabel;
        public final TextInputLayout textInputLayout;
        public final EditText optionText;
        public final ImageButton clearButton;

        public final OnClearListener onClearListener;
        public final OnValueChangedListener onValueChangedListener;
        public final OnFocusChangeListener onFocusChangeListener;
        public final OnEditTextClickedListener onEditTextClickedListener;
        public OnOptionSelectedListener onOptionSelectedListener;

        public AutoCompleteRowViewHolder(View itemView, DataEntity.Type type,
                                         FragmentManager fragmentManager) {
            super(itemView);

            textViewLabel = (TextView) itemView.findViewById(R.id.autocomplete_row_label);
            textInputLayout = (TextInputLayout) itemView.findViewById(
                    R.id.autocomplete_row_text_input_layout);

            optionText = (EditText) itemView.findViewById(R.id.autocomplete_row_option_text);
            clearButton = (ImageButton) itemView.findViewById(R.id.button_clear);

            textInputLayout.setHint(itemView.getContext().getString(R.string.find_option));

            onClearListener = new OnClearListener(optionText);
            onFocusChangeListener = new OnFocusChangeListener(textInputLayout, optionText);
            onValueChangedListener = new OnValueChangedListener();
            onOptionSelectedListener = new OnOptionSelectedListener(optionText);
            onEditTextClickedListener = new OnEditTextClickedListener(fragmentManager, onOptionSelectedListener);


            optionText.addTextChangedListener(onValueChangedListener);
            optionText.setOnFocusChangeListener(onFocusChangeListener);
            optionText.setOnClickListener(onEditTextClickedListener);
            clearButton.setOnClickListener(onClearListener);
        }

        public void setOnOptionSelectedListener() {
            if (onEditTextClickedListener != null && onOptionSelectedListener != null) {
                onEditTextClickedListener.setListener(onOptionSelectedListener);
            }
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

    public static class OnOptionSelectedListener implements AutoCompleteDialogFragment.OnOptionSelectedListener {
        private final TextView valueTextView;
        private DataEntity dataEntity;

        public OnOptionSelectedListener(TextView valueTextView) {
            this.valueTextView = valueTextView;
        }

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onOptionSelected(IPickable pickable) {
            valueTextView.setText(pickable.toString());
            dataEntity.updateValue(pickable.toString());
        }
    }

    private static class OnClearListener implements View.OnClickListener {
        private final EditText editText;
        private DataEntity dataEntity;
        private static final String EMPTY_FIELD = "";

        public OnClearListener(EditText editText) {
            this.editText = editText;
        }

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onClick(View view) {
            editText.setText(EMPTY_FIELD);
            dataEntity.updateValue(EMPTY_FIELD);
        }
    }

    private static class OnEditTextClickedListener implements View.OnClickListener {
        private FragmentManager fragmentManager;
        private ArrayList<IPickable> options;
        AutoCompleteDialogFragment mOptionDialogFragment;

        private OnOptionSelectedListener onOptionSelectedListener;

        public OnEditTextClickedListener(FragmentManager fragmentManager, OnOptionSelectedListener onOptionSelectedListener) {
            this.fragmentManager = fragmentManager;
            this.onOptionSelectedListener = onOptionSelectedListener;

            if (mOptionDialogFragment != null) {
                mOptionDialogFragment.setOnOptionSelectedListener(onOptionSelectedListener);
            }
        }

        @Override
        public void onClick(View v) {
            if (options == null) {
                options = new ArrayList<>();
                options.add(new DefaultPickable("Male", "Male"));
                options.add(new DefaultPickable("Female", "Female"));
            }

            mOptionDialogFragment = AutoCompleteDialogFragment.newInstance("Find option:", options, onOptionSelectedListener);
            mOptionDialogFragment.show(fragmentManager, "tag");
        }

        public void setOptions(ArrayList<IPickable> options) {
            this.options = options;
        }

        public void setListener(OnOptionSelectedListener listener) {
            AutoCompleteDialogFragment mOptionDialogFragment;
            this.onOptionSelectedListener = listener;
            if (listener != null) {
                mOptionDialogFragment = (AutoCompleteDialogFragment) fragmentManager.findFragmentByTag("tag");
                if (mOptionDialogFragment != null) {
                    mOptionDialogFragment.setOnOptionSelectedListener(listener);
                }
            }
        }
    }
}
