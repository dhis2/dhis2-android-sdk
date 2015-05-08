/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis2.android.sdk.utils.ui.adapters.rows.dataentry;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.utils.ui.adapters.rows.AbsTextWatcher;

import java.util.ArrayList;
import java.util.List;

public class OptionDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = OptionDialogFragment.class.getSimpleName();

    private static final int LOADER_ID = 1;
    private static final String EXTRA_OPTIONS = "extra:options";

    private EditText mFilter;
    private OptionDialogAdapter mAdapter;
    private OnOptionSelectedListener mListener;

    public static OptionDialogFragment newInstance(ArrayList<String> options,
                                                   OnOptionSelectedListener listener) {
        OptionDialogFragment dialogFragment = new OptionDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_OPTIONS, options);
        dialogFragment.setArguments(args);
        dialogFragment.setOnOptionSetListener(listener);
        return dialogFragment;
    }

    private List<String> getOptions() {
        return getArguments().getStringArrayList(EXTRA_OPTIONS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_options, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView) view
                .findViewById(R.id.simple_listview);
        ImageView closeDialogButton = (ImageView) view
                .findViewById(R.id.close_dialog_button);
        mFilter = (EditText) view
                .findViewById(R.id.filter_options);

        mAdapter = new OptionDialogAdapter(LayoutInflater.from(getActivity()));
        mAdapter.swapData(getOptions());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        mFilter.addTextChangedListener(new AbsTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s.toString());
            }
        });

        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mListener.onOptionSelected(position, mAdapter.getItem(position));
        }

        dismiss();
    }

    private void setOnOptionSetListener(OnOptionSelectedListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(int position, String name);
    }
}
