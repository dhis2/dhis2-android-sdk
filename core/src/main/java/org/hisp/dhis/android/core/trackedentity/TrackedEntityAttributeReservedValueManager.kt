/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.settings.GeneralSettingObjectRepository;
import org.hisp.dhis.android.core.settings.GeneralSettings;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStoreInterface;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Reusable
@SuppressWarnings("PMD.ExcessiveImports")
public final class TrackedEntityAttributeReservedValueManager {

    private static final Integer FILL_UP_TO = 100;
    private static final Double FACTOR_TO_REFILL = 0.5;


    private final TrackedEntityAttributeReservedValueStoreInterface store;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;
    private final IdentifiableObjectStore<ProgramTrackedEntityAttribute> programTrackedEntityAttributeStore;
    private final LinkStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final GeneralSettingObjectRepository generalSettingObjectRepository;
    private final IdentifiableObjectStore<ReservedValueSetting> reservedValueSettingStore;
    private final D2CallExecutor executor;
    private final QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> reservedValueQueryCallFactory;

    private final D2ProgressManager d2ProgressManager = new D2ProgressManager(null);

    @Inject
    TrackedEntityAttributeReservedValueManager(
            TrackedEntityAttributeReservedValueStoreInterface store,
            IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore,
            IdentifiableObjectStore<ProgramTrackedEntityAttribute> programTrackedEntityAttributeStore,
            LinkStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore,
            UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            GeneralSettingObjectRepository generalSettingObjectRepository,
            IdentifiableObjectStore<ReservedValueSetting> reservedValueSettingStore,
            D2CallExecutor executor,
            QueryCallFactory<TrackedEntityAttributeReservedValue,
                    TrackedEntityAttributeReservedValueQuery> reservedValueQueryCallFactory) {
        this.store = store;
        this.organisationUnitStore = organisationUnitStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
        this.organisationUnitProgramLinkStore = organisationUnitProgramLinkStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.generalSettingObjectRepository = generalSettingObjectRepository;
        this.reservedValueSettingStore = reservedValueSettingStore;
        this.executor = executor;
        this.reservedValueQueryCallFactory = reservedValueQueryCallFactory;
    }

    /**
     * @see #getValue(String, String)
     *
     * @param attributeUid          Attribute uid
     * @param organisationUnitUid   Organisation unit uid
     * @return Value of tracked entity attribute
     */
    public String blockingGetValue(@NonNull String attributeUid, @NonNull String organisationUnitUid) {
        return getValue(attributeUid, organisationUnitUid).blockingGet();
    }

