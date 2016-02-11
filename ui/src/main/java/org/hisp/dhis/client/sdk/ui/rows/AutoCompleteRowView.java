package org.hisp.dhis.client.sdk.ui.rows;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class AutoCompleteRowView implements IRowView {
    private ArrayList<String> options;

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

    }

    public void setOptions(ArrayList<String> options) {
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

        public AutoCompleteRowViewHolder(View itemView, DataEntity.Type type, FragmentManager fragmentManager) {
            super(itemView);

            textViewLabel = (TextView) itemView.findViewById(R.id.autocomplete_row_label);
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.autocomplete_row_text_input_layout);
            optionText = (EditText) itemView.findViewById(R.id.autocomplete_row_option_text);
            clearButton = (ImageButton) itemView.findViewById(R.id.clear_autocomplete_row_view);

            textInputLayout.setHint(itemView.getContext().getString(R.string.find_option));

            onClearListener = new OnClearListener(optionText);
            onFocusChangeListener = new OnFocusChangeListener(textInputLayout, optionText);
            onValueChangedListener = new OnValueChangedListener();
            onEditTextClickedListener = new OnEditTextClickedListener(fragmentManager, optionText);


            optionText.addTextChangedListener(onValueChangedListener);
            optionText.setOnFocusChangeListener(onFocusChangeListener);
            optionText.setOnClickListener(onEditTextClickedListener);
            clearButton.setOnClickListener(onClearListener);
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
        private EditText editText;
        private FragmentManager fragmentManager;
        private ArrayList<String> options;

//        private OnOptionSelectedListener listener;

        public OnEditTextClickedListener(FragmentManager fragmentManager, EditText editText) {
            this.fragmentManager = fragmentManager;
            this.editText = editText;
        }
        @Override
        public void onClick(View v) {
            OptionDialogFragment.newInstance(v.getContext(), editText, options).show(fragmentManager, "tag");
        }

        public void setOptions(ArrayList<String> options) {
            this.options = options;
        }
    }

    public static class OptionDialogFragment extends AppCompatDialogFragment {
        private ArrayList<String> options;
        private static final String ARGS_OPTIONS = "extra:Options";

        public OptionDialogFragment() {
            //empty constructor
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return super.onCreateDialog(savedInstanceState);
        }

        @Override
        public void setupDialog(Dialog dialog, int style) {
            AppCompatDialog appCompatDialog = (AppCompatDialog) dialog;
            appCompatDialog.setContentView(R.layout.dialog_autocomplete);
            appCompatDialog.setTitle(dialog.getContext().getString(R.string.find_option));


            super.setupDialog(appCompatDialog, style);

        }



        public static OptionDialogFragment newInstance(Context context, EditText editText, ArrayList<String> options) {
            OptionDialogFragment optionDialogFragment = new OptionDialogFragment();
            Bundle args = new Bundle();
            args.putStringArrayList(ARGS_OPTIONS, options);
            optionDialogFragment.setArguments(args);
            return optionDialogFragment;
        }


    }

    public class OptionDialogAdapter extends RecyclerView.Adapter {
        private ArrayList<String> options;

        public OptionDialogAdapter(ArrayList<String> options) {
            this.options = options;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OptionDialogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_autocomplete_dialog_row, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OptionDialogViewHolder optionDialogViewHolder = (OptionDialogViewHolder) holder;

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    public class OptionDialogViewHolder extends RecyclerView.ViewHolder {
        public final TextView optionValueTextView;

        public OptionDialogViewHolder(View itemView) {
            super(itemView);
            optionValueTextView = (TextView) itemView.findViewById(R.id.autocomplete_dialog_row_label);
        }
    }
}
