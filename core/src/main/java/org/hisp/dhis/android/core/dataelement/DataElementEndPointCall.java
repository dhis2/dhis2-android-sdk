package org.hisp.dhis.android.core.dataelement;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class DataElementEndPointCall implements
        Call<Response<Payload<DataElement>>> {
    private final DataElementService dataElementService;
    private final DatabaseAdapter databaseAdapter;
    private final DataElementQuery dataElementQuery;
    private final Date serverDate;
    private final DataElementHandler dataElementHandler;
    private final ResourceHandler resourceHandler;

    private boolean isExecuted;

    public DataElementEndPointCall(
            @NonNull DataElementService dataElementService,
            @NonNull DataElementQuery dataElementQuery,
            @NonNull DataElementHandler trackedEntityAttributeHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull DatabaseAdapter databaseAdapter,
            Date serverDate) {
        this.dataElementService = dataElementService;
        this.databaseAdapter = databaseAdapter;
        this.dataElementQuery = dataElementQuery;
        this.serverDate = new Date(serverDate.getTime());
        this.dataElementHandler = trackedEntityAttributeHandler;
        this.resourceHandler = resourceHandler;

        if (dataElementQuery.getUIds() != null
                && dataElementQuery.getUIds().size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of dataElement: "
                            + dataElementQuery.getUIds().size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<DataElement>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        String lastSyncedDataElements = resourceHandler.getLastUpdated(
                ResourceModel.Type.DATA_ELEMENT);

        Response<Payload<DataElement>> dataElementByUids =
                dataElementService.getDataElements(getFields(),
                        DataElement.uid.in(dataElementQuery.getUIds()),
                        DataElement.lastUpdated.gt(
                                lastSyncedDataElements)).execute();

        if (dataElementByUids.isSuccessful()
                && dataElementByUids.body().items() != null) {

            Transaction transaction = databaseAdapter.beginNewTransaction();

            try {
                List<DataElement> dataElements =
                        dataElementByUids.body().items();

                for (DataElement dataElement : dataElements) {
                    dataElementHandler.handleDataElement(
                            dataElement);
                }
                resourceHandler.handleResource(ResourceModel.Type.DATA_ELEMENT,
                        serverDate);
                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
        return dataElementByUids;
    }


    private Fields<DataElement> getFields() {
        return Fields.<DataElement>builder().fields(
                DataElement.uid, DataElement.code, DataElement.name, DataElement.displayName,
                DataElement.created, DataElement.lastUpdated, DataElement.shortName,
                DataElement.displayShortName, DataElement.description,
                DataElement.displayDescription, DataElement.aggregationType,
                DataElement.deleted, DataElement.dimension, DataElement.displayFormName,
                DataElement.domainType, DataElement.formName, DataElement.numberType,
                DataElement.valueType, DataElement.zeroIsSignificant,
                DataElement.optionSet.with(OptionSet.uid, OptionSet.version),
                DataElement.categoryCombo.with(CategoryCombo.uid)).build();
    }
}