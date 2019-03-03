package org.hisp.dhis.android.core.sms.data;

import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WebApiRepositoryImpl implements WebApiRepository {
    private Api apiService;

    public WebApiRepositoryImpl(Retrofit retrofit) {
        apiService = retrofit.create(Api.class);
    }

    @Override
    public Single<Metadata> getMetadataIds(final GetMetadataIdsConfig config) {
        return Single.create((SingleOnSubscribe<MetadataResponseModel>) emitter ->
                apiService.getMetadataIds(
                        config.dataElements ? Api.GET_IDS : null,
                        config.categoryOptionCombos ? Api.GET_IDS : null,
                        config.organisationUnits ? Api.GET_IDS : null,
                        config.users ? Api.GET_IDS : null,
                        config.trackedEntityTypes ? Api.GET_IDS : null,
                        config.trackedEntityAttributes ? Api.GET_IDS : null,
                        config.programs ? Api.GET_IDS : null
                ).enqueue(new Callback<MetadataResponseModel>() {
                    // translating Retrofit call to RxJava Single task
                    @Override
                    public void onResponse(Call<MetadataResponseModel> call, Response<MetadataResponseModel> response) {
                        emitter.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<MetadataResponseModel> call, Throwable t) {
                        emitter.onError(t);
                    }
                }))
                .map(response -> {
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
            newList.add(new Metadata.ID(item.id));
        }
        return newList;
    }

    private interface Api {
        String GET_IDS = "id";

        @GET("metadata")
        Call<MetadataResponseModel> getMetadataIds(
                @Query("dataElements:fields") String dataElements,
                @Query("categoryOptionCombos:fields") String categoryOptionCombos,
                @Query("organisationUnits:fields") String organisationUnits,
                @Query("users:fields") String users,
                @Query("trackedEntityTypes:fields") String trackedEntityTypes,
                @Query("trackedEntityAttributes:fields") String trackedEntityAttributes,
                @Query("programs:fields") String programs
        );
    }

    private static class MetadataResponseModel {
        SystemInfo system;
        List<Id> categoryOptionCombos;
        List<Id> organisationUnits;
        List<Id> dataElements;
        List<Id> users;
        List<Id> trackedEntityTypes;
        List<Id> trackedEntityAttributes;
        List<Id> programs;

        private static class SystemInfo {
            Date date;
        }

        private static class Id {
            String id;
        }
    }
}
