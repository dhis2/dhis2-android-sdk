/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.calls.factories.NoArgumentsCallFactory;
import org.hisp.dhis.android.core.calls.factories.QueryCallFactory;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import retrofit2.Retrofit;

public final class TrackedEntityAttributeReservedValueManager {

    private static final Integer MIN_TO_TRY_FILL = 50;
    private static final Integer FILL_UP_TO = 100;

    private final TrackedEntityAttributeReservedValueStoreInterface store;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final NoArgumentsCallFactory<SystemInfo> systemInfoCallFactory;
    private final DHISVersionManager versionManager;
    private final QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCallFactory;

    TrackedEntityAttributeReservedValueManager(
            DatabaseAdapter databaseAdapter,
            Retrofit retrofit,
            NoArgumentsCallFactory<SystemInfo> systemInfoCallFactory,
            DHISVersionManager versionManager,
            TrackedEntityAttributeReservedValueStoreInterface store,
            IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            TrackedEntityAttributeStore trackedEntityAttributeStore,
            QueryCallFactory<TrackedEntityAttributeReservedValue,
                    TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCallFactory) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.systemInfoCallFactory = systemInfoCallFactory;
        this.versionManager = versionManager;
        this.store = store;
        this.organisationUnitStore = organisationUnitStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.trackedEntityAttributeReservedValueQueryCallFactory = trackedEntityAttributeReservedValueQueryCallFactory;
    }

    @SuppressFBWarnings("DE_MIGHT_IGNORE")
    public String getValue(String attribute, String organisationUnitUid) throws D2Error {
        OrganisationUnit organisationUnit = organisationUnitUid == null ? null :
                OrganisationUnitStore.create(databaseAdapter).selectByUid(organisationUnitUid);
        syncReservedValue(attribute, organisationUnit, null);

        TrackedEntityAttributeReservedValue reservedValue = store.popOne(attribute, organisationUnitUid);

        if (reservedValue == null) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.NO_RESERVED_VALUES)
                    .errorDescription("There are no reserved values")
                    .errorComponent(D2ErrorComponent.Database).build();
        } else {
            return reservedValue.value();
        }
    }

    public void syncReservedValues(String attribute, String organisationUnitUid, Integer numberOfValuesToFillUp) {

        if (attribute == null) {
            syncAllTrackedEntityAttributeReservedValues(numberOfValuesToFillUp, organisationUnitUid);
        } else if (organisationUnitUid == null) {
            syncTrackedEntityAttributeReservedValue(attribute, numberOfValuesToFillUp);
        } else {
            OrganisationUnit organisationUnit = organisationUnitStore.selectByUid(organisationUnitUid);
            syncReservedValue(attribute, organisationUnit, numberOfValuesToFillUp);
        }
    }

    private void syncTrackedEntityAttributeReservedValue(String attribute, Integer numberOfValuesToFillUp) {
        List<OrganisationUnit> organisationUnits = getAttributeWithOUCodeOrgUnits(attribute);

        if (organisationUnits.isEmpty()) {
            syncReservedValue(attribute, null, numberOfValuesToFillUp);
        } else {
            for (OrganisationUnit organisationUnit : organisationUnits) {
                syncReservedValue(attribute, organisationUnit, numberOfValuesToFillUp);
            }
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private void syncReservedValue(String attribute,
                                   OrganisationUnit organisationUnit,
                                   Integer minNumberOfValuesToHave) {
        try {
            // TODO use server date
            store.deleteExpired(new Date());

            Integer remainingValues = organisationUnit == null ?
                    store.count(attribute) : store.count(attribute, organisationUnit.uid());

            Integer minNumberToTryFill = minNumberOfValuesToHave == null ?
                    MIN_TO_TRY_FILL : minNumberOfValuesToHave;

            if (remainingValues < minNumberToTryFill) {
                Integer numberToReserve =
                        (minNumberOfValuesToHave == null ? FILL_UP_TO : minNumberOfValuesToHave) - remainingValues;

                fillReservedValues(attribute, organisationUnit, numberToReserve);
            }

        } catch (D2Error ignored) {
            // Synchronization was not successful.
        }
    }

    private void fillReservedValues(String trackedEntityAttributeUid, OrganisationUnit organisationUnit,
                                    Integer numberToReserve) throws D2Error {

        D2CallExecutor executor = new D2CallExecutor();

        SystemInfo systemInfo = executor.executeD2Call(systemInfoCallFactory.create());

        GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                systemInfo.serverDate(), versionManager);

        String trackedEntityAttributePattern;
        try {
            trackedEntityAttributePattern = trackedEntityAttributeStore.getPattern(trackedEntityAttributeUid);
        } catch (Exception e) {
            trackedEntityAttributePattern = "";
        }

        executor.executeD2Call(trackedEntityAttributeReservedValueQueryCallFactory.create(genericCallData,
                TrackedEntityAttributeReservedValueQuery.create(
                        trackedEntityAttributeUid, numberToReserve, organisationUnit, trackedEntityAttributePattern)));
    }

    private List<OrganisationUnit> getAttributeWithOUCodeOrgUnits(String attribute) {
        String join = " INNER JOIN ";
        String on = " ON";

        String queryStatement = "SELECT OrganisationUnit.* FROM (" + OrganisationUnitTableInfo.TABLE_INFO.name() +
                join + OrganisationUnitProgramLinkModel.TABLE + on +
                " OrganisationUnit.uid = OrganisationUnitProgramLink.organisationUnit " +
                join + ProgramTableInfo.TABLE_INFO.name() + on +
                " OrganisationUnitProgramLink.program = Program.uid " +
                join + ProgramTrackedEntityAttributeModel.TABLE + on +
                " Program.uid = ProgramTrackedEntityAttribute.program " +
                join + TrackedEntityAttributeModel.TABLE + on +
                " TrackedEntityAttribute.uid = ProgramTrackedEntityAttribute.trackedEntityAttribute) " +
                " WHERE TrackedEntityAttribute.uid = '" + attribute + "'" +
                " AND TrackedEntityAttribute.pattern LIKE '%ORG_UNIT_CODE%';";

        List<OrganisationUnit> organisationUnits = new ArrayList<>();

        try (Cursor cursor = databaseAdapter.query(queryStatement)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    organisationUnits.add(OrganisationUnit.create(cursor));
                } while (cursor.moveToNext());
            }
        }

        return organisationUnits;
    }

    private void syncAllTrackedEntityAttributeReservedValues(Integer numberOfValuesToFillUp,
                                                             String organisationUnitUid) {
        String selectStatement = generateAllTrackedEntityAttributeReservedValuesSelectStatement(organisationUnitUid);

        try (Cursor cursor = databaseAdapter.query(selectStatement)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String ownerUid = cursor.getString(0);
                    syncReservedValues(ownerUid, null, numberOfValuesToFillUp);
                } while (cursor.moveToNext());
            }
        }
    }

    private static String generateAllTrackedEntityAttributeReservedValuesSelectStatement(String organisationUnitUid) {
        String tEAUidColumn = "t." + TrackedEntityAttributeModel.Columns.UID;
        String tEAGeneratedColumn = "t." + TrackedEntityAttributeModel.Columns.GENERATED;
        String oUPLProgramColumn = "o." + OrganisationUnitProgramLinkModel.Columns.PROGRAM;
        String oUPLOrganisationUnitColumn = "o." + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT;
        String pTEATEAColumn = "p." + ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE;
        String pTEAProgramColumn = "p." + ProgramTrackedEntityAttributeModel.Columns.PROGRAM;

        String selectStatement = "SELECT DISTINCT " + tEAUidColumn + " " +
                "FROM " +
                TrackedEntityAttributeModel.TABLE + " t, " +
                OrganisationUnitProgramLinkModel.TABLE + " o, " +
                ProgramTrackedEntityAttributeModel.TABLE + " p " +

                "WHERE " + tEAGeneratedColumn + " = 1";

        if (organisationUnitUid != null) {
            selectStatement = selectStatement.concat(" AND " + pTEATEAColumn + " = " + tEAUidColumn +
                                    " AND " + pTEAProgramColumn + " = " + oUPLProgramColumn +
                                    " AND " + oUPLOrganisationUnitColumn + " = '" + organisationUnitUid + "'");
        }

        return selectStatement.concat(";");
    }

    public static TrackedEntityAttributeReservedValueManager create(DatabaseAdapter databaseAdapter,
                                                                    Retrofit retrofit,
                                                                    D2InternalModules internalModules) {
        return new TrackedEntityAttributeReservedValueManager(
                databaseAdapter,
                retrofit,
                internalModules.systemInfo.callFactory,
                internalModules.systemInfo.publicModule.versionManager,
                TrackedEntityAttributeReservedValueStore.create(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
                new TrackedEntityAttributeStoreImpl(databaseAdapter),
                TrackedEntityAttributeReservedValueEndpointCall.factory(APICallExecutorImpl.create(databaseAdapter)));
    }
}