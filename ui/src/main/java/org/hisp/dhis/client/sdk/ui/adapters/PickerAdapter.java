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

package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.FilterableDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.Picker;

import java.util.ArrayList;
import java.util.List;


// TODO show animation when amount of pickers change
// TODO provide callbacks to client code (notify clients about changes on each selection)
// TODO show selection chain in picker list
public class PickerAdapter extends RecyclerView.Adapter {
    private static final String PICKER_ADAPTER_STATE = "state:pickerAdapter";

    private final FragmentManager fragmentManager;
    private final LayoutInflater layoutInflater;
    private final List<Picker> pickers;

    private OnPickerListChangeListener onPickerListChangeListener;
    private Picker pickerTree;

    public PickerAdapter(FragmentManager fragmentManager, Context context) {
        this.fragmentManager = fragmentManager;
        this.layoutInflater = LayoutInflater.from(context);
        this.pickers = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PickerViewHolder(layoutInflater.inflate(
                R.layout.recyclerview_row_picker, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PickerViewHolder) holder).update(pickers.get(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = pickers.size();

        if (itemCount > 0) {
            Picker lastPicker = pickers.get(itemCount - 1);

            // if last picker does not gave any items, we don't want
            // to show it as picker in the list, but use it as value for parent
            if (lastPicker.getChildren().isEmpty()) {
                itemCount = itemCount - 1;
            }
        }

        return itemCount;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && pickerTree != null) {
            outState.putSerializable(PICKER_ADAPTER_STATE, pickerTree);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Picker pickerTree = (Picker) savedInstanceState
                    .getSerializable(PICKER_ADAPTER_STATE);
            swapData(pickerTree);
        }
    }

    public void setOnPickerListChangeListener(OnPickerListChangeListener listener) {
        this.onPickerListChangeListener = listener;
    }

    public void swapData(Picker pickerTree) {
        this.pickerTree = pickerTree;
        this.pickers.clear();

        if (pickerTree != null) {

            // flattening the picker tree into list
            Picker node = getRootNode(pickerTree);
            do {
                // we don't want to add leaf nodes to list
                if (!node.isLeaf()) {
                    if (node.areChildrenRoots()) {
                        for (Picker childNode : node.getChildren()) {
                            pickers.add(childNode);
                        }
                    } else {
                        pickers.add(node);
                    }
                }
            } while ((node = node.getSelectedChild()) != null);
        }

        if (onPickerListChangeListener != null) {
            onPickerListChangeListener.onPickerListChanged(new ArrayList<>(pickers));
        }

        notifyDataSetChanged();
    }

    public List<Picker> getData() {
        // defensive copy: preventing clients from mutating
        // list of pickers set to adapter
        return new ArrayList<>(pickers);
    }

    private Picker getRootNode(Picker picker) {
        Picker node = picker;

        // walk up the tree
        while (node.getParent() != null) {
            node = node.getParent();
        }

        return node;
    }

    public interface OnPickerListChangeListener {
        void onPickerListChanged(List<Picker> pickers);
    }

    private class OnItemClickedListener implements OnPickerItemClickListener {

        @Override
        public void onPickerItemClickListener(Picker selectedPicker) {
            if (selectedPicker.getParent() != null) {
                selectedPicker.getParent().setSelectedChild(selectedPicker);
            }

            // re-render the tree
            swapData(selectedPicker);
        }
    }

    private class PickerViewHolder extends RecyclerView.ViewHolder {
        private final TextView pickerLabel;
        private final ImageView cancel;

        public PickerViewHolder(View itemView) {
            super(itemView);

            pickerLabel = (TextView) itemView.findViewById(R.id.textview_picker);
            cancel = (ImageView) itemView.findViewById(R.id.imageview_cancel);
        }

        public void update(Picker picker) {
            if (picker.getSelectedChild() != null) {
                pickerLabel.setText(picker.getSelectedChild().getName());
            } else {
                pickerLabel.setText(picker.getHint());
            }

            OnClickListener listener = new OnClickListener(picker);
            pickerLabel.setOnClickListener(listener);
            cancel.setOnClickListener(listener);

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
                OnPickerItemClickListener listener =
                        new OnItemClickedListener();
                fragment.setOnPickerItemClickListener(listener);
            }
        }
    }

    private class OnClickListener implements View.OnClickListener {
        private final Picker picker;

        private OnClickListener(Picker picker) {
            this.picker = picker;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.textview_picker) {
                attachFragment();
            } else if (view.getId() == R.id.imageview_cancel) {
                clearSelection();
            }
        }

        private void attachFragment() {
            OnPickerItemClickListener listener =
                    new OnItemClickedListener();
            FilterableDialogFragment dialogFragment =
                    FilterableDialogFragment.newInstance(picker);

            dialogFragment.setOnPickerItemClickListener(listener);
            dialogFragment.show(fragmentManager, FilterableDialogFragment.TAG);
        }

        private void clearSelection() {
            picker.setSelectedChild(null);
            swapData(picker);
        }
    }
}