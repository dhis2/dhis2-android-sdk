/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeFields;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Reusable
public final class TrackedEntityAttributeReservedValueManager {

    private static final Integer MIN_TO_TRY_FILL = 50;
    private static final Integer FILL_UP_TO = 100;

    private final TrackedEntityAttributeReservedValueStoreInterface store;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;
    private final DatabaseAdapter databaseAdapter;
    private final D2CallExecutor executor;
    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCallFactory;

    private D2ProgressManager d2ProgressManager = new D2ProgressManager(null);

    @Inject
    TrackedEntityAttributeReservedValueManager(
            DatabaseAdapter databaseAdapter,
            D2CallExecutor executor,
            ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            TrackedEntityAttributeReservedValueStoreInterface store,
            IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore,
            QueryCallFactory<TrackedEntityAttributeReservedValue,
                    TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCallFactory) {
        this.databaseAdapter = databaseAdapter;
        this.executor = executor;
        this.systemInfoRepository = systemInfoRepository;
        this.store = store;
        this.organisationUnitStore = organisationUnitStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.trackedEntityAttributeReservedValueQueryCallFactory = trackedEntityAttributeReservedValueQueryCallFactory;
    }

    /**
     * Get a reserved value and remove it from database. If the number of available values is below a threshold
     * (default {@link #MIN_TO_TRY_FILL}) it tries to synchronize before returning a value.
     *
     * @param attribute Attribute uid
     * @param organisationUnitUid Optional organisation uid
     * @return Value of tracked entity attribute
     * @throws D2Error If there are no more reserved values available in database
     */
    @SuppressFBWarnings("DE_MIGHT_IGNORE")
    public String blockingGetValue(String attribute, String organisationUnitUid) {
        return getValue(attribute, organisationUnitUid).blockingGet();
    }

    /**
     * Get a reserved value and remove it from database. If the number of available values is below a threshold
     * (default {@link #MIN_TO_TRY_FILL}) it tries to synchronize before returning a value.
     *
     * @param attribute Attribute uid
     * @param organisationUnitUid Optional organisation uid
     * @return Single with value of tracked entity attribute
     * @throws D2Error If there are no more reserved values available in database (inside Single)
     */
    public Single<String> getValue(String attribute, String organisationUnitUid) {

        return systemInfoRepository.download()
                .andThen(Completable.fromAction(
                        () -> syncReservedValue(attribute, getOrganisationUnit(organisationUnitUid), null)))
                .andThen(Single.create(emitter -> {
                    TrackedEntityAttributeReservedValue reservedValue = store.popOne(attribute, organisationUnitUid);

                    if (reservedValue == null) {
                        emitter.onError(D2Error.builder()
                                .errorCode(D2ErrorCode.NO_RESERVED_VALUES)
                                .errorDescription("There are no reserved values")
                                .errorComponent(D2ErrorComponent.Database).build());
                    } else {
                        emitter.onSuccess(reservedValue.value());
                    }
                }));
    }

    private OrganisationUnit getOrganisationUnit(String uid) {
        return uid == null ? null : organisationUnitStore.selectByUid(uid);
    }

    /**
     * Synchronization of TrackedEntityInstance reserved values. The number of reserved values is filled up to the
     * numberOfValuesToFillUp. If not defined, it defaults to {@link #FILL_UP_TO}.
     * <br><br>
     * If an attribute uid is defined, the synchronization is only triggered for this attribute. If not defined, the
     * synchronization is triggered for all the attributes with the property "generated" set to true.
     * <br><br>
     * If an organisationunit uid is defined, only TrackedEntityAttributes linked to programs that are linked to this
     * organisationunit are considered for syncing. If not defined, the SDK checks if TrackedEntityAttribute pattern
     * contains ORGUNIT_CODE. If so, it reserves values for each orgunit assigned to the program and applies the limit
     * per orgunit. If not, the limit is applied per attribute.
     *
     * @param attribute              An optional attribute uid
     * @param organisationUnitUid    An optional organisationunit uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     */
    public void blockingSyncReservedValues(String attribute, String organisationUnitUid, Integer numberOfValuesToFillUp) {
        syncReservedValues(attribute, organisationUnitUid, numberOfValuesToFillUp).blockingSubscribe();
    }

