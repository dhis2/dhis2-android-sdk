package org.hisp.dhis.android.core.sms.data.webapirepository;

import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WebApiRepositoryImpl implements WebApiRepository {
    private final ApiService apiService;

    public WebApiRepositoryImpl(Retrofit retrofit) {
        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public Single<Metadata> getMetadataIds(final GetMetadataIdsConfig config) {
        return translateCallToSingle(
                metadataCall(config)
        ).map(response -> {
            Metadata metadata = new Metadata();
            metadata.lastSyncDate = response.system.date;
            metadata.categoryOptionCombos = mapIds(response.categoryOptionCombos);
            metadata.dataElements = mapIds(response.dataElements);
            metadata.organisationUnits = mapIds(response.organisationUnits);
            metadata.users = mapIds(response.users);
            metadata.trackedEntityTypes = mapIds(response.trackedEntityTypes);
            metadata.trackedEntityAttributes = mapIds(response.trackedEntityAttributes);
            metadata.programs = mapIds(response.programs);
            return metadata;
        });
    }

    private List<Metadata.ID> mapIds(List<MetadataResponseModel.Id> ids) {
        if (ids == null) {
            return null;
        }
        ArrayList<Metadata.ID> newList = new ArrayList<>();
        for (MetadataResponseModel.Id item : ids) {
            newList.add(makeID(item.id));
        }
        return newList;
    }

    private Metadata.ID makeID(String id) {
        return new Metadata.ID(id);
    }

    private Call<MetadataResponseModel> metadataCall(final GetMetadataIdsConfig config) {
        ConfigWrapper c = new ConfigWrapper(config);
        return apiService.getMetadataIds(
                c.dataElements(),
                c.categoryOptionCombos(),
                c.organisationUnits(),
                c.users(),
                c.trackedEntityTypes(),
                c.trackedEntityAttributes(),
                c.programs()
        );
    }

    private <T> Single<T> translateCallToSingle(Call<T> call) {
        return Single.create(emitter ->
                call.enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {
                        emitter.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {
                        emitter.onError(t);
                    }
                })
        );
    }
}
