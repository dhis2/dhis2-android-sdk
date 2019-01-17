package org.hisp.dhis.android.core.trackedentity.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.utils.Utils;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import dagger.Reusable;
import retrofit2.Call;

@SuppressWarnings({"PMD.PreserveStackTrace"})
@Reusable
public final class TrackedEntityInstanceQueryCallFactory {

    private final TrackedEntityInstanceService service;
    private final SearchGridMapper mapper;
    private final APICallExecutor apiCallExecutor;

    @Inject
    TrackedEntityInstanceQueryCallFactory(
            @NonNull TrackedEntityInstanceService service,
            @NonNull SearchGridMapper mapper,
            APICallExecutor apiCallExecutor) {
        this.service = service;
        this.mapper = mapper;
        this.apiCallExecutor = apiCallExecutor;
    }

    public Callable<List<TrackedEntityInstance>> getCall(final TrackedEntityInstanceQuery query) {
        return new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                return queryTrackedEntityInstances(query);
            }
        };
    }

    private List<TrackedEntityInstance> queryTrackedEntityInstances(TrackedEntityInstanceQuery query) throws D2Error {

        OuMode mode = query.orgUnitMode();
        String orgUnitModeStr = mode == null ? null : mode.toString();

        String orgUnits = Utils.joinCollectionWithSeparator(query.orgUnits(), ";");
        Call<SearchGrid> searchGridCall = service.query(orgUnits,
                orgUnitModeStr, query.program(), query.formattedProgramStartDate(), query.formattedProgramEndDate(),
                query.query(), query.attribute(), query.filter(), query.paging(), query.page(), query.pageSize());

        SearchGrid searchGrid;

        try {
            searchGrid = apiCallExecutor.executeObjectCall(searchGridCall);
        } catch (D2Error d2E) {
            if (d2E.httpErrorCode() != null && d2E.httpErrorCode() == HttpsURLConnection.HTTP_REQ_TOO_LONG) {
                throw D2Error.builder()
                        .errorCode(D2ErrorCode.TOO_MANY_ORG_UNITS)
                        .errorDescription("Too many org units were selected")
                        .errorComponent(D2ErrorComponent.SDK)
                        .httpErrorCode(d2E.httpErrorCode())
                        .build();
            } else {
                throw d2E;
            }
        }

        try {
            return mapper.transform(searchGrid);
        } catch (ParseException pe) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.SEARCH_GRID_PARSE)
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorDescription("Search Grid mapping exception")
                    .originalException(pe)
                    .build();
        }
    }
}
