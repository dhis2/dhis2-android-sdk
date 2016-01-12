package org.hisp.dhis.client.sdk.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.ChainablePickerState;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.Picker;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.SelectorAdapter;

import java.util.ArrayList;
import java.util.List;

public class PickerFragment extends Fragment {

    private SelectorAdapter mSelectorAdapter;
    private List<Picker> mRootPickerList;
    private RecyclerView mRecyclerView;

    private static final String EXTRA_PICKER_LIST = "extra:mPickerList";
    private static final String PICKER_KEY = "extra:PickerKey";
    public static final String TAG = PickerFragment.class.getSimpleName();


    public PickerFragment() {
    }

    public static PickerFragment newInstance(List<Picker> pickerList) {
        PickerFragment pickerFragment = new PickerFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_PICKER_LIST, (ArrayList) pickerList);

        pickerFragment.setArguments(args);
        return pickerFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//        if(mPickerList.size() < 1) {
////            throw new IllegalArgumentException("Picker fragment requires at least one picker");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChainablePickerState state = null;

        if(savedInstanceState != null) {
            state = savedInstanceState.getParcelable(PICKER_KEY);
        }
        if(mRootPickerList == null) {
            mRootPickerList = new ArrayList<>();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.pickerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mSelectorAdapter = new SelectorAdapter();
        mRecyclerView.setAdapter(null);

        List<Picker> pickers = new ArrayList<>();

        if(state == null) {
            mRootPickerList = getArguments().getParcelableArrayList(EXTRA_PICKER_LIST);

            for(Picker parentPicker : mRootPickerList) {
                parentPicker.setParentList(pickers);
                parentPicker.setParentView(mRecyclerView);
                pickers.add(parentPicker);
            }

            mSelectorAdapter.setPickers(pickers);
        }
        else {
            mRootPickerList = state.getRootNodes();
            for(Picker rootPicker : mRootPickerList) {
                Picker current = rootPicker;
                current.setParentList(pickers);
                current.setParentView(mRecyclerView);
                pickers.add(current);

                while(current.getNextLinkedSibling() != null) {
                    current = current.getNextLinkedSibling();
                    if(current.isAdded()) {
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
        if(mRootPickerList == null) {
            mRootPickerList = new ArrayList<>();
        }
        ChainablePickerState state = new ChainablePickerState(mRootPickerList);
        bundle.putParcelable(PICKER_KEY, state);
        super.onSaveInstanceState(bundle);
    }
}
