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

import android.os.Bundle;
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
import org.hisp.dhis.client.sdk.ui.adapters.OnPickerItemClickListener;
import org.hisp.dhis.client.sdk.ui.adapters.PickerItemAdapter;
import org.hisp.dhis.client.sdk.ui.models.Picker;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.DividerDecoration;


public class FilterableDialogFragment extends AppCompatDialogFragment {
    // for fragment manager
    public static final String TAG = FilterableDialogFragment.class.getSimpleName();

    // for arguments bundle
    public static final String ARGS_PICKER = "args:picker";

    private OnPickerItemClickDelegate onPickerItemClickDelegate;

    public static FilterableDialogFragment newInstance(Picker picker) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGS_PICKER, picker);

        FilterableDialogFragment fragment = new FilterableDialogFragment();
        fragment.setArguments(arguments);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);

        return fragment;
    }

    public FilterableDialogFragment() {
        // explicit empty constructor
        onPickerItemClickDelegate = new OnPickerItemClickDelegate();
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

        final PickerItemAdapter itemAdapter = new PickerItemAdapter(getActivity(), picker);
        itemAdapter.setOnPickerItemClickListener(onPickerItemClickDelegate);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider)));

        EditText filterEditText = (EditText) view
                .findViewById(R.id.edittext_filter_picker_items);
        filterEditText.addTextChangedListener(new AbsTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                itemAdapter.filter(editable.toString());
            }
        });
    }

    public void setOnPickerItemClickListener(OnPickerItemClickListener clickListener) {
        onPickerItemClickDelegate.setOnPickerItemClickListener(clickListener);
    }

    private class OnPickerItemClickDelegate implements OnPickerItemClickListener {
        private OnPickerItemClickListener onPickerItemClickListener;

        @Override
        public void onPickerItemClickListener(Picker selectedPicker) {
            if (onPickerItemClickListener != null) {
                onPickerItemClickListener.onPickerItemClickListener(selectedPicker);
            }

            dismiss();
        }

        public void setOnPickerItemClickListener(OnPickerItemClickListener onItemClickListener) {
            this.onPickerItemClickListener = onItemClickListener;
        }
    }

//    public static abstract class AbsPickerLoader {
//        private final Picker picker;
//        private OnPickerLoadedListener onPickersLoadedListener;
//
//        public AbsPickerLoader(Picker picker) {
//            this.picker = picker;
//        }
//
//        public void setOnPickersLoadedListener(OnPickerLoadedListener onPickersLoadedListener) {
//            this.onPickersLoadedListener = onPickersLoadedListener;
//        }
//
//        public OnPickerLoadedListener getOnPickersLoadedListener() {
//            return onPickersLoadedListener;
//        }
//
//        public Picker getPicker() {
//            return picker;
//        }
//
//        public abstract void onLoadPicker();
//    }
//
//
//    private static class PickerItemLoader extends FilterableDialogFragment.AbsPickerLoader {
//        private final OptionSetInteractor optionSetInteractor;
//
//        private PickerItemLoader(Picker picker, OptionSetInteractor optionSetInteractor) {
//            super(picker);
//            this.optionSetInteractor = optionSetInteractor;
//        }
//
//        @Override
//        public void onLoadPicker() {
//            OptionSet optionSet = new OptionSet();
//            optionSet.setUId(getPicker().getId());
//
//            optionSetInteractor.list(optionSet)
//                    .map(new Func1<List<Option>, Picker>() {
//                        @Override
//                        public Picker call(List<Option> options) {
//                            if (options == null || options.isEmpty()) {
//                                return getPicker();
//                            }
//
//                            Picker picker = getPicker();
//
//                            // build options
//                            for (Option option : options) {
//                                picker.addChild(Picker.create(option.getCode(),
//                                        option.getDisplayName(), picker));
//                            }
//
//                            return picker;
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<Picker>() {
//                        @Override
//                        public void call(Picker picker) {
//                            if (getOnPickersLoadedListener() != null) {
//                                getOnPickersLoadedListener().onPickerLoaded(picker);
//                            }
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            // log throwable exception
//                        }
//                    });
//        }
//    }
//
//    public interface OnPickerLoadedListener {
//        void onPickerLoaded(Picker picker);
//    }
}
