package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectFactory;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoQuery;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.user.User;

import java.util.Date;

import retrofit2.Response;

@SuppressWarnings({"PMD.NPathComplexity", "PMD.StdCyclomaticComplexity",
        "PMD.CouplingBetweenObjects",
        "PMD.ExcessiveImports", "PMD.ModifiedCyclomaticComplexity", "PMD.CyclomaticComplexity"
})
public class DeletedObjectCall implements Call<Response> {
    private final DatabaseAdapter databaseAdapter;
    private final SystemInfoService systemInfoService;
    private final ResourceStore resourceStore;
    private final SystemInfoStore systemInfoStore;
    DeletedObjectFactory deletedObjectFactory;

    private boolean isExecuted;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public DeletedObjectCall(@NonNull DatabaseAdapter databaseAdapter,
            @NonNull SystemInfoService systemInfoService,
            @NonNull SystemInfoStore systemInfoStore,
            @NonNull ResourceStore resourceStore,
            @NonNull DeletedObjectFactory deletedObjectFactory,
            boolean isTranslationOn,
            @NonNull String translationLocale) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoService = systemInfoService;
        this.systemInfoStore = systemInfoStore;
        this.resourceStore = resourceStore;
        this.deletedObjectFactory = deletedObjectFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }


    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        Response response = null;
        Transaction transaction = databaseAdapter.beginNewTransaction();

        SystemInfoQuery systemInfoQuery = SystemInfoQuery.defaultQuery(isTranslationOn,
                translationLocale);
        try {
            response = new SystemInfoCall(
                    databaseAdapter, systemInfoStore, systemInfoService, resourceStore,
                    systemInfoQuery)
                    .call();

            if (!response.isSuccessful()) {
                return response;
            }

            SystemInfo systemInfo = (SystemInfo) response.body();
            Date serverDate = systemInfo.serverDate();

            response = syncDeletedObject(serverDate,
                    User.class);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncDeletedObject(serverDate,
                    OrganisationUnit.class);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncCategories(serverDate);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncPrograms(serverDate);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncDeletedObject(serverDate,
                    TrackedEntityAttribute.class);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncDeletedObject(serverDate,
                    RelationshipType.class);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncDeletedObject(serverDate,
                    Program.class);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncDeletedObject(serverDate,
                    TrackedEntity.class);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncDeletedObject(serverDate,
                    Option.class);

            if (!response.isSuccessful()) {
                return response;
            }
            response = syncDeletedObject(serverDate,
                    OptionSet.class);


            transaction.setSuccessful();
            return response;
        } finally {
            transaction.end();
        }
    }

    @Nullable
    private Response syncPrograms(Date serverDate) throws Exception {
        Response response = syncDeletedObject(serverDate,
                ProgramRule.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramRuleAction.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramRuleVariable.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramIndicator.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                DataElement.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramStage.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramStageDataElement.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramStageSection.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                ProgramTrackedEntityAttribute.class);

        return response;
    }

    @Nullable
    private Response syncCategories(Date serverDate) throws Exception {
        Response response = syncDeletedObject(serverDate,
                Category.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                CategoryOption.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                CategoryCombo.class);

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                CategoryOptionCombo.class);

        return response;
    }


    private Response<Payload<DeletedObject>> syncDeletedObject(Date serverDate,
            Class<?> deletedObjectKlass)
            throws Exception {
        return deletedObjectFactory.newEndPointCall(deletedObjectKlass, serverDate).call();
    }
}
