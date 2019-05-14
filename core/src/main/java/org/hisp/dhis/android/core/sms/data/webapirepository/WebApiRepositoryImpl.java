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
            metadata.lastSyncDate = response.system().date();
            metadata.categoryOptionCombos = mapIds(response.categoryOptionCombos());
            metadata.dataElements = mapIds(response.dataElements());
            metadata.organisationUnits = mapIds(response.organisationUnits());
            metadata.users = mapIds(response.users());
            metadata.trackedEntityTypes = mapIds(response.trackedEntityTypes());
            metadata.trackedEntityAttributes = mapIds(response.trackedEntityAttributes());
            metadata.programs = mapIds(response.programs());
            return metadata;
        });
    }

    private List<Metadata.ID> mapIds(List<MetadataResponseModel.MetadataId> ids) {
        if (ids == null) {
            return null;
        }
        ArrayList<Metadata.ID> newList = new ArrayList<>();
        for (MetadataResponseModel.MetadataId item : ids) {
            newList.add(makeID(item.id()));
        }
        return newList;
    }

    private Metadata.ID makeID(String id) {
        return new Metadata.ID(id);
    }

    private Call<MetadataResponseModel> metadataCall(final GetMetadataIdsConfig c) {
        return apiService.getMetadataIds(
                val(c.dataElements),
                val(c.categoryOptionCombos),
                val(c.organisationUnits),
                val(c.users),
                val(c.trackedEntityTypes),
                val(c.trackedEntityAttributes),
                val(c.programs)
        );
    }

    private String val(boolean enable) {
        return enable ? ApiService.GET_IDS : null;
    }

    private <T> Single<T> translateCallToSingle(Call<T> call) {
        return Single.create(emitter ->
                call.enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            emitter.onSuccess(response.body());
                        } else {
                            emitter.onError(new HttpException(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {
                        emitter.onError(t);
                    }
                })
        );
    }
}
