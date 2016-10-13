package org.hisp.dhis.client.sdk.ui.bindings.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.bindings.App;
import org.hisp.dhis.client.sdk.ui.bindings.R;
import org.hisp.dhis.client.sdk.ui.bindings.commons.NavigationHandler;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.fragments.BaseFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.rows.RowViewAdapter;
import org.hisp.dhis.client.sdk.ui.views.DividerDecoration;
import org.hisp.dhis.client.sdk.utils.Logger;

import java.util.List;

public class DefaultProfileFragment extends BaseFragment implements ProfileView {
    private static final String STATE_IS_REFRESHING = "state:isRefreshing";
    private static final String TAG = DefaultProfileFragment.class.getSimpleName();

    private ProfilePresenter profilePresenter;
    private Logger logger;

    // pull-to-refresh:
    private SwipeRefreshLayout swipeRefreshLayout;
    private RowViewAdapter rowViewAdapter;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // injection of logger and profile presenter
        logger = App.from(getActivity().getApplication())
                .getAppComponent().logger();
        profilePresenter = App.from(getActivity().getApplication())
                .getUserComponent().profilePresenter();
        alertDialog = createAlertDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setupToolbar();
        setupSwipeRefreshLayout(view, savedInstanceState);
        setupRecyclerView(view);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_IS_REFRESHING, swipeRefreshLayout.isRefreshing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        profilePresenter.attachView(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        alertDialog.dismiss();
        profilePresenter.detachView();
        super.onPause();
    }

    @Override
    public void showProgressBar() {
        logger.d(TAG, "showProgressBar()");
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        logger.d(TAG, "hideProgressBar()");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showUserAccountForm(List<FormEntity> formEntities) {
        rowViewAdapter.swap(formEntities);
    }

    @Override
    public String getUserAccountFieldLabel(@NonNull @UserAccountFieldId String fieldId) {
        switch (fieldId) {
            case ID_FIRST_NAME:
                return getString(R.string.first_name);
            case ID_SURNAME:
                return getString(R.string.surname);
            case ID_GENDER:
                return getString(R.string.gender);
            case ID_GENDER_MALE:
                return getString(R.string.gender_male);
            case ID_GENDER_FEMALE:
                return getString(R.string.gender_female);
            case ID_GENDER_OTHER:
                return getString(R.string.gender_other);
            case ID_BIRTHDAY:
                return getString(R.string.birthday);
            case ID_INTRODUCTION:
                return getString(R.string.introduction);
            case ID_EDUCATION:
                return getString(R.string.education);
            case ID_EMPLOYER:
                return getString(R.string.employer);
            case ID_INTERESTS:
                return getString(R.string.interests);
            case ID_JOB_TITLE:
                return getString(R.string.job_title);
            case ID_LANGUAGES:
                return getString(R.string.languages);
            case ID_EMAIL:
                return getString(R.string.email);
            case ID_PHONE_NUMBER:
                return getString(R.string.phone_number);
            default:
                throw new IllegalArgumentException("Unsupported prompt");
        }
    }

    private void setupToolbar() {
        if (getParentToolbar() != null) {
            getParentToolbar().inflateMenu(R.menu.menu_profile);
            getParentToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return DefaultProfileFragment.this.onMenuItemClick(item);
                }
            });
        }
    }

    private void setupSwipeRefreshLayout(final View view, final Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.swiperefreshlayout_profile);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.color_primary_default);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profilePresenter.sync();
            }
        });

        if (savedInstanceState != null) {
            // this workaround is necessary because of the message queue
            // implementation in android. If you will try to setRefreshing(true) right away,
            // this call will be placed in UI message queue by SwipeRefreshLayout BEFORE
            // message to hide progress bar which probably is created by layout
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(savedInstanceState
                            .getBoolean(STATE_IS_REFRESHING, false));
                }
            });
        }
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view
                .findViewById(R.id.recyclerview_profile);

        // we want RecyclerView to behave like ListView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Using ItemDecoration in order to implement divider
        DividerDecoration itemDecoration = new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider));

        rowViewAdapter = new RowViewAdapter(getChildFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(rowViewAdapter);
    }

    private boolean onMenuItemClick(MenuItem item) {
        logger.d(TAG, "onMenuItemClick()");

        if (item.getItemId() == R.id.action_refresh) {
            profilePresenter.sync();
            return true;
        } else if (item.getItemId() == R.id.menu_log_out) {
            alertDialog.show();
            return true;
        }

        return false;
    }

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.drawable.ic_warning);
        alertDialogBuilder.setTitle(R.string.warning_logout_header);
        alertDialogBuilder.setMessage(R.string.warning_logout_body);
        alertDialogBuilder.setNegativeButton(R.string.warning_logout_dismiss, null);
        alertDialogBuilder.setPositiveButton(R.string.warning_logout_confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        profilePresenter.logout();

                        Intent logoutIntent = new Intent(getContext(),
                                NavigationHandler.loginActivity());
                        startActivity(logoutIntent);
                        getActivity().finish();
                    }
                }
        );

        return alertDialogBuilder.create();
    }
}
