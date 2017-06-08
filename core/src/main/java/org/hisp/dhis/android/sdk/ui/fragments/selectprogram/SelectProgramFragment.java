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

package org.hisp.dhis.android.sdk.ui.fragments.selectprogram;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.controllers.SyncStrategy;
import org.hisp.dhis.android.sdk.events.OnTeiDownloadedEvent;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.activities.SynchronisationStateHandler;
import org.hisp.dhis.android.sdk.ui.adapters.AbsAdapter;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogFragment;
import org.hisp.dhis.android.sdk.ui.dialogs.OrgUnitDialogFragment;
import org.hisp.dhis.android.sdk.ui.dialogs.ProgramDialogFragment;
import org.hisp.dhis.android.sdk.ui.dialogs.UpcomingEventsDialogFilter;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RefreshListViewEvent;
import org.hisp.dhis.android.sdk.ui.views.CardTextViewButton;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;
import org.hisp.dhis.client.sdk.ui.fragments.BaseFragment;

import java.util.Arrays;

public abstract class SelectProgramFragment extends BaseFragment
        implements View.OnClickListener, AutoCompleteDialogFragment.OnOptionSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<SelectProgramFragmentForm>, SynchronisationStateHandler.OnSynchronisationStateListener {
    public static final String TAG = SelectProgramFragment.class.getSimpleName();
    protected final String STATE;
    protected final int LOADER_ID;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ListView mListView;
    protected ProgressBar mProgressBar;
    protected AbsAdapter mAdapter;

    protected CardTextViewButton mOrgUnitButton;
    protected CardTextViewButton mProgramButton;

    protected SelectProgramFragmentState mState;
    protected SelectProgramFragmentPreferences mPrefs;

    public SelectProgramFragment() {
        this("state:SelectProgramFragment", 1);
    }

    public SelectProgramFragment(String stateName, int loaderId) {
        STATE = stateName;
        LOADER_ID = loaderId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_program, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mPrefs = new SelectProgramFragmentPreferences(
                getActivity().getApplicationContext());

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Green, R.color.Blue, R.color.orange);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = (ListView) view.findViewById(R.id.event_listview);



        mAdapter = getAdapter(savedInstanceState);
        View header = getListViewHeader(savedInstanceState);
        setStandardButtons(header);
        mListView.addHeaderView(header, TAG, false);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        if (savedInstanceState != null &&
                savedInstanceState.getParcelable(STATE) != null) {
            mState = savedInstanceState.getParcelable(STATE);
        }

        if (mState == null) {
            // restoring last selection of program
            Pair<String, String> orgUnit = mPrefs.getOrgUnit();
            Pair<String, String> program = mPrefs.getProgram();
            Pair<String, String> filter = mPrefs.getFilter();
            mState = new SelectProgramFragmentState();
            if (orgUnit != null) {
                mState.setOrgUnit(orgUnit.first, orgUnit.second);
                if (program != null) {
                    mState.setProgram(program.first, program.second);
                }
                if(filter != null) {
                    mState.setFilter(filter.first, filter.second);
                }
                else {
                    mState.setFilter("0", Arrays.asList(UpcomingEventsDialogFilter.Type.values()).get(0).toString());
                }


            }
        }

        onRestoreState(true);
    }

    @Override
    public abstract void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);

    @Override
    public abstract boolean onContextItemSelected(MenuItem item);

    protected abstract AbsAdapter getAdapter(Bundle savedInstanceState);

    protected View getListViewHeader(Bundle savedInstanceState) {
        View header = getLayoutInflater(savedInstanceState).inflate(
                R.layout.fragment_select_program_header, mListView, false
        );
        return header;
    }

    protected void setStandardButtons(View header) {
        mProgressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mOrgUnitButton = (CardTextViewButton) header.findViewById(R.id.select_organisation_unit);
        mProgramButton = (CardTextViewButton) header.findViewById(R.id.select_program);

        mOrgUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrgUnitDialogFragment fragment = OrgUnitDialogFragment
                        .newInstance(SelectProgramFragment.this,
                                getProgramTypes());
                fragment.show(getChildFragmentManager());
            }
        });
        mProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgramDialogFragment fragment = ProgramDialogFragment
                        .newInstance(SelectProgramFragment.this, mState.getOrgUnitId(),
                                getProgramTypes());
                fragment.show(getChildFragmentManager());
            }
        });

        mOrgUnitButton.setEnabled(true);
        mProgramButton.setEnabled(false);
    }

    protected abstract ProgramType[] getProgramTypes();

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
        SynchronisationStateHandler.getInstance().removeListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        SynchronisationStateHandler.getInstance().setListener(this);
        setRefreshing(SynchronisationStateHandler.getInstance().getState());
        Dhis2Application.getEventBus().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(STATE, mState);
        super.onSaveInstanceState(out);
    }

    @Override
    public void onOptionSelected(int dialogId, int position, String id, String name) {
        switch (dialogId) {
            case OrgUnitDialogFragment.ID: {
                onUnitSelected(id, name);
                break;
            }
            case ProgramDialogFragment.ID: {
                onProgramSelected(id, name);
                break;
            }
        }
    }

    @Override
    public abstract Loader<SelectProgramFragmentForm> onCreateLoader(int id, Bundle args);

    @Override
    public void onLoaderReset(Loader<SelectProgramFragmentForm> loader) {
        mAdapter.swapData(null);
    }

    public void onRefreshFinished() {
        setRefreshing(false);
    }

    @Override
    public void onLoadFinished(Loader<SelectProgramFragmentForm> loader, SelectProgramFragmentForm data) {
        if (LOADER_ID == loader.getId()) {
            mProgressBar.setVisibility(View.GONE);
            mAdapter.swapData(data.getEventRowList());
            setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (isAdded()) {
            Context context = getActivity().getBaseContext();
            Toast.makeText(context, getString(R.string.syncing), Toast.LENGTH_SHORT).show();
            DhisService.synchronize(context, SyncStrategy.DOWNLOAD_ALL);
        }
    }

    protected void setRefreshing(final boolean refreshing) {
        /* workaround for bug in android support v4 library */
        if (mSwipeRefreshLayout.isRefreshing() != refreshing) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    public void onRestoreState(boolean hasUnits) {
        mOrgUnitButton.setEnabled(hasUnits);
        if (!hasUnits) {
            return;
        }

        SelectProgramFragmentState backedUpState = new SelectProgramFragmentState(mState);
        if (!backedUpState.isOrgUnitEmpty()) {
            onUnitSelected(
                    backedUpState.getOrgUnitId(),
                    backedUpState.getOrgUnitLabel()
            );

            if (!backedUpState.isProgramEmpty()) {
                onProgramSelected(
                        backedUpState.getProgramId(),
                        backedUpState.getProgramName()
                );
            }
        }

    }

    public void onUnitSelected(String orgUnitId, String orgUnitLabel) {
        mOrgUnitButton.setText(orgUnitLabel);
        mProgramButton.setEnabled(true);

        mState.setOrgUnit(orgUnitId, orgUnitLabel);
        mState.resetProgram();

        mPrefs.putOrgUnit(new Pair<>(orgUnitId, orgUnitLabel));
        mPrefs.putProgram(null);

        handleViews(0);
    }

    public void onProgramSelected(String programId, String programName) {
        mProgramButton.setText(programName);

        mState.setProgram(programId, programName);
        mPrefs.putProgram(new Pair<>(programId, programName));
        handleViews(1);

        mProgressBar.setVisibility(View.VISIBLE);
        // this call will trigger onCreateLoader method
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
    }


    @Subscribe /* it doesn't seem that this subscribe works. Inheriting class will have to */
    public void onReceivedUiEvent(UiEvent uiEvent) {
        if(uiEvent.getEventType().equals(UiEvent.UiEventType.SYNCING_START)) {
            setRefreshing(true);
        } else if(uiEvent.getEventType().equals(UiEvent.UiEventType.SYNCING_END)) {
            setRefreshing(false);
        }
    }

    @Override
    public void stateChanged() {
        // stub - will listen to updates in onResume()
    }

    protected abstract void handleViews(int level);
}