    /**
     * Synchronization of TrackedEntityInstance reserved values. The number of reserved values is filled up to the
     * numberOfValuesToFillUp. If not defined, it defaults to {@link #FILL_UP_TO}.
     * <br><br>
     * If an attribute uid is defined, the synchronization is only triggered for this attribute. If not defined, the
     * synchronization is triggered for all the attributes with the property "generated" set to true.
     * <br><br>
     * If an organisationunit uid is defined, only TrackedEntityAttributes linked to programs that are linked to this
     * organisationunit are considered for syncing. If not defined, the SDK checks if TrackedEntityAttribute pattern
     * contains ORGUNIT_CODE. If so, it reserves values for each orgunit assigned to the program and applies the limit
     * per orgunit. If not, the limit is applied per attribute.
     *
     * @param attribute              An optional attribute uid
     * @param organisationUnitUid    An optional organisationunit uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return Single with value of tracked entity attribute
     */
    public Observable<D2Progress> syncReservedValues(String attribute, String organisationUnitUid,
                                                     Integer numberOfValuesToFillUp) {
        return systemInfoRepository.download()
                .andThen(syncReservedValuesInternal(attribute, organisationUnitUid, numberOfValuesToFillUp));
    }

    private Observable<D2Progress> syncReservedValuesInternal(String attribute, String organisationUnitUid,
                                                     Integer numberOfValuesToFillUp) {
        if (attribute == null) {
            return syncAllTrackedEntityAttributeReservedValues(numberOfValuesToFillUp, organisationUnitUid);
        } else if (organisationUnitUid == null) {
            return syncTrackedEntityAttributeReservedValue(attribute, numberOfValuesToFillUp);
        } else {
            return Single.fromCallable(() -> {
                OrganisationUnit organisationUnit = organisationUnitStore.selectByUid(organisationUnitUid);
                syncReservedValue(attribute, organisationUnit, numberOfValuesToFillUp);
                return increaseProgress();
            }).toObservable();
        }
    }

    private D2Progress increaseProgress() {
        return d2ProgressManager.increaseProgress(TrackedEntityAttributeReservedValue.class, false);
    }

