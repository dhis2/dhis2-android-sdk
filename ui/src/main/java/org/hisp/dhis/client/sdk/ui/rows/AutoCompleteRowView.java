package org.hisp.dhis.client.sdk.ui.rows;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class AutoCompleteRowView implements IRowView {
    private ArrayList<String> options;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager,
                                                      LayoutInflater inflater, ViewGroup parent,
                                                      DataEntity.Type type) {
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
        public final OnOptionSelectedListener onOptionSelectedListener;

        public AutoCompleteRowViewHolder(View itemView, DataEntity.Type type,
                                         FragmentManager fragmentManager) {
            super(itemView);

            textViewLabel = (TextView) itemView.findViewById(R.id.autocomplete_row_label);
            textInputLayout = (TextInputLayout) itemView.findViewById(
                    R.id.autocomplete_row_text_input_layout);

            optionText = (EditText) itemView.findViewById(R.id.autocomplete_row_option_text);
            clearButton = (ImageButton) itemView.findViewById(R.id.clear_autocomplete_row_view);

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

    private static class OnOptionSelectedListener implements OnTextViewClick.OnOptionItemSelectedListener {
        private final TextView valueTextView;
        private DataEntity dataEntity;
        public OnOptionSelectedListener(TextView valueTextView) {
            this.valueTextView = valueTextView;
        }

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onOptionSelected(String id, String name) {
            valueTextView.setText(name);
            dataEntity.updateValue(name);
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
        private ArrayList<String> options;

        private OnOptionSelectedListener onOptionSelectedListener;

        public OnEditTextClickedListener(FragmentManager fragmentManager, OnOptionSelectedListener onOptionSelectedListener) {
            this.fragmentManager = fragmentManager;
            this.onOptionSelectedListener = onOptionSelectedListener;
        }

        @Override
        public void onClick(View v) {
            if (options == null) {
                options = new ArrayList<>();
                this.options.add("Male");
                this.options.add("Female");
                this.options.add("Transgender");
            }
            OptionDialogFragment.newInstance(options, onOptionSelectedListener)
                    .show(fragmentManager, "tag");
        }

        public void setOptions(ArrayList<String> options) {
            this.options = options;
        }
    }

    public static class OptionDialogFragment extends AppCompatDialogFragment {
        private ArrayList<String> options;
        private static final String ARGS_OPTIONS = "extra:Options";
        private TextInputLayout textInputLayout;
        private EditText editText;
        private ProgressBar progressBar;
        private RecyclerView recyclerView;
        private OnOptionSelectedListener onOptionSelectedListener;

        public OptionDialogFragment() {
            //empty constructor
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            options = getArguments().getStringArrayList(ARGS_OPTIONS);
            return super.onCreateDialog(savedInstanceState);
        }

        @Override
        public void setupDialog(Dialog dialog, int style) {
            AppCompatDialog appCompatDialog = (AppCompatDialog) dialog;
            appCompatDialog.setContentView(R.layout.dialog_autocomplete);
            appCompatDialog.setTitle(dialog.getContext().getString(R.string.find_option));

            textInputLayout = (TextInputLayout) appCompatDialog.findViewById(R.id.dialog_autocomplete_textinputlayout);
            editText = (EditText) appCompatDialog.findViewById(R.id.dialog_autocomplete_edittext);

            progressBar = (ProgressBar) appCompatDialog.findViewById(R.id.dialog_autocomplete_progress_bar);
            recyclerView = (RecyclerView) appCompatDialog.findViewById(R.id.dialog_autocomplete_recyclerview);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            OptionDialogAdapter optionDialogAdapter = new OptionDialogAdapter(dialog, options, onOptionSelectedListener);
            recyclerView.setAdapter(optionDialogAdapter);

            OptionDialogFilterTextWatcher optionDialogFilterTextWatcher = new OptionDialogFilterTextWatcher();
            optionDialogFilterTextWatcher.setOptionDialogAdapter(optionDialogAdapter);
            editText.addTextChangedListener(optionDialogFilterTextWatcher);


            super.setupDialog(appCompatDialog, style);

        }

        public static OptionDialogFragment newInstance(ArrayList<String> options, OnOptionSelectedListener onOptionSelectedListener) {
            OptionDialogFragment optionDialogFragment = new OptionDialogFragment();
            Bundle args = new Bundle();
            args.putStringArrayList(ARGS_OPTIONS, options);
            optionDialogFragment.setArguments(args);
            optionDialogFragment.setOnOptionSelectedListener(onOptionSelectedListener);
            return optionDialogFragment;
        }

        public void setOnOptionSelectedListener(OnOptionSelectedListener onOptionSelectedListener) {
            this.onOptionSelectedListener = onOptionSelectedListener;
        }
    }

    private static class OptionDialogFilterTextWatcher extends AbsTextWatcher {
        OptionDialogAdapter optionDialogAdapter;

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            optionDialogAdapter.getFilter().filter(s);
        }

        public void setOptionDialogAdapter(OptionDialogAdapter optionDialogAdapter) {
            this.optionDialogAdapter = optionDialogAdapter;
        }
    }

    public static class OptionDialogAdapter extends RecyclerView.Adapter implements Filterable {
        private ArrayList<String> options;
        private OnOptionSelectedListener onOptionSelectedListener;
        private Dialog dialog;

        public OptionDialogAdapter(Dialog dialog, ArrayList<String> options, OnOptionSelectedListener onOptionSelectedListener) {
            this.dialog = dialog;
            this.options = options;
            this.onOptionSelectedListener = onOptionSelectedListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OptionDialogViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_autocomplete_dialog_row, parent, false), dialog, onOptionSelectedListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OptionDialogViewHolder optionDialogViewHolder = (OptionDialogViewHolder) holder;
            optionDialogViewHolder.optionValueTextView.setText(options.get(position));
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        @Override
        public Filter getFilter() {
            return new AutoCompleteRowFilter(this, options);
        }

        public void setOptions(ArrayList<String> filteredOptions) {
            this.options = filteredOptions;
        }
    }

    private static class AutoCompleteRowFilter extends Filter {
        private ArrayList<String> options;
        private ArrayList<String> filteredOptions;
        private OptionDialogAdapter optionDialogAdapter;

        public AutoCompleteRowFilter(OptionDialogAdapter optionDialogAdapter, ArrayList<String> options) {
            this.optionDialogAdapter = optionDialogAdapter;
            this.options = new ArrayList<>(options);
            this.filteredOptions = new ArrayList<>();
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredOptions.clear();
            final FilterResults filterResults = new FilterResults();

            if(constraint.length() == 0) {
                filteredOptions.addAll(options);
            }
            else {
                final String filterString = constraint.toString().toLowerCase().trim();
                for(String option : options) {
                    if(option.toLowerCase().trim().contains(filterString)) {
                        filteredOptions.add(option);
                    }
                }
            }

            filterResults.values = filteredOptions;
            filterResults.count = filteredOptions.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            optionDialogAdapter.setOptions((ArrayList<String>)results.values);
            optionDialogAdapter.notifyDataSetChanged();
        }
    }

    public static class OptionDialogViewHolder extends RecyclerView.ViewHolder {
        public final TextView optionValueTextView;
        public final OnTextViewClick onTextViewClick;

        public OptionDialogViewHolder(View itemView, Dialog parentDialog, OnOptionSelectedListener onOptionSelectedListener) {
            super(itemView);
            optionValueTextView = (TextView) itemView.findViewById(
                    R.id.autocomplete_dialog_row_label);

            onTextViewClick = new OnTextViewClick(parentDialog, optionValueTextView, onOptionSelectedListener);
            optionValueTextView.setOnClickListener(onTextViewClick);

        }
    }

    static class OnTextViewClick implements View.OnClickListener {
        private final TextView textView;
        private final OnOptionItemSelectedListener onOptionItemSelectedListener;
        private final Dialog parentDialog;

        public OnTextViewClick(Dialog parentDialog, TextView textView, OnOptionItemSelectedListener onOptionItemSelectedListener) {
            this.parentDialog = parentDialog;
            this.textView = textView;
            this.onOptionItemSelectedListener = onOptionItemSelectedListener;
        }

        @Override
        public void onClick(View v) {
            onOptionItemSelectedListener.onOptionSelected("", textView.getText().toString());
            parentDialog.dismiss();
        }

        interface OnOptionItemSelectedListener {
            void onOptionSelected(String id, String name);
        }
    }
}
