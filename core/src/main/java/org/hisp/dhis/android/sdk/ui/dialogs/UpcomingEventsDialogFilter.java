package org.hisp.dhis.android.sdk.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;

import java.util.ArrayList;
import java.util.List;

public class UpcomingEventsDialogFilter extends AutoCompleteDialogFragment
        implements LoaderManager.LoaderCallbacks<List<AutoCompleteDialogAdapter.OptionAdapterValue>>  {
    public static final int ID = 120000101;
    private static final int LOADER_ID = 2133332121;

    public static UpcomingEventsDialogFilter newInstance(OnOptionSelectedListener optionSelectedListener) {
        UpcomingEventsDialogFilter upcomingEventsDialogFilter = new UpcomingEventsDialogFilter();
        upcomingEventsDialogFilter.setOnOptionSetListener(optionSelectedListener);

        return upcomingEventsDialogFilter;
    }
    @Override
    public Loader<List<AutoCompleteDialogAdapter.OptionAdapterValue>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            return new DbLoader<>(
                    getActivity().getBaseContext(),
                    modelsToTrack,
                    new UpcomingEventsDialogFilterQuery());
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDialogLabel(R.string.choose_filter);
        setDialogId(ID);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }


    @Override
    public void onLoadFinished(Loader<List<AutoCompleteDialogAdapter.OptionAdapterValue>> loader, List<AutoCompleteDialogAdapter.OptionAdapterValue> data) {
        if (loader.getId() == LOADER_ID) {
            getAdapter().swapData(data);

            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<AutoCompleteDialogAdapter.OptionAdapterValue>> loader) {
        getAdapter().swapData(null);
    }

    public enum Type {
        ACTIVE,
        UPCOMING,
        OVERDUE
//        FOLLOW_UP
    }
}
