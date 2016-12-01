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

package org.hisp.dhis.client.sdk.ui.rows;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.adapters.OnPickerItemClickListener;
import org.hisp.dhis.client.sdk.ui.fragments.FilterableDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;
import org.hisp.dhis.client.sdk.ui.utils.FormUtils;
import org.hisp.dhis.client.sdk.ui.views.QuickSelectionContainer;

import static android.view.View.GONE;
import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FilterableRowView implements RowView {
    private final FragmentManager fragmentManager;

    public FilterableRowView(FragmentManager fragmentManager) {
        this.fragmentManager = isNull(fragmentManager, "fragmentManager must not be null");
    }

    public RecyclerView.ViewHolder onCreateViewHolder(View view) {
        return new FilterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new FilterViewHolder(inflater.inflate(
                R.layout.recyclerview_row_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityFilter formEntityFilter = (FormEntityFilter) formEntity;
        ((FilterViewHolder) viewHolder).update(formEntityFilter);
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {
        private static final String EMPTY_STRING = "";

        private final TextView textViewLabel;
        private final EditText filterEditText;
        private final ImageButton buttonDropDown;
        private final ImageButton clearButton;

        private final OnClickListener onClickListener;
        private final OnItemClickListener onItemClickListener;
        private final QuickSelectionContainer quickSelectionContainer;

        public FilterViewHolder(View itemView) {
            super(itemView);

            textViewLabel = (TextView) itemView
                    .findViewById(R.id.textview_row_label);
            filterEditText = (EditText) itemView
                    .findViewById(R.id.recyclerview_row_filter_edittext);
            buttonDropDown = (ImageButton) itemView
                    .findViewById(R.id.button_dropdown);
            clearButton = (ImageButton) itemView
                    .findViewById(R.id.button_clear);
            quickSelectionContainer = (QuickSelectionContainer) itemView
                    .findViewById(R.id.recyclerview_row_filter_quick_selection_container);

            onClickListener = new OnClickListener();
            onItemClickListener = new OnItemClickListener(filterEditText);

            filterEditText.setOnClickListener(onClickListener);
            clearButton.setOnClickListener(onClickListener);
            buttonDropDown.setOnClickListener(onClickListener);
        }

        public void update(FormEntityFilter formEntityFilter) {
            onClickListener.setFormEntityFilter(formEntityFilter);
            onItemClickListener.setFormEntityFilter(formEntityFilter);

            textViewLabel.setText(FormUtils.getFormEntityLabel(formEntityFilter));

            // set the value to edittext
            String filterEditTextValue = EMPTY_STRING;

            Picker picker = formEntityFilter.getPicker();
            if (picker != null && picker.getSelectedChild() != null) {
                filterEditTextValue = picker.getSelectedChild().getName();
            }

            if (picker != null && picker.getChildren().size() < 6) {
                filterEditText.setVisibility(GONE);
                quickSelectionContainer.setFormEntityFilter(formEntityFilter);
            } else {
                filterEditText.setVisibility(View.VISIBLE);
                quickSelectionContainer.hide();
            }

            filterEditText.setText(filterEditTextValue);

            if (formEntityFilter.isLocked()) {
                filterEditText.setEnabled(false);
                clearButton.setEnabled(false);
                clearButton.setClickable(false);
                buttonDropDown.setEnabled(false);
                buttonDropDown.setClickable(false);
            } else {
                filterEditText.setEnabled(true);
                clearButton.setEnabled(true);
                clearButton.setClickable(true);
                buttonDropDown.setEnabled(true);
                buttonDropDown.setClickable(true);
            }

            // after configuration change, callback
            // in dialog fragment can be lost
            attachListenerToExistingFragment(picker);
        }

        private void attachListenerToExistingFragment(Picker picker) {
            FilterableDialogFragment fragment = (FilterableDialogFragment)
                    fragmentManager.findFragmentByTag(FilterableDialogFragment.TAG);

            // if we don't have fragment attached to activity,
            // we don't want to do anything else
            if (fragment == null) {
                return;
            }

            // get the arguments bundle out from fragment
            Bundle arguments = fragment.getArguments();

            // if we don't have picker set to fragment, we can't distinguish
            // the fragment which we need to update
            if (arguments == null || !arguments
                    .containsKey(FilterableDialogFragment.ARGS_PICKER)) {
                return;
            }

            Picker existingPicker = (Picker) arguments
                    .getSerializable(FilterableDialogFragment.ARGS_PICKER);
            if (picker.equals(existingPicker)) {
                fragment.setOnPickerItemClickListener(onItemClickListener);
            }
        }

        private class OnClickListener implements View.OnClickListener {
            private FormEntityFilter formEntityFilter;

            public void setFormEntityFilter(FormEntityFilter formEntityFilter) {
                this.formEntityFilter = formEntityFilter;
            }

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.recyclerview_row_filter_edittext) {
                    showFilterableDialogFragment();
                } else if (view.getId() == R.id.button_dropdown) {
                    showFilterableDialogFragment();
                } else if (view.getId() == R.id.button_clear) {
                    filterEditText.setText(EMPTY_STRING);

                    // we also have to clear out selected child in picker
                    if (formEntityFilter != null && formEntityFilter.getPicker() != null) {
                        formEntityFilter.getPicker().setSelectedChild(null);

                        // using this hack in order to trigger listener in formEntityFilter
                        formEntityFilter.setPicker(formEntityFilter.getPicker());
                    }

                    quickSelectionContainer.refresh();
                }
            }

            private void showFilterableDialogFragment() {
                if (formEntityFilter != null && formEntityFilter.getPicker() != null) {
                    FilterableDialogFragment dialogFragment = FilterableDialogFragment
                            .newInstance(formEntityFilter.getPicker());
                    dialogFragment.setOnPickerItemClickListener(onItemClickListener);
                    dialogFragment.show(fragmentManager, FilterableDialogFragment.TAG);
                }
            }
        }

        private class OnItemClickListener implements OnPickerItemClickListener {
            private final EditText formEditText;
            private FormEntityFilter formEntityFilter;

            public OnItemClickListener(EditText formEditText) {
                this.formEditText = formEditText;
            }

            public void setFormEntityFilter(FormEntityFilter formEntityFilter) {
                this.formEntityFilter = formEntityFilter;
            }

            @Override
            public void onPickerItemClickListener(Picker selectedPicker) {
                if (selectedPicker.getParent() != null) {
                    Picker parentPicker = selectedPicker.getParent();
                    parentPicker.setSelectedChild(selectedPicker);

                    if (formEntityFilter != null) {
                        formEntityFilter.setPicker(parentPicker);
                    }
                }

                // setting value to edittext
                if (formEditText.getVisibility() != GONE) {
                    formEditText.setText(selectedPicker.getName());
                } else {
                    quickSelectionContainer.refresh();
                }
            }
        }
    }
}
