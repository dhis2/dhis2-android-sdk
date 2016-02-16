package org.hisp.dhis.client.sdk.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.DefaultPickable;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.IPickable;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteDialogFragment extends AppCompatDialogFragment {

    private static final String TAG = AutoCompleteDialogFragment.class.getSimpleName();
    private ArrayList<IPickable> options;
    private String title;
    private static final String ARGS_OPTIONS = "extra:Options";
    private static final String ARGS_TITLE = "extra:Title";

    private TextInputLayout textInputLayout;
    private EditText editText;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private OnOptionSelectedListener onOptionSelectedListener;
    private OptionDialogAdapter optionDialogAdapter;

    public AutoCompleteDialogFragment() {
        //empty constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        options = getArguments().getParcelableArrayList(ARGS_OPTIONS);
        title = getArguments().getString(ARGS_TITLE);
        return super.onCreateDialog(savedInstanceState);
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        AppCompatDialog appCompatDialog = (AppCompatDialog) dialog;
        appCompatDialog.setContentView(R.layout.dialog_autocomplete);
        appCompatDialog.setTitle(title);

        textInputLayout = (TextInputLayout) appCompatDialog.findViewById(R.id.dialog_autocomplete_textinputlayout);
        editText = (EditText) appCompatDialog.findViewById(R.id.dialog_autocomplete_edittext);

        progressBar = (ProgressBar) appCompatDialog.findViewById(R.id.dialog_autocomplete_progress_bar);
        recyclerView = (RecyclerView) appCompatDialog.findViewById(R.id.dialog_autocomplete_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        OptionDialogAdapter optionDialogAdapter = new OptionDialogAdapter(appCompatDialog, options, onOptionSelectedListener);


        recyclerView.setAdapter(optionDialogAdapter);

        OptionDialogFilterTextWatcher optionDialogFilterTextWatcher = new OptionDialogFilterTextWatcher();
        optionDialogFilterTextWatcher.setOptionDialogAdapter(optionDialogAdapter);
        editText.addTextChangedListener(optionDialogFilterTextWatcher);

        super.setupDialog(appCompatDialog, style);

    }


    public static AutoCompleteDialogFragment newInstance(String title, List<IPickable> options, OnOptionSelectedListener onOptionSelectedListener) {
        AutoCompleteDialogFragment autoCompleteDialogFragment = new AutoCompleteDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGS_OPTIONS, (ArrayList) options);
        args.putString(ARGS_TITLE, title);
        autoCompleteDialogFragment.setArguments(args);
        autoCompleteDialogFragment.setOnOptionSelectedListener(onOptionSelectedListener);
        return autoCompleteDialogFragment;
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener onOptionSelectedListener) {
        this.onOptionSelectedListener = onOptionSelectedListener;
    }


    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(IPickable pickable);
    }

    public OptionDialogAdapter getOptionDialogAdapter() {
        return optionDialogAdapter;
    }

    public void setOptions(ArrayList<IPickable> options) {
        this.options = options;
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
        private ArrayList<IPickable> options;
        private OnOptionSelectedListener onOptionSelectedListener;
        private Dialog dialog;

        public OptionDialogAdapter(Dialog dialog, ArrayList<IPickable> options, OnOptionSelectedListener onOptionSelectedListener) {
            this.dialog = dialog;
            this.options = options;
            this.onOptionSelectedListener = onOptionSelectedListener;
        }

        public void setParentDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OptionDialogViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_row_autocomplete_dialog, parent, false), dialog, onOptionSelectedListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OptionDialogViewHolder optionDialogViewHolder = (OptionDialogViewHolder) holder;
            optionDialogViewHolder.optionValueTextView.setText(options.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        @Override
        public Filter getFilter() {
            return new AutoCompleteRowFilter(this, options);
        }

        public void setOptions(ArrayList<IPickable> filteredOptions) {
            this.options = filteredOptions;
        }
    }

    private static class AutoCompleteRowFilter extends Filter {
        private ArrayList<IPickable> options;
        private ArrayList<IPickable> filteredOptions;
        private OptionDialogAdapter optionDialogAdapter;

        public AutoCompleteRowFilter(OptionDialogAdapter optionDialogAdapter, ArrayList<IPickable> options) {
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
                for(IPickable option : options) {
                    if(option.toString().toLowerCase().trim().contains(filterString)) {
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

            optionDialogAdapter.setOptions((ArrayList<IPickable>) results.values);
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
    public static class OnTextViewClick implements View.OnClickListener {
        private final TextView textView;
        private final OnOptionSelectedListener onOptionItemSelectedListener;
        private final Dialog parentDialog;

        public OnTextViewClick(Dialog parentDialog, TextView textView, OnOptionSelectedListener onOptionItemSelectedListener) {
            this.parentDialog = parentDialog;
            this.textView = textView;
            this.onOptionItemSelectedListener = onOptionItemSelectedListener;
        }

        @Override
        public void onClick(View v) {
            onOptionItemSelectedListener.onOptionSelected(new DefaultPickable(textView.getText().toString(), ""));
            parentDialog.dismiss();
        }
    }

}