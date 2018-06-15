package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.calls.factories.GenericCallFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.List;

public final class CategoryComboEndpointCall extends SyncCall<List<CategoryCombo>> {

    private final GenericCallData data;
    private final CategoryComboQuery query;
    private final CategoryComboService service;
    private final CategoryComboHandler handler;

    private CategoryComboEndpointCall(
            GenericCallData data,
            CategoryComboQuery query,
            CategoryComboService service,
            CategoryComboHandler handler) {
        this.data = data;
        this.query = query;
        this.service = service;
        this.handler = handler;
    }

    @Override
    public List<CategoryCombo> call() throws Exception {
        setExecuted();

        retrofit2.Call<Payload<CategoryCombo>> call = service.getCategoryCombos(getFields(),
                query.paging(), query.page(), query.pageSize());
        List<CategoryCombo> categoryCombos = new APICallExecutor().executePayloadCall(call);
        handle(categoryCombos);
        return categoryCombos;
    }

    private void handle(List<CategoryCombo> categoryCombos) {
        Transaction transaction = data.databaseAdapter().beginNewTransaction();

        try {
            for (CategoryCombo categoryCombo : categoryCombos) {
                handler.handle(categoryCombo);
            }
            data.handleResource(ResourceModel.Type.CATEGORY_COMBO);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    @NonNull
    private Fields<CategoryCombo> getFields() {

        return Fields.<CategoryCombo>builder().fields(CategoryCombo.uid, CategoryCombo.code,
                CategoryCombo.name, CategoryCombo.displayName,
                CategoryCombo.created, CategoryCombo.lastUpdated, CategoryCombo.deleted,
                CategoryCombo.displayName, CategoryCombo.isDefault, CategoryCombo.categories,
                CategoryCombo.categoryOptionCombos.with(CategoryOptionCombo.uid,
                        CategoryOptionCombo.code,
                        CategoryOptionCombo.name,
                        CategoryOptionCombo.displayName,
                        CategoryOptionCombo.created,
                        CategoryOptionCombo.lastUpdated,
                        CategoryOptionCombo.deleted,
                        CategoryOptionCombo.categoryCombo.with(CategoryCombo.uid),
                        CategoryOptionCombo.displayName,
                        CategoryOptionCombo.categoryOptions.with(
                                CategoryOption.uid
                        )))
                .build();
    }

    public static final GenericCallFactory<List<CategoryCombo>> FACTORY
            = new GenericCallFactory<List<CategoryCombo>>() {

        @Override
        public Call<List<CategoryCombo>> create(GenericCallData genericCallData) {
            return new CategoryComboEndpointCall(
                    genericCallData,
                    CategoryComboQuery.defaultQuery(),
                    genericCallData.retrofit().create(CategoryComboService.class),
                    CategoryComboHandler.create(genericCallData.databaseAdapter())
            );
        }
    };
}