    /**
     * Get a reserved value and remove it from database. If the number of available values is below a threshold
     * (default {@link #FILL_UP_TO} * {@link #FACTOR_TO_REFILL}) it tries to download before returning a value.
     *
     * @param attributeUid          Attribute uid
     * @param organisationUnitUid   Organisation unit uid
     * @return Single with value of tracked entity attribute
     */
    public Single<String> getValue(@NonNull String attributeUid, @NonNull String organisationUnitUid) {
        Completable optionalDownload = downloadValuesIfBelowThreshold(
                attributeUid, getOrganisationUnit(organisationUnitUid), null, false
        ).onErrorComplete();

        return optionalDownload.andThen(Single.create(emitter -> {
            String pattern = trackedEntityAttributeStore.selectByUid(attributeUid).pattern();
            String attributeOrgunit = isOrgunitDependent(pattern) ? organisationUnitUid : null;

            TrackedEntityAttributeReservedValue reservedValue = store.popOne(attributeUid, attributeOrgunit);

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
     * @param attributeUid              Attribute uid
     * @param numberOfValuesToFillUp    An optional maximum number of values to reserve
     */
    public void blockingDownloadReservedValues(@NonNull String attributeUid,
                                               Integer numberOfValuesToFillUp) {

        downloadReservedValues(attributeUid, numberOfValuesToFillUp).blockingSubscribe();
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
     * @param attributeUid              Attribute uid
     * @param numberOfValuesToFillUp    An optional maximum number of values to reserve
     * @return An Observable that notifies about the progress.
     */
    public Observable<D2Progress> downloadReservedValues(@NonNull String attributeUid,
                                                         Integer numberOfValuesToFillUp) {

        return downloadValuesForOrgUnits(attributeUid, numberOfValuesToFillUp);
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
     * Downloads reserved values for all the trackedEntityAttributeValues of type "generated", that is, it applies
     * {@link #downloadReservedValues(String, Integer)} for every generated attribute.
     *
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return An Observable that notifies about the progress.
     */
    public Observable<D2Progress> downloadAllReservedValues(Integer numberOfValuesToFillUp) {
        List<Observable<D2Progress>> observables = new ArrayList<>();

        List<TrackedEntityAttribute> generatedAttributes = getGeneratedAttributes();

        for (TrackedEntityAttribute attribute : generatedAttributes) {
            observables.add(downloadValuesForOrgUnits(attribute.uid(), numberOfValuesToFillUp));
        }

        return Observable.merge(observables);
    }

    /**
     * Get the count of the reserved values by attribute. If a organisation unit uid is inserted as parameter the method
     * will return the count of the reserved values by attribute and organisation unit.
     *
     * @param attributeUid          Attribute uid
     * @param organisationUnitUid   An optional organisation unit uid
     * @return Single with the reserved value count by attribute or by attribute and organisation unit.
     */
    public Single<Integer> count(@NonNull String attributeUid, @Nullable String organisationUnitUid) {
        return Single.fromCallable(() -> blockingCount(attributeUid, organisationUnitUid));
    }

    /**
     * @see #count(String, String)
     *
     * @param attributeUid          Attribute uid
     * @param organisationUnitUid   An optional organisation unit uid
     * @return The reserved value count by attribute or by attribute and organisation unit.
     */
    public int blockingCount(@NonNull String attributeUid, @Nullable String organisationUnitUid) {
        return store.count(attributeUid, organisationUnitUid, null);
    }

    /**
     * Generate a list of reserved value summaries from the existing tracked entity attribute reserved values in the DB.
     *
     * @return Single with a list of the reserved value summaries
     */
    public Single<List<ReservedValueSummary>> getReservedValueSummaries() {
        return Single.just(blockingGetReservedValueSummaries());
    }

    /**
     * @see #getReservedValueSummaries()
     *
     * @return List of the reserved value summaries
     */
    public List<ReservedValueSummary> blockingGetReservedValueSummaries() {
        String whereClause = new WhereClauseBuilder()
                .appendKeyNumberValue(TrackedEntityAttributeTableInfo.Columns.GENERATED, 1).build();
        String orderByClause = OrderByClauseBuilder.orderByFromItems(
                Collections.singletonList(RepositoryScopeOrderByItem.builder()
                        .column(IdentifiableColumns.DISPLAY_NAME)
                        .direction(RepositoryScope.OrderByDirection.ASC).build()),
                CoreColumns.ID);
        List<TrackedEntityAttribute> trackedEntityAttributes =
                trackedEntityAttributeStore.selectWhere(whereClause, orderByClause);

        List<ReservedValueSummary> reservedValueSummaries = new ArrayList<>();

        for (TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributes) {
            ReservedValueSummary.Builder builder = ReservedValueSummary.builder()
                    .trackedEntityAttribute(trackedEntityAttribute);
            if (isOrgunitDependent(trackedEntityAttribute.pattern())) {
                List<OrganisationUnit> organisationUnits = getOrgUnitsLinkedToAttribute(trackedEntityAttribute.uid());
                for (OrganisationUnit organisationUnit : organisationUnits) {
                    builder.organisationUnit(organisationUnit)
                            .count(blockingCount(trackedEntityAttribute.uid(), organisationUnit.uid()))
                            .numberOfValuesToFillUp(getFillUpToValue(null, trackedEntityAttribute.uid()));
                    reservedValueSummaries.add(builder.build());
                }
            } else {
                builder.count(blockingCount(trackedEntityAttribute.uid(), null))
                        .numberOfValuesToFillUp(getFillUpToValue(null, trackedEntityAttribute.uid()));
                reservedValueSummaries.add(builder.build());
            }

        }

        return reservedValueSummaries;
    }

    private D2Progress increaseProgress() {
        return d2ProgressManager.increaseProgress(TrackedEntityAttributeReservedValue.class, false);
    }

    private Observable<D2Progress> downloadValuesForOrgUnits(@NonNull String attribute,
                                                             Integer numberOfValuesToFillUp) {

        String pattern = trackedEntityAttributeStore.selectByUid(attribute).pattern();

        if (isOrgunitDependent(pattern)) {
            List<OrganisationUnit> organisationUnits = getOrgUnitsLinkedToAttribute(attribute);
            return Observable.fromIterable(organisationUnits).flatMapSingle(organisationUnit ->
                    downloadValuesIfBelowThreshold(attribute, organisationUnit, numberOfValuesToFillUp, true)
                            .onErrorComplete()
                            .toSingle(this::increaseProgress));
        } else {
            return downloadValuesIfBelowThreshold(attribute, null, numberOfValuesToFillUp, true)
                    .onErrorComplete()
                    .toSingle(this::increaseProgress)
                    .toObservable();
        }
    }

    private Completable downloadValuesIfBelowThreshold(String attribute,
                                                       OrganisationUnit organisationUnit,
                                                       Integer minNumberOfValuesToHave,
                                                       boolean storeError) {
        return Completable.defer(() -> {
            // Using local date. It's not worth it to make a system info call
            store.deleteExpired(new Date());

            Integer fillUpTo = getFillUpToValue(minNumberOfValuesToHave, attribute);

            String pattern = trackedEntityAttributeStore.selectByUid(attribute).pattern();
            int remainingValues = store.count(
                    attribute,
                    isOrgunitDependent(pattern) ? UidsHelper.getUidOrNull(organisationUnit) : null,
                    pattern);

            // If number of values is explicitly specified, we use that value as threshold.
            int minNumberToTryFill = minNumberOfValuesToHave == null ?
                    (int) (fillUpTo * FACTOR_TO_REFILL) : minNumberOfValuesToHave;

            if (remainingValues < minNumberToTryFill) {
                Integer numberToReserve = fillUpTo - remainingValues;

                return downloadValues(attribute, organisationUnit, numberToReserve, pattern, storeError);
            } else {
                return Completable.complete();
            }
        });
    }

    private Completable downloadValues(String trackedEntityAttributeUid,
                                       OrganisationUnit organisationUnit,
                                       Integer numberToReserve,
                                       String pattern,
                                       boolean storeError) {

        return Completable.fromAction(() -> {
            executor.executeD2Call(reservedValueQueryCallFactory.create(
                    TrackedEntityAttributeReservedValueQuery.create(trackedEntityAttributeUid, numberToReserve,
                            organisationUnit, pattern)), storeError);
        }).doOnComplete(() -> {
            if (pattern != null) {
                store.deleteIfOutdatedPattern(trackedEntityAttributeUid, pattern);
            }
        });
    }

    private List<OrganisationUnit> getOrgUnitsLinkedToAttribute(String attribute) {
        List<String> linkedProgramUids = programTrackedEntityAttributeStore.selectStringColumnsWhereClause(
                ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM,
                new WhereClauseBuilder().appendKeyStringValue(
                        ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                        attribute
                ).build());

        List<String> linkedOrgunitUids = organisationUnitProgramLinkStore.selectStringColumnsWhereClause(
                OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
                new WhereClauseBuilder().appendInKeyStringValues(
                        OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                        linkedProgramUids
                ).build());

        List<String> captureOrgunits = userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(
                OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);

        linkedOrgunitUids.retainAll(captureOrgunits);

        return organisationUnitStore.selectWhere(new WhereClauseBuilder().appendInKeyStringValues(
                IdentifiableColumns.UID,
                linkedOrgunitUids
        ).build());
    }

    private List<TrackedEntityAttribute> getGeneratedAttributes() {
        String whereClause = new WhereClauseBuilder()
                .appendKeyNumberValue(TrackedEntityAttributeTableInfo.Columns.GENERATED, 1).build();
        return trackedEntityAttributeStore.selectWhere(whereClause);
    }

    private OrganisationUnit getOrganisationUnit(String uid) {
        return uid == null ? null : organisationUnitStore.selectByUid(uid);
    }

    private boolean isOrgunitDependent(String pattern) {
        return pattern != null && pattern.contains("ORG_UNIT_CODE");
    }

    private Integer getFillUpToValue(Integer minNumberOfValuesToHave, String attribute) {
        if (minNumberOfValuesToHave == null) {
            ReservedValueSetting reservedValueSetting = reservedValueSettingStore.selectByUid(attribute);
            if (reservedValueSetting == null || reservedValueSetting.numberOfValuesToReserve() == null) {
                GeneralSettings generalSettings = generalSettingObjectRepository.blockingGet();
                if (generalSettings == null || generalSettings.reservedValues() == null) {
                    return FILL_UP_TO;
                } else {
                    return generalSettings.reservedValues();
                }
            } else {
                return reservedValueSetting.numberOfValuesToReserve();
            }
        } else {
            this.reservedValueSettingStore.updateOrInsert(ReservedValueSetting.builder()
                    .uid(attribute).numberOfValuesToReserve(minNumberOfValuesToHave).build());
            return minNumberOfValuesToHave;
        }
    }
}