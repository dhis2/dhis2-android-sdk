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

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import retrofit2.Retrofit;

public final class TrackedEntityAttributeReservedValueManager {

    private static final int MIN_TO_TRY_FILL = 50;
    private static final int FILL_UP_TO = 100;

    private final TrackedEntityAttributeReservedValueStoreInterface store;
    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;

    private TrackedEntityAttributeReservedValueManager(
            DatabaseAdapter databaseAdapter,
            Retrofit retrofit,
            TrackedEntityAttributeReservedValueStoreInterface store,
            IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
            TrackedEntityAttributeStore trackedEntityAttributeStore) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.store = store;
        this.organisationUnitStore = organisationUnitStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
    }

    @SuppressFBWarnings("DE_MIGHT_IGNORE")
    public String getValue(String attribute, String organisationUnitUid) throws D2CallException {
        syncReservedValues(attribute, organisationUnitUid, false);

        TrackedEntityAttributeReservedValueModel reservedValue = store.popOne(attribute, organisationUnitUid);

        if (reservedValue == null) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.NO_RESERVED_VALUES)
                    .errorDescription("There are no reserved values")
                    .isHttpError(false).build();
        } else {
            return reservedValue.value();
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private void syncReservedValues(String attribute, String organisationUnitUid, boolean forceFill) {
        try {
            // TODO use server date
            store.deleteExpired(new Date());

            int remainingValues = store.count(attribute, organisationUnitUid);
            if (forceFill || remainingValues <= MIN_TO_TRY_FILL) {
                fillReservedValues(attribute, organisationUnitUid, remainingValues);
            }
        } catch (D2CallException ignored) {
            // Synchronization was not successful.
        }
    }

    public void forceSyncReservedValues(String attribute, String organisationUnitUid) {
        syncReservedValues(attribute, organisationUnitUid, true);
    }

    private void fillReservedValues(String trackedEntityAttributeUid, String organisationUnitUid,
                                    Integer remainingValues) throws D2CallException {

        D2CallExecutor executor = new D2CallExecutor();

        SystemInfo systemInfo = executor.executeD2Call(
                SystemInfoCall.FACTORY.create(databaseAdapter, retrofit));

        GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                systemInfo.serverDate());

        Integer numberToReserve = FILL_UP_TO - remainingValues;
        OrganisationUnitModel organisationUnitModel =
                this.organisationUnitStore.selectByUid(organisationUnitUid, OrganisationUnitModel.factory);

        String trackedEntityAttributePattern;
        try {
            trackedEntityAttributePattern = trackedEntityAttributeStore.getPattern(trackedEntityAttributeUid);
        } catch (Exception e) {
            trackedEntityAttributePattern = "";
        }

        executor.executeD2Call(TrackedEntityAttributeReservedValueEndpointCall.FACTORY.create(
                genericCallData, TrackedEntityAttributeReservedValueQuery.create(trackedEntityAttributeUid,
                        numberToReserve, organisationUnitModel, trackedEntityAttributePattern)));
    }

    public void syncAllTrackedEntityAttributeReservedValues() {
        String selectStatement = generateAllTrackedEntityAttributeReservedValuesSelectStatement();
        Cursor cursor = databaseAdapter.query(selectStatement);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String ownerUid = cursor.getString(0);
                String orgUnitUid = cursor.getString(1);
                forceSyncReservedValues(ownerUid, orgUnitUid);
            } while (cursor.moveToNext());
        }
    }

    private static String generateAllTrackedEntityAttributeReservedValuesSelectStatement() {
        String tEAUidColumn = "t." + TrackedEntityAttributeModel.Columns.UID;
        String tEAGeneratedColumn = "t." + TrackedEntityAttributeModel.Columns.GENERATED;
        String oUPLOrgUnitColumn = "o." + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT;
        String oUPLProgramColumn = "o." + OrganisationUnitProgramLinkModel.Columns.PROGRAM;
        String pTEATEAColumn = "p." + ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE;
        String pTEAProgramColumn = "p." + ProgramTrackedEntityAttributeModel.Columns.PROGRAM;

        return "SELECT DISTINCT " +
                tEAUidColumn + ", " +
                oUPLOrgUnitColumn  + " " +

                "FROM " +
                TrackedEntityAttributeModel.TABLE + " t, " +
                OrganisationUnitProgramLinkModel.TABLE + " o, " +
                ProgramTrackedEntityAttributeModel.TABLE + " p " +

                "WHERE " +
                tEAGeneratedColumn + " = 1 AND " +
                pTEATEAColumn + " = " + tEAUidColumn + " AND " +
                pTEAProgramColumn + " = " + oUPLProgramColumn + ";";
    }

    public static TrackedEntityAttributeReservedValueManager create(DatabaseAdapter databaseAdapter,
                                                                    Retrofit retrofit) {
        return new TrackedEntityAttributeReservedValueManager(
                databaseAdapter,
                retrofit,
                TrackedEntityAttributeReservedValueStore.create(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
        new TrackedEntityAttributeStoreImpl(databaseAdapter));
    }
}