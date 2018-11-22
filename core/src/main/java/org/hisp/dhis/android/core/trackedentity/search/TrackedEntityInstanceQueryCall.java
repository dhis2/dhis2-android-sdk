package org.hisp.dhis.android.core.trackedentity.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.APICallExecutorImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.utils.Utils;

import java.text.ParseException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Retrofit;

@SuppressWarnings({"PMD.PreserveStackTrace"})
public final class TrackedEntityInstanceQueryCall extends SyncCall<List<TrackedEntityInstance>> {

    private final TrackedEntityInstanceService service;
    private final TrackedEntityInstanceQuery query;
    private final SearchGridMapper mapper;
    private final APICallExecutor apiCallExecutor;

    TrackedEntityInstanceQueryCall(
            @NonNull TrackedEntityInstanceService service,
            @NonNull TrackedEntityInstanceQuery query,
            @NonNull SearchGridMapper mapper,
            APICallExecutor apiCallExecutor) {
        this.service = service;
        this.query = query;
        this.mapper = mapper;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2Error {
        setExecuted();

        OuMode mode = query.orgUnitMode();
        String orgUnitModeStr = mode == null ? null : mode.toString();

        String orgUnits = Utils.joinCollectionWithSeparator(query.orgUnits(), ";");
        Call<SearchGrid> searchGridCall = service.query(orgUnits,
                orgUnitModeStr, query.program(), query.query(), query.attribute(), query.filter(),
                query.paging(), query.page(), query.pageSize());

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

    public static TrackedEntityInstanceQueryCall create(Retrofit retrofit, DatabaseAdapter databaseAdapter,
                                                        TrackedEntityInstanceQuery query) {
        return new TrackedEntityInstanceQueryCall(
                retrofit.create(TrackedEntityInstanceService.class),
                query,
                new SearchGridMapper(),
                APICallExecutorImpl.create(databaseAdapter));
    }
}