    private Observable<D2Progress> syncTrackedEntityAttributeReservedValue(String attribute,
                                                                           Integer numberOfValuesToFillUp) {
        List<OrganisationUnit> organisationUnits = getAttributeWithOUCodeOrgUnits(attribute);
        if (organisationUnits.isEmpty()) {
            return Single.fromCallable(() -> {
                syncReservedValue(attribute, null, numberOfValuesToFillUp);
                return increaseProgress();
            }).toObservable();
        } else {
            return Observable.create(emitter -> {
                for (OrganisationUnit organisationUnit : organisationUnits) {
                    syncReservedValue(attribute, organisationUnit, numberOfValuesToFillUp);
                    emitter.onNext(increaseProgress());
                }
                emitter.onComplete();
            });

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

        String trackedEntityAttributePattern;
        try {
            trackedEntityAttributePattern =
                    trackedEntityAttributeStore.selectByUid(trackedEntityAttributeUid).pattern();
        } catch (Exception e) {
            trackedEntityAttributePattern = "";
        }

        executor.executeD2Call(trackedEntityAttributeReservedValueQueryCallFactory.create(
                TrackedEntityAttributeReservedValueQuery.create(
                        trackedEntityAttributeUid, numberToReserve, organisationUnit, trackedEntityAttributePattern)));
    }

    private List<OrganisationUnit> getAttributeWithOUCodeOrgUnits(String attribute) {
        String join = " INNER JOIN ";
        String on = " ON ";
        String eq = " = ";
        String dot = ".";

        String oUUid = OrganisationUnitTableInfo.TABLE_INFO.name() + dot + BaseIdentifiableObjectModel.Columns.UID;
        String programUid = ProgramTableInfo.TABLE_INFO.name() + dot + BaseIdentifiableObjectModel.Columns.UID;
        String oUPLOrganisationUnit = OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name() + dot
                + OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT;
        String oUPLProgram = OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name() + dot
                + OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM;
        String pTEAProgram = ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name() + dot
                + ProgramTrackedEntityAttributeFields.PROGRAM;
        String pTEATrackedEntityAttribute = ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name() + dot
                + ProgramTrackedEntityAttributeFields.TRACKED_ENTITY_ATTRIBUTE;
        String tEAUid = TrackedEntityAttributeTableInfo.TABLE_INFO.name() + dot +
                BaseIdentifiableObjectModel.Columns.UID;
        String tEAPattern = TrackedEntityAttributeTableInfo.TABLE_INFO.name() + dot +
                TrackedEntityAttributeFields.PATTERN;

        String queryStatement = "SELECT " + OrganisationUnitTableInfo.TABLE_INFO.name() + ".* FROM (" +
                OrganisationUnitTableInfo.TABLE_INFO.name() +
                join + OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name() + on + oUUid + eq + oUPLOrganisationUnit +
                join + ProgramTableInfo.TABLE_INFO.name() + on + oUPLProgram + eq + programUid +
                join + ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name() + on + programUid + eq + pTEAProgram +
                join + TrackedEntityAttributeTableInfo.TABLE_INFO.name() + on + tEAUid +
                eq + pTEATrackedEntityAttribute + ") " +
                " WHERE " + new WhereClauseBuilder()
                .appendKeyStringValue(tEAUid, attribute)
                .appendKeyLikeStringValue(tEAPattern, "%ORG_UNIT_CODE%").build() + ";";

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

    private Observable<D2Progress> syncAllTrackedEntityAttributeReservedValues(Integer numberOfValuesToFillUp,
                                                                               String organisationUnitUid) {
        String selectStatement = generateAllTrackedEntityAttributeReservedValuesSelectStatement(organisationUnitUid);

        List<Observable<D2Progress>> observables = new ArrayList<>();

        try (Cursor cursor = databaseAdapter.query(selectStatement)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String ownerUid = cursor.getString(0);
                    observables.add(syncReservedValuesInternal(ownerUid, null, numberOfValuesToFillUp));
                } while (cursor.moveToNext());
            }
        }

        return Observable.merge(observables);
    }

    private static String generateAllTrackedEntityAttributeReservedValuesSelectStatement(String organisationUnitUid) {
        String tEAUidColumn = "t." + BaseIdentifiableObjectModel.Columns.UID;
        String tEAGeneratedColumn = "t." + TrackedEntityAttributeFields.GENERATED;
        String oUPLProgramColumn = "o." + OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM;
        String oUPLOrganisationUnitColumn = "o." + OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT;
        String pTEATEAColumn = "p." + ProgramTrackedEntityAttributeFields.TRACKED_ENTITY_ATTRIBUTE;
        String pTEAProgramColumn = "p." + ProgramTrackedEntityAttributeFields.PROGRAM;

        String selectStatement = "SELECT DISTINCT " + tEAUidColumn + " " +
                "FROM " +
                TrackedEntityAttributeTableInfo.TABLE_INFO.name() + " t, " +
                OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name() + " o, " +
                ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name() + " p " +

                "WHERE " + tEAGeneratedColumn + " = 1";

        if (organisationUnitUid != null) {
            selectStatement = selectStatement.concat(" AND " + pTEATEAColumn + " = " + tEAUidColumn +
                    " AND " + pTEAProgramColumn + " = " + oUPLProgramColumn +
                    " AND " + oUPLOrganisationUnitColumn + " = '" + organisationUnitUid + "'");
        }

        return selectStatement.concat(";");
    }
}