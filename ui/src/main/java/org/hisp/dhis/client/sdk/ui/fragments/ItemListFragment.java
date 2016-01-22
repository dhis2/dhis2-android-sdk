package org.hisp.dhis.client.sdk.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.itemlistrowview.EItemListRowStatus;
import org.hisp.dhis.client.sdk.ui.views.itemlistrowview.ItemListRow;
import org.hisp.dhis.client.sdk.ui.views.itemlistrowview.ItemListRowAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemListFragment extends Fragment {
    public static final String TAG = ItemListFragment.class.getSimpleName();
    private static final String EXTRA_ORGANISATION_UNIT = "extra:OrganisationUnit";
    private static final String EXTRA_PROGRAM = "extra:Program";

    private String mOrganisationUnitId;
    private String mProgramId;

    private RecyclerView mRecyclerView;
    private TextView mEmptyItemsTextView;

    public ItemListFragment() {

    }

    public static ItemListFragment newInstance() {
        ItemListFragment itemListCardViewFragment= new ItemListFragment();


        return itemListCardViewFragment;
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

        //load data into memory
        List<Pair<String,Integer>> valuesPos = new ArrayList<>();
        valuesPos.add(new Pair<String, Integer>("Erling", 1));
        valuesPos.add(new Pair<String, Integer>("Fjelstad", 2));
        valuesPos.add(new Pair<String, Integer>("Mann", 3));
        ItemListRow itemCardRow1 = new ItemListRow(null, valuesPos, EItemListRowStatus.OFFLINE.toString());

        List<Pair<String,Integer>> valuesPos1 = new ArrayList<>();
        valuesPos1.add(new Pair<String, Integer>("Simen", 1));
        valuesPos1.add(new Pair<String, Integer>("R", 1));
        valuesPos1.add(new Pair<String, Integer>("Russnes", 2));
        valuesPos1.add(new Pair<String, Integer>("Mann", 3));
        ItemListRow itemCardRow2 = new ItemListRow(null, valuesPos1, EItemListRowStatus.SENT.toString());
        List<Pair<String,Integer>> valuesPos2 = new ArrayList<>();
        valuesPos2.add(new Pair<String, Integer>("Araz", 1));
        valuesPos2.add(new Pair<String, Integer>("AB", 1));
        valuesPos2.add(new Pair<String, Integer>("Abishov", 2));
        valuesPos2.add(new Pair<String, Integer>("Man", 3));
        ItemListRow itemCardRow3 = new ItemListRow(null, valuesPos2, EItemListRowStatus.ERROR.toString());
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
            }
        };
        View.OnClickListener onStatusClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onStatusClick");
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick");
                return true;
            }
        };
        itemCardRow1.setOnRowClickListener(onClickListener);
        itemCardRow1.setOnStatusClickListener(onStatusClickListener);
        itemCardRow1.setOnLongClickListener(onLongClickListener);
        itemCardRow2.setOnRowClickListener(onClickListener);
        itemCardRow2.setOnStatusClickListener(onStatusClickListener);
        itemCardRow2.setOnLongClickListener(onLongClickListener);
        itemCardRow3.setOnRowClickListener(onClickListener);
        itemCardRow3.setOnStatusClickListener(onStatusClickListener);
        itemCardRow3.setOnLongClickListener(onLongClickListener);
        List<ItemListRow> itemCardRowList = new ArrayList<>();
        itemCardRowList.add(itemCardRow1);
        itemCardRowList.add(itemCardRow2);
        itemCardRowList.add(itemCardRow3);

        //init adapter
        ItemListRowAdapter adapter = new ItemListRowAdapter(itemCardRowList);

        mRecyclerView.setAdapter(adapter);
    }
}
