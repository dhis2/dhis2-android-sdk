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

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogAdapter.OptionAdapterValue;
import org.hisp.dhis.android.sdk.ui.views.FontTextView;

public class AutoCompleteDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener {
    private static final String TAG = AutoCompleteDialogFragment.class.getSimpleName();

    private EditText mFilter;
    private TextView mDialogLabel;
    private AutoCompleteDialogAdapter mAdapter;
    private OnOptionSelectedListener mListener;
    private int mDialogId;
    public ProgressBar mProgressBar;
    private FontTextView mFontTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return inflater.inflate(R.layout.dialog_fragment_auto_complete, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView mListView = (ListView) view
                .findViewById(R.id.simple_listview);
        ImageView closeDialogButton = (ImageView) view
                .findViewById(R.id.close_dialog_button);
        mFilter = (EditText) view
                .findViewById(R.id.filter_options);
        mDialogLabel = (TextView) view
                .findViewById(R.id.dialog_label);
        mProgressBar = (ProgressBar) view
                .findViewById(R.id.auto_complete_dialog_progress_bar);
        mFontTextView = (FontTextView) view
                .findViewById(R.id.no_items_textview);
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFilter.getWindowToken(), 0);
        mProgressBar.setVisibility(View.GONE);

        mAdapter = new AutoCompleteDialogAdapter(LayoutInflater.from(getActivity()));
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

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
            OptionAdapterValue value = mAdapter.getItem(position);
            if (value != null) {
                mListener.onOptionSelected(mDialogId, position, value.id, value.label);
            }
        }

        dismiss();
    }

    @Override
    public void dismiss() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFilter.getWindowToken(), 0);
        super.dismiss();
    }

    /* This method must be called only after onViewCreated() */
    public void setDialogLabel(int resourceId) {
        if (mDialogLabel != null) {
            mDialogLabel.setText(resourceId);
        }
    }

    /* This method must be called only after onViewCreated() */
    public void setDialogLabel(CharSequence sequence) {
        if (mDialogLabel != null) {
            mDialogLabel.setText(sequence);
        }
    }

    public void setDialogId(int dialogId) {
        mDialogId = dialogId;
    }

    public int getDialogId() {
        return mDialogId;
    }

    /* This method must be called only after onViewCreated() */
    public CharSequence getDialogLabel() {
        if (mDialogLabel != null) {
            return mDialogLabel.getText();
        } else {
            return null;
        }
    }

    public AutoCompleteDialogAdapter getAdapter() {
        return mAdapter;
    }

    public void setOnOptionSetListener(OnOptionSelectedListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(int dialogId, int position, String id, String name);
    }

    public void setTextToNoItemsTextView(String text) {
        mFontTextView.setText(text);
    }

    public void setNoItemsTextViewVisibility(int visible) {
        switch (visible) {
            case View.VISIBLE : {
                mFontTextView.setVisibility(visible);
                break;
            }
            case View.INVISIBLE : {
                mFontTextView.setVisibility(visible);
                break;
            }
            case View.GONE: {
                mFontTextView.setVisibility(visible);
                break;
            }
            default: break;
        }


    }
}
