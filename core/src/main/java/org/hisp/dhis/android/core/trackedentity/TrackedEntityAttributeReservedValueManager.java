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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkModelStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeFields;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.utils.internal.BooleanWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
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
    private final IdentifiableObjectStore<ProgramTrackedEntityAttribute> programTrackedEntityAttributeStore;
    private final LinkModelStore<OrganisationUnitProgramLink> organisationUnitProgramLinkLinkModelStore;
    private final D2CallExecutor executor;
    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> reservedValueQueryCallFactory;

    private final D2ProgressManager d2ProgressManager = new D2ProgressManager(null);

    @Inject
    TrackedEntityAttributeReservedValueManager(
            D2CallExecutor executor,
            ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            TrackedEntityAttributeReservedValueStoreInterface store,
            IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore,
            IdentifiableObjectStore<ProgramTrackedEntityAttribute> programTrackedEntityAttributeStore,
            LinkModelStore<OrganisationUnitProgramLink> organisationUnitProgramLinkLinkModelStore,
            QueryCallFactory<TrackedEntityAttributeReservedValue,
                    TrackedEntityAttributeReservedValueQuery> reservedValueQueryCallFactory) {
        this.executor = executor;
        this.systemInfoRepository = systemInfoRepository;
        this.store = store;
        this.organisationUnitStore = organisationUnitStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
        this.organisationUnitProgramLinkLinkModelStore = organisationUnitProgramLinkLinkModelStore;
        this.reservedValueQueryCallFactory = reservedValueQueryCallFactory;
    }

    /**
     * @see #getValue(String, String)
     *
     * @param attribute           Attribute uid
     * @param organisationUnitUid Organisation unit uid
     * @return Value of tracked entity attribute
     */
    public String blockingGetValue(@NonNull String attribute, @NonNull String organisationUnitUid) {
        return getValue(attribute, organisationUnitUid).blockingGet();
    }

    /**
     * Get a reserved value and remove it from database. If the number of available values is below a threshold
     * (default {@link #MIN_TO_TRY_FILL}) it tries to download before returning a value.
     *
     * @param attribute           Attribute uid
     * @param organisationUnitUid Organisation unit uid
     * @return Single with value of tracked entity attribute
     */
    public Single<String> getValue(@NonNull String attribute, @NonNull String organisationUnitUid) {
        Completable optionalDownload = downloadValuesIfBelowThreshold(
                attribute, getOrganisationUnit(organisationUnitUid), null, new BooleanWrapper(false)
        ).onErrorComplete();

        return optionalDownload.andThen(Single.create(emitter -> {
            String pattern = trackedEntityAttributeStore.selectByUid(attribute).pattern();
            String attributeOrgunit = isOrgunitDependent(pattern) ? organisationUnitUid : null;

            TrackedEntityAttributeReservedValue reservedValue = store.popOne(attribute, attributeOrgunit);

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

    /**
     * @see #downloadReservedValues(String, Integer)
     *
     * @param attribute              An optional attribute uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     */
    public void blockingDownloadReservedValues(@NonNull String attribute,
                                               Integer numberOfValuesToFillUp) {

        downloadReservedValues(attribute, numberOfValuesToFillUp).blockingSubscribe();
    }

    /**
     * Download of TrackedEntityInstance reserved values. The number of reserved values is filled up to the
     * numberOfValuesToFillUp. If not defined, it defaults to {@link #FILL_UP_TO}.
     * <br><br>
     * The download is only triggered for this attribute passed as parameter.
     * <br><br>
     * If the attribute pattern is dependent on OrganisationUnit code (that is, it contains "ORG_UNIT_CODE"), it
     * reserves values for each orgunit assigned to the programs with this attribute. It applies the limit
     * per orgunit. Otherwise the limit is applied per attribute.
     *
     * @param attribute              An optional attribute uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return Single with value of tracked entity attribute
     */
    public Observable<D2Progress> downloadReservedValues(@NonNull String attribute,
                                                         Integer numberOfValuesToFillUp) {

        return downloadValuesForOrgUnits(attribute, numberOfValuesToFillUp, new BooleanWrapper(false));
    }

    /**
     * @see #downloadAllReservedValues(Integer)
     *
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     */
    public void blockingDownloadAllReservedValues(Integer numberOfValuesToFillUp) {
        downloadAllReservedValues(numberOfValuesToFillUp).blockingSubscribe();
    }

    /**
     * Downloads reserved values for all the trackedEntityAttributeValues of type "generated",that is, it applies
     * {@link #downloadReservedValues(String, Integer)} for every generated attribute.
     *
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     */
    public Observable<D2Progress> downloadAllReservedValues(Integer numberOfValuesToFillUp) {
        List<Observable<D2Progress>> observables = new ArrayList<>();
        BooleanWrapper systemInfoDownloaded = new BooleanWrapper(false);

        List<TrackedEntityAttribute> generatedAttributes = getGeneratedAttributes();

        for (TrackedEntityAttribute attribute : generatedAttributes) {
            observables.add(downloadValuesForOrgUnits(attribute.uid(), numberOfValuesToFillUp, systemInfoDownloaded));
        }

        return Observable.merge(observables);
    }

    private D2Progress increaseProgress() {
        return d2ProgressManager.increaseProgress(TrackedEntityAttributeReservedValue.class, false);
    }

    private Observable<D2Progress> downloadValuesForOrgUnits(@NonNull String attribute,
                                                             Integer numberOfValuesToFillUp,
                                                             BooleanWrapper systemInfoDownloaded) {

        String pattern = trackedEntityAttributeStore.selectByUid(attribute).pattern();

        if (isOrgunitDependent(pattern)) {
            List<OrganisationUnit> organisationUnits = getOrgUnitsLinkedToAttribute(attribute);
            return Observable.fromIterable(organisationUnits).flatMapSingle(organisationUnit ->
                    downloadValuesIfBelowThreshold(
                            attribute, organisationUnit, numberOfValuesToFillUp, systemInfoDownloaded)
                            .onErrorComplete()
                            .toSingle(this::increaseProgress));
        } else {
            return downloadValuesIfBelowThreshold(attribute, null, numberOfValuesToFillUp, systemInfoDownloaded)
                    .onErrorComplete()
                    .toSingle(this::increaseProgress)
                    .toObservable();
        }
    }

    private Completable downloadValuesIfBelowThreshold(String attribute,
                                                       OrganisationUnit organisationUnit,
                                                       Integer minNumberOfValuesToHave,
                                                       BooleanWrapper systemInfoDownloaded) {
        return Completable.defer(() -> {
            // TODO use server date
            store.deleteExpired(new Date());

            Integer remainingValues = organisationUnit == null ?
                    store.count(attribute) : store.count(attribute, organisationUnit.uid());

            Integer minNumberToTryFill = minNumberOfValuesToHave == null ?
                    MIN_TO_TRY_FILL : minNumberOfValuesToHave;

            if (remainingValues < minNumberToTryFill) {
                Integer numberToReserve =
                        (minNumberOfValuesToHave == null ? FILL_UP_TO : minNumberOfValuesToHave) - remainingValues;

                return downloadValues(attribute, organisationUnit, numberToReserve, systemInfoDownloaded);
            } else {
                return Completable.complete();
            }
        });
    }

    private Completable downloadValues(String trackedEntityAttributeUid,
                                       OrganisationUnit organisationUnit,
                                       Integer numberToReserve,
                                       BooleanWrapper systemInfoDownloaded) {

        Completable downloadSystemInfo = systemInfoDownloaded.get() ? Completable.complete() :
                systemInfoRepository.download().andThen(Completable.fromAction(() -> systemInfoDownloaded.set(true)));

        return downloadSystemInfo.andThen(Completable.fromAction(() -> {
            String trackedEntityAttributePattern;
            try {
                trackedEntityAttributePattern =
                        trackedEntityAttributeStore.selectByUid(trackedEntityAttributeUid).pattern();
            } catch (Exception e) {
                trackedEntityAttributePattern = "";
            }

            executor.executeD2Call(reservedValueQueryCallFactory.create(
                    TrackedEntityAttributeReservedValueQuery.create(trackedEntityAttributeUid, numberToReserve,
                            organisationUnit, trackedEntityAttributePattern)));
        }));
    }

    private List<OrganisationUnit> getOrgUnitsLinkedToAttribute(String attribute) {
        List<String> linkedProgramUids = programTrackedEntityAttributeStore.selectStringColumnsWhereClause(
                ProgramTrackedEntityAttributeFields.PROGRAM,
                new WhereClauseBuilder().appendKeyStringValue(
                        ProgramTrackedEntityAttributeFields.TRACKED_ENTITY_ATTRIBUTE,
                        attribute
                ).build());

        List<String> linkedOrgunitUids = organisationUnitProgramLinkLinkModelStore.selectStringColumnsWhereClause(
                OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
                new WhereClauseBuilder().appendInKeyStringValues(
                        OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                        linkedProgramUids
                ).build());

        return organisationUnitStore.selectWhere(new WhereClauseBuilder().appendInKeyStringValues(
                BaseIdentifiableObjectModel.Columns.UID,
                linkedOrgunitUids
        ).build());
    }

    private List<TrackedEntityAttribute> getGeneratedAttributes() {
        String whereClause = new WhereClauseBuilder()
                .appendKeyNumberValue(TrackedEntityAttributeFields.GENERATED, 1).build();
        return trackedEntityAttributeStore.selectWhere(whereClause);
    }

    private OrganisationUnit getOrganisationUnit(String uid) {
        return uid == null ? null : organisationUnitStore.selectByUid(uid);
    }

    private boolean isOrgunitDependent(String pattern) {
        return pattern != null && pattern.contains("ORG_UNIT_CODE");
    }
}