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

package org.hisp.dhis.client.sdk.ui.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.Picker;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FilterableDialogFragment extends AppCompatDialogFragment {
    // for fragment manager
    public static final String TAG = FilterableDialogFragment.class.getSimpleName();

    // for arguments bundle
    public static final String ARGS_PICKER = "args:picker";

    private OnPickerItemClickListener onPickerItemClickListener;

    public static FilterableDialogFragment newInstance(Picker picker) {
        Bundle arguments = new Bundle();
        // arguments.putParcelable(ARGS_PICKER, picker);
        arguments.putSerializable(ARGS_PICKER, picker);

        FilterableDialogFragment fragment = new FilterableDialogFragment();
        fragment.setArguments(arguments);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);

        return fragment;
    }

    public FilterableDialogFragment() {
        // explicit empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filterable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Picker picker = null;
        if (getArguments() != null) {
            picker = (Picker) getArguments().getSerializable(ARGS_PICKER);
        }

        if (picker == null) {
            return;
        }

        TextView textViewTitle = (TextView) view
                .findViewById(R.id.textview_titlebar_title);
        if (picker.getHint() != null) {
            textViewTitle.setText(picker.getHint());
        }

        ImageView cancelButton = (ImageView) view
                .findViewById(R.id.imageview_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view
                .findViewById(R.id.recyclerview_picker_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final PickerItemAdapter itemAdapter = new PickerItemAdapter(picker);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider)));

        EditText filterEditText = (EditText) view
                .findViewById(R.id.edittext_search_picker_items);
        filterEditText.addTextChangedListener(new AbsTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                itemAdapter.filter(editable.toString());
            }
        });
    }

    public void setOnPickerItemClickListener(OnPickerItemClickListener clickListener) {
        onPickerItemClickListener = clickListener;
    }

    public interface OnPickerItemClickListener {
        void onPickerItemClickListener(Picker selectedPicker);
    }

    private class PickerItemAdapter extends RecyclerView.Adapter {
        // view inflater
        private final LayoutInflater inflater;

        // Adapter data
        private final Picker currentPicker;
        private final List<Picker> originalPickers;
        private final List<Picker> filteredPickers;

        public PickerItemAdapter(Picker currentPicker) {
            this.inflater = LayoutInflater.from(getActivity());
            this.currentPicker = isNull(currentPicker, "Picker must not be null");
            this.originalPickers = currentPicker.getChildren();
            this.filteredPickers = new ArrayList<>(currentPicker.getChildren());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PickerItemViewHolder(inflater.inflate(
                    R.layout.recyclerview_picker_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PickerItemViewHolder pickerViewHolder = (PickerItemViewHolder) holder;
            Picker picker = filteredPickers.get(position);

            if (this.currentPicker.getSelectedChild() != null &&
                    picker.equals(this.currentPicker.getSelectedChild())) {
                pickerViewHolder.updateViewHolder(picker, true);
            } else {
                pickerViewHolder.updateViewHolder(picker, false);
            }
        }

        @Override
        public int getItemCount() {
            return filteredPickers.size();
        }

        private void filter(@NonNull String query) {
            filteredPickers.clear();

            query = query.toLowerCase();
            for (Picker picker : originalPickers) {
                if (picker.getName() != null && picker.getName().toLowerCase().contains(query)) {
                    filteredPickers.add(picker);
                }
            }

            notifyDataSetChanged();
        }

        private class PickerItemViewHolder extends RecyclerView.ViewHolder {
            final TextView textViewLabel;
            final OnClickListener onTextViewLabelClickListener;

            public PickerItemViewHolder(View itemView) {
                super(itemView);

                this.textViewLabel = (TextView) itemView;
                this.onTextViewLabelClickListener = new OnClickListener();

                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                // for selected state
                                new int[]{android.R.attr.state_selected},

                                // default color state
                                new int[]{}
                        },
                        new int[]{
                                ContextCompat.getColor(
                                        getActivity(), R.color.color_primary_default),
                                textViewLabel.getCurrentTextColor()
                        });

                this.textViewLabel.setTextColor(colorStateList);
                this.textViewLabel.setOnClickListener(onTextViewLabelClickListener);
            }

            public void updateViewHolder(Picker picker, boolean isSelected) {
                textViewLabel.setSelected(isSelected);
                textViewLabel.setText(picker.getName());
                onTextViewLabelClickListener.setPicker(picker);
            }

            private class OnClickListener implements View.OnClickListener {
                private Picker picker;

                public void setPicker(Picker picker) {
                    this.picker = picker;
                }

                @Override
                public void onClick(View view) {
                    if (onPickerItemClickListener != null) {
                        onPickerItemClickListener.onPickerItemClickListener(picker);
                    }

                    dismiss();
                }
            }
        }
    }
}
