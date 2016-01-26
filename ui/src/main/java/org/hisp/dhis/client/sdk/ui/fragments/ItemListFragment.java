package org.hisp.dhis.client.sdk.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;

public class ItemListFragment extends Fragment {
    public static final String TAG = ItemListFragment.class.getSimpleName();
    private static final String EXTRA_ORGANISATION_UNIT = "extra:OrganisationUnit";
    private static final String EXTRA_PROGRAM = "extra:Program";

    private String mOrganisationUnitId;
    private String mProgramId;

    public RecyclerView mRecyclerView;
    private TextView mEmptyItemsTextView;

    public ItemListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_itemlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.itemListCardViewRecyclerView);
        mEmptyItemsTextView = (TextView) view.findViewById(R.id.tvNoItems);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }
}
