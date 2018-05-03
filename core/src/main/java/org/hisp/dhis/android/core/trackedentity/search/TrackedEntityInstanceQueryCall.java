package org.hisp.dhis.android.core.trackedentity.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.utils.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

public final class TrackedEntityInstanceQueryCall extends SyncCall<List<TrackedEntityInstance>> {

    private final TrackedEntityInstanceService service;
    private final TrackedEntityInstanceQuery query;
    private final SearchGridMapper mapper;

    private TrackedEntityInstanceQueryCall(
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
        D2CallException.Builder httpExceptionBuilder = D2CallException.builder()
                .isHttpError(true).errorDescription("Search Grid call failed");

        try {
            String orgUnits = Utils.joinCollectionWithSeparator(query.orgUnits(), ";");
            Response<SearchGrid> searchGridResponse = service.query(orgUnits,
                    orgUnitModeStr, query.program(), query.query(), query.attribute(), query.filter(),
                    query.paging(), query.page(), query.pageSize()).execute();

            if (!searchGridResponse.isSuccessful()) {
                throw httpExceptionBuilder.httpErrorCode(searchGridResponse.code()).build();
            } else {
                SearchGrid searchGrid = searchGridResponse.body();

                try {
                    return mapper.transform(searchGrid);
                } catch (ParseException pe) {
                    throw D2CallException.builder()
                            .isHttpError(false).errorDescription("Search Grid mapping exception")
                            .build();
                }
            }
        } catch (IOException e) {
            throw httpExceptionBuilder.build();
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
