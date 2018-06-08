package org.hisp.dhis.android.core.trackedentity.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.utils.Utils;

import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

@SuppressWarnings({"PMD.PreserveStackTrace"})
public final class TrackedEntityInstanceQueryCall extends SyncCall<List<TrackedEntityInstance>> {

    private final TrackedEntityInstanceService service;
    private final TrackedEntityInstanceQuery query;
    private final SearchGridMapper mapper;

    TrackedEntityInstanceQueryCall(
            @NonNull TrackedEntityInstanceService service,
            @NonNull TrackedEntityInstanceQuery query,
            @NonNull SearchGridMapper mapper) {
        this.service = service;
        this.query = query;
        this.mapper = mapper;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2CallException {
        super.setExecuted();

        OuMode mode = query.orgUnitMode();
        String orgUnitModeStr = mode == null ? null : mode.toString();

        String orgUnits = Utils.joinCollectionWithSeparator(query.orgUnits(), ";");
        Call<SearchGrid> searchGridCall = service.query(orgUnits,
                orgUnitModeStr, query.program(), query.query(), query.attribute(), query.filter(),
                query.paging(), query.page(), query.pageSize());
        SearchGrid searchGrid = new APICallExecutor().executeObjectCall(searchGridCall);
        try {
            return mapper.transform(searchGrid);
        } catch (ParseException pe) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.SEARCH_GRID_PARSE)
                    .isHttpError(false).errorDescription("Search Grid mapping exception")
                    .originalException(pe)
                    .build();
        }
    }

    public static TrackedEntityInstanceQueryCall create(Retrofit retrofit, TrackedEntityInstanceQuery query) {
        return new TrackedEntityInstanceQueryCall(
                retrofit.create(TrackedEntityInstanceService.class),
                query,
                new SearchGridMapper()
        );
    }
}
