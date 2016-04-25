/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.ui.adapters.ValidationErrorAdapter;

import java.util.ArrayList;

public final class ValidationErrorDialog extends DialogFragment
        implements View.OnClickListener {
    private static final String TAG = ValidationErrorDialog.class.getSimpleName();
    private static final String ERRORS_LIST_EXTRA = "extra:ErrorsList";
    private static final String HEADER_EXTRA = "extra:Header";

    private TextView mHeader;
    private ListView mListView;
    private Button mButton;
    private ValidationErrorAdapter mAdapter;

    public static ValidationErrorDialog newInstance(ArrayList<String> errors) {
        ValidationErrorDialog dialog = new ValidationErrorDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(ERRORS_LIST_EXTRA, errors);
        dialog.setArguments(args);
        return dialog;
    }

    public static ValidationErrorDialog newInstance(String header, ArrayList<String> errors) {
        ValidationErrorDialog dialog = new ValidationErrorDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(ERRORS_LIST_EXTRA, errors);
        args.putString(HEADER_EXTRA, header);
        dialog.setArguments(args);
        return dialog;
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
        return inflater.inflate(
                R.layout.dialog_fragment_validation_errors, container, false
        );
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.simple_listview);
        mHeader = (TextView) view.findViewById(R.id.header);
        mButton = (Button) view.findViewById(R.id.closebutton);
        String header = getArguments().getString(HEADER_EXTRA);
        if(header != null) {
            mHeader.setText(header);
        }

        mAdapter = new ValidationErrorAdapter(
                LayoutInflater.from(getActivity().getBaseContext()));
        mListView.setAdapter(mAdapter);
        mButton.setOnClickListener(this);
        if (getArguments() != null) {
            mAdapter.swapData(getArguments()
                    .getStringArrayList(ERRORS_LIST_EXTRA));
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }
}
