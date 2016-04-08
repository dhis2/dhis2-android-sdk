/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.Pickable;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteDialogFragment extends AppCompatDialogFragment {

    private static final String TAG = AutoCompleteDialogFragment.class.getSimpleName();
    private static final String ARGS_OPTIONS = "extra:Options";
    private static final String ARGS_TITLE = "extra:Title";
    private ArrayList<Pickable> options;
    private String title;
    private TextInputLayout textInputLayout;
    private EditText editText;
    private RecyclerView recyclerView;
    private OnOptionSelectedListener onOptionSelectedListener;
    private OptionDialogAdapter optionDialogAdapter;

    public AutoCompleteDialogFragment() {
        //empty constructor
    }

    public static AutoCompleteDialogFragment newInstance(String title, List<Pickable> options,
                                                         OnOptionSelectedListener
                                                                 onOptionSelectedListener) {
        AutoCompleteDialogFragment autoCompleteDialogFragment = new AutoCompleteDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGS_OPTIONS, (ArrayList) options);
        args.putString(ARGS_TITLE, title);
        autoCompleteDialogFragment.setArguments(args);
        autoCompleteDialogFragment.setOnOptionSelectedListener(onOptionSelectedListener);
        return autoCompleteDialogFragment;
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

        textInputLayout = (TextInputLayout) appCompatDialog.findViewById(R.id
                .dialog_autocomplete_textinputlayout);
        editText = (EditText) appCompatDialog.findViewById(R.id.dialog_autocomplete_edittext);

        recyclerView = (RecyclerView) appCompatDialog.findViewById(R.id
                .dialog_autocomplete_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        OptionDialogAdapter optionDialogAdapter = new OptionDialogAdapter(appCompatDialog,
                options, onOptionSelectedListener);


        recyclerView.setAdapter(optionDialogAdapter);

        OptionDialogFilterTextWatcher optionDialogFilterTextWatcher = new
                OptionDialogFilterTextWatcher();
        optionDialogFilterTextWatcher.setOptionDialogAdapter(optionDialogAdapter);
        editText.addTextChangedListener(optionDialogFilterTextWatcher);

        super.setupDialog(appCompatDialog, style);

    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener onOptionSelectedListener) {
        this.onOptionSelectedListener = onOptionSelectedListener;
        if (optionDialogAdapter != null) {
            optionDialogAdapter.setOnOptionSelectedListener(onOptionSelectedListener);
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    public OptionDialogAdapter getOptionDialogAdapter() {
        return optionDialogAdapter;
    }

    public void setOptions(ArrayList<Pickable> options) {
        this.options = options;
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(Pickable pickable);
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
        private ArrayList<Pickable> options;
        private OnOptionSelectedListener onOptionSelectedListener;
        private Dialog dialog;
        private OptionDialogViewHolder mOptionDialogViewHolder;

        public OptionDialogAdapter(Dialog dialog, ArrayList<Pickable> options,
                                   OnOptionSelectedListener onOptionSelectedListener) {
            this.dialog = dialog;
            this.options = options;
            this.onOptionSelectedListener = onOptionSelectedListener;
        }

        public void setParentDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mOptionDialogViewHolder = new OptionDialogViewHolder(LayoutInflater.from(parent
                    .getContext())
                    .inflate(R.layout.recyclerview_row_autocomplete_dialog, parent, false),
                    dialog, onOptionSelectedListener);
            return mOptionDialogViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OptionDialogViewHolder optionDialogViewHolder = (OptionDialogViewHolder) holder;
            optionDialogViewHolder.optionValueTextView.setText(options.get(position).toString());
            optionDialogViewHolder.optionValueTextView.setTag(options.get(position));
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        @Override
        public Filter getFilter() {
            return new AutoCompleteRowFilter(this, options);
        }

        public void setOptions(ArrayList<Pickable> filteredOptions) {
            this.options = filteredOptions;
        }

        public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
            this.onOptionSelectedListener = listener;
            if (mOptionDialogViewHolder != null) {
                mOptionDialogViewHolder.setOnOptionSelectedListener(listener);
            }

        }
    }

    private static class AutoCompleteRowFilter extends Filter {
        private ArrayList<Pickable> options;
        private ArrayList<Pickable> filteredOptions;
        private OptionDialogAdapter optionDialogAdapter;

        public AutoCompleteRowFilter(OptionDialogAdapter optionDialogAdapter,
                                     ArrayList<Pickable> options) {
            this.optionDialogAdapter = optionDialogAdapter;
            this.options = new ArrayList<>(options);
            this.filteredOptions = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredOptions.clear();
            final FilterResults filterResults = new FilterResults();

            if (constraint.length() == 0) {
                filteredOptions.addAll(options);
            } else {
                final String filterString = constraint.toString().toLowerCase().trim();
                for (Pickable option : options) {
                    if (option.toString().toLowerCase().trim().contains(filterString)) {
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

            optionDialogAdapter.setOptions((ArrayList<Pickable>) results.values);
            optionDialogAdapter.notifyDataSetChanged();
        }
    }

    public static class OptionDialogViewHolder extends RecyclerView.ViewHolder {
        public final TextView optionValueTextView;
        public final OnTextViewClick onTextViewClick;

        public OptionDialogViewHolder(View itemView, Dialog parentDialog,
                                      OnOptionSelectedListener onOptionSelectedListener) {
            super(itemView);
            optionValueTextView = (TextView) itemView.findViewById(
                    R.id.autocomplete_dialog_row_label);

            onTextViewClick = new OnTextViewClick(parentDialog, optionValueTextView,
                    onOptionSelectedListener);
            optionValueTextView.setOnClickListener(onTextViewClick);

        }

        public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
            onTextViewClick.setOnOptionItemSelectedListener(listener);
        }
    }

    public static class OnTextViewClick implements View.OnClickListener {
        private final TextView textView;
        private final Dialog parentDialog;
        private OnOptionSelectedListener onOptionItemSelectedListener;

        public OnTextViewClick(Dialog parentDialog, TextView textView, OnOptionSelectedListener
                onOptionItemSelectedListener) {
            this.parentDialog = parentDialog;
            this.textView = textView;
            this.onOptionItemSelectedListener = onOptionItemSelectedListener;
        }

        public void setOnOptionItemSelectedListener(OnOptionSelectedListener listener) {
            this.onOptionItemSelectedListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (onOptionItemSelectedListener != null) {
                onOptionItemSelectedListener.onOptionSelected((Pickable) textView.getTag());
            }
            parentDialog.dismiss();
        }
    }

}