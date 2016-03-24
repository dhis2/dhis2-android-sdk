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

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.ChainablePickerState;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.Picker;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.SelectorAdapter;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.SelectorViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PickerFragment extends Fragment {

    public static final String TAG = PickerFragment.class.getSimpleName();
    private static final String EXTRA_PICKER_LIST = "extra:mPickerList";
    private static final String PICKER_KEY = "extra:PickerKey";
    private SelectorAdapter mSelectorAdapter;
    private List<Picker> mRootPickerList;
    private RecyclerView mRecyclerView;


    public PickerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChainablePickerState state = null;

        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(PICKER_KEY);
        }
        if (mRootPickerList == null) {
            mRootPickerList = new ArrayList<>();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.pickerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mSelectorAdapter = new SelectorAdapter();
        mRecyclerView.setAdapter(null);

        List<Picker> pickers = new ArrayList<>();

        if (state == null) {

            for (Picker parentPicker : mRootPickerList) {
                parentPicker.setParentList(pickers);
                parentPicker.setParentView(mRecyclerView);
                pickers.add(parentPicker);
            }

            mSelectorAdapter.setPickers(pickers);
        } else {
            mRootPickerList = state.getRootNodes();
            for (Picker rootPicker : mRootPickerList) {
                Picker current = rootPicker;
                current.setParentList(pickers);
                current.setParentView(mRecyclerView);
                pickers.add(current);

                while (current.getNextLinkedSibling() != null) {
                    current = current.getNextLinkedSibling();
                    if (current.isAdded()) {
                        current.setParentList(pickers);
                        current.setParentView(mRecyclerView);
                        pickers.add(current);
                    }
                }
            }
            mSelectorAdapter.setPickers(pickers);
        }

        mRecyclerView.setAdapter(mSelectorAdapter);
    }

    public SelectorAdapter getSelectorAdapter() {
        return mSelectorAdapter;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (mRootPickerList == null) {
            mRootPickerList = new ArrayList<>();
        }
        ChainablePickerState state = new ChainablePickerState(mRootPickerList);
        bundle.putParcelable(PICKER_KEY, state);
        super.onSaveInstanceState(bundle);
    }

    public void setRootPickerList(List<Picker> mRootPickerList) {
        this.mRootPickerList = mRootPickerList;
    }

    /**
     * Used to clear focus of autocompletetextviews when touching outside them
     *
     * @param event
     */
    public void dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (SelectorViewHolder selectorViewHolder : mSelectorAdapter.getSelectorViewHolders
                    ()) {
                if (selectorViewHolder != null && selectorViewHolder.getAutoCompleteTextView() !=
                        null) {
                    AutoCompleteTextView autoCompleteTextView = selectorViewHolder
                            .getAutoCompleteTextView();
                    if (autoCompleteTextView.hasFocus()) {
                        Rect outRect = new Rect();
                        autoCompleteTextView.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            autoCompleteTextView.clearFocus();
                        }
                    }
                }
            }
        }
    }
}
