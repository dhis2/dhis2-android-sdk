/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitProgramLinkStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.settings.GeneralSettingObjectRepository
import org.hisp.dhis.android.core.trackedentity.internal.ReservedValueSettingStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueEndpointCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.koin.core.annotation.Singleton
import java.util.Date

@SuppressWarnings("LongParameterList", "TooManyFunctions")
@Singleton
class TrackedEntityAttributeReservedValueManager internal constructor(
    private val store: TrackedEntityAttributeReservedValueStore,
    private val organisationUnitStore: OrganisationUnitStore,
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore,
    private val organisationUnitProgramLinkStore: OrganisationUnitProgramLinkStore,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val generalSettingObjectRepository: GeneralSettingObjectRepository,
    private val reservedValueSettingStore: ReservedValueSettingStore,
    private val reservedValueQueryCallFactory: TrackedEntityAttributeReservedValueEndpointCallFactory,
) {
    private val d2ProgressManager = D2ProgressManager(null)

    /**
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid Organisation unit uid
     * @return Value of tracked entity attribute
     * @see .getValue
     */
    fun blockingGetValue(attributeUid: String, organisationUnitUid: String): String {
        return runBlocking {
            getValueCoroutines(attributeUid, organisationUnitUid)
        }
    }

    /**
     * Get a reserved value and remove it from database. If the number of available values is below a threshold
     * (default [.FILL_UP_TO] * [.FACTOR_TO_REFILL]) it tries to download before returning a value.
     *
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid Organisation unit uid
     * @return Single with value of tracked entity attribute
     */

    fun getValue(attributeUid: String, organisationUnitUid: String): Single<String> {
        return rxSingle { getValueCoroutines(attributeUid, organisationUnitUid) }
    }

    private suspend fun getValueCoroutines(attributeUid: String, organisationUnitUid: String): String {
        downloadValuesIfBelowThreshold(attributeUid, getOrganisationUnit(organisationUnitUid), null, false)

        val pattern = trackedEntityAttributeStore.selectByUid(attributeUid)!!.pattern()
        val attributeOrgUnit = if (isOrgUnitDependent(pattern)) organisationUnitUid else null

        return store.popOne(attributeUid, attributeOrgUnit)?.value() ?: throw D2Error.builder()
            .errorCode(D2ErrorCode.NO_RESERVED_VALUES).errorDescription("There are no reserved values")
            .errorComponent(D2ErrorComponent.Database).build()
    }

    /**
     * @param attributeUid           Attribute uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @see .downloadReservedValues
     */
    fun blockingDownloadReservedValues(
        attributeUid: String,
        numberOfValuesToFillUp: Int?,
    ) {
        runBlocking { downloadReservedValuesFlow(attributeUid, numberOfValuesToFillUp).collect() }
    }

    /**
     * Download of TrackedEntityInstance reserved values. The number of reserved values is filled up to the
     * numberOfValuesToFillUp. If not defined, it defaults to [.FILL_UP_TO].
     * <br></br><br></br>
     * The download is only triggered for this attribute passed as parameter.
     * <br></br><br></br>
     * If the attribute pattern is dependent on OrganisationUnit code (that is, it contains "ORG_UNIT_CODE"), it
     * reserves values for each orgUnit assigned to the programs with this attribute. It applies the limit
     * per orgUnit. Otherwise the limit is applied per attribute.
     *
     * @param attributeUid           Attribute uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return An Observable that notifies about the progress.
     */

    fun downloadReservedValues(
        attributeUid: String,
        numberOfValuesToFillUp: Int?,
    ): Observable<D2Progress> {
        return downloadReservedValuesFlow(attributeUid, numberOfValuesToFillUp).asObservable()
    }

    private fun downloadReservedValuesFlow(
        attributeUid: String,
        numberOfValuesToFillUp: Int?,
    ) = flow {
        emitAll(downloadValuesForOrgUnits(attributeUid, numberOfValuesToFillUp))
    }

    /**
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @see .downloadAllReservedValues
     */
    fun blockingDownloadAllReservedValues(numberOfValuesToFillUp: Int?) {
        runBlocking {
            downloadAllReservedValuesFlow(numberOfValuesToFillUp).collect()
        }
    }

    /**
     * Downloads reserved values for all the trackedEntityAttributeValues of type "generated", that is, it applies
     * [.downloadReservedValues] for every generated attribute.
     *
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return An Observable that notifies about the progress.
     */

    fun downloadAllReservedValues(numberOfValuesToFillUp: Int?): Observable<D2Progress> {
        return downloadAllReservedValuesFlow(numberOfValuesToFillUp).asObservable()
    }

    private fun downloadAllReservedValuesFlow(
        numberOfValuesToFillUp: Int?,
    ) = flow {
        val flows = generatedAttributes().map { attribute ->
            downloadValuesForOrgUnits(attribute.uid(), numberOfValuesToFillUp)
        }
        emitAll(flows.merge())
    }

    /**
     * Get the count of the reserved values by attribute. If a organisation unit uid is inserted as parameter the method
     * will return the count of the reserved values by attribute and organisation unit.
     *
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid An optional organisation unit uid
     * @return Single with the reserved value count by attribute or by attribute and organisation unit.
     */
    fun count(attributeUid: String, organisationUnitUid: String?): Single<Int> =
        rxSingle { countInternal(attributeUid, organisationUnitUid) }

    /**
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid An optional organisation unit uid
     * @return The reserved value count by attribute or by attribute and organisation unit.
     * @see .count
     */
    fun blockingCount(attributeUid: String, organisationUnitUid: String?): Int {
        return runBlocking { countInternal(attributeUid, organisationUnitUid) }
    }

    private suspend fun countInternal(attributeUid: String, organisationUnitUid: String?): Int {
        return store.count(attributeUid, organisationUnitUid, null)
    }

    /**
     * Generate a list of reserved value summaries from the existing tracked entity attribute reserved values in the DB.
     *
     * @return Single with a list of the reserved value summaries
     */
    fun getReservedValueSummaries(): Single<List<ReservedValueSummary>> {
        return rxSingle { getReservedValueSummariesInternal() }
    }

    /**
     * @return List of the reserved value summaries
     * @see .getReservedValueSummaries
     */
    fun blockingGetReservedValueSummaries(): List<ReservedValueSummary> {
        return runBlocking { getReservedValueSummariesInternal() }
    }

    private suspend fun getReservedValueSummariesInternal(): List<ReservedValueSummary> {
        val whereClause =
            WhereClauseBuilder().appendKeyNumberValue(TrackedEntityAttributeTableInfo.Columns.GENERATED, 1).build()
        val orderByClause = OrderByClauseBuilder.orderByFromItems(
            listOf(
                RepositoryScopeOrderByItem.builder().column(IdentifiableColumns.DISPLAY_NAME)
                    .direction(RepositoryScope.OrderByDirection.ASC).build(),
            ),
            CoreColumns.ID,
        )
        val trackedEntityAttributes = trackedEntityAttributeStore.selectWhere(whereClause, orderByClause)
        val reservedValueSummaries: MutableList<ReservedValueSummary> = ArrayList()
        for (trackedEntityAttribute in trackedEntityAttributes) {
            val builder = ReservedValueSummary.builder().trackedEntityAttribute(trackedEntityAttribute)
            if (isOrgUnitDependent(trackedEntityAttribute.pattern())) {
                val organisationUnits = getOrgUnitsWithCodeLinkedToAttributes(trackedEntityAttribute.uid())
                for (organisationUnit in organisationUnits) {
                    builder.organisationUnit(organisationUnit)
                        .count(blockingCount(trackedEntityAttribute.uid(), organisationUnit.uid()))
                        .numberOfValuesToFillUp(
                            getFillUpToValue(
                                null,
                                trackedEntityAttribute.uid(),
                            ),
                        )
                    reservedValueSummaries.add(builder.build())
                }
            } else {
                builder.count(blockingCount(trackedEntityAttribute.uid(), null))
                    .numberOfValuesToFillUp(getFillUpToValue(null, trackedEntityAttribute.uid()))
                reservedValueSummaries.add(builder.build())
            }
        }
        return reservedValueSummaries
    }

    private fun increaseProgress(): D2Progress {
        return d2ProgressManager.increaseProgress(
            TrackedEntityAttributeReservedValue::class.java,
            false,
        )
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun downloadValuesForOrgUnits(
        attribute: String,
        numberOfValuesToFillUp: Int?,
    ): Flow<D2Progress> = flow {
        val pattern = trackedEntityAttributeStore.selectByUid(attribute)!!.pattern()

        if (isOrgUnitDependent(pattern)) {
            val organisationUnits = getOrgUnitsWithCodeLinkedToAttributes(attribute)

            for (organisationUnit in organisationUnits) {
                try {
                    downloadValuesIfBelowThreshold(
                        attribute,
                        organisationUnit,
                        numberOfValuesToFillUp,
                        true,
                    )
                } catch (e: Exception) {
                    Log.e(
                        this::class.java.simpleName,
                        "Error downloading reserved values for attribute: $attribute and " +
                                "org. unit: ${organisationUnit.uid()}",
                        e,
                    )
                }
                emit(increaseProgress())
            }
        } else {
            try {
                downloadValuesIfBelowThreshold(attribute, null, numberOfValuesToFillUp, true)
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, "Error downloading reserved values for attribute: $attribute", e)
            }
            emit(increaseProgress())
        }
    }

    private suspend fun downloadValuesIfBelowThreshold(
        attribute: String,
        organisationUnit: OrganisationUnit?,
        minNumberOfValuesToHave: Int?,
        storeError: Boolean,
    ) = coroutineScope {
        try {
            // Using local date. It's not worth it to make a system info call
            store.deleteExpired(Date())
            val fillUpTo = getFillUpToValue(minNumberOfValuesToHave, attribute)
            val pattern = trackedEntityAttributeStore.selectByUid(attribute)!!.pattern()
            val remainingValues = store.count(
                attribute,
                if (isOrgUnitDependent(pattern)) getUidOrNull(organisationUnit) else null,
                pattern,
            )

            // If number of values is explicitly specified, we use that value as threshold.
            val minNumberToTryFill = minNumberOfValuesToHave ?: (fillUpTo!! * FACTOR_TO_REFILL).toInt()

            if (remainingValues < minNumberToTryFill) {
                val numberToReserve = fillUpTo!! - remainingValues
                downloadValues(
                    attribute,
                    organisationUnit,
                    numberToReserve,
                    pattern,
                    storeError,
                )
            }
        } catch (ignored: Exception) {
            // Ignored
        }
    }

    private suspend fun downloadValues(
        trackedEntityAttributeUid: String,
        organisationUnit: OrganisationUnit?,
        numberToReserve: Int,
        pattern: String?,
        storeError: Boolean,
    ) {
        reservedValueQueryCallFactory.create(
            TrackedEntityAttributeReservedValueQuery(
                trackedEntityAttributeUid,
                numberToReserve,
                organisationUnit,
                pattern,
                storeError,
            ),
        )

        if (pattern != null) {
            store.deleteIfOutdatedPattern(trackedEntityAttributeUid, pattern)
        }
    }

    private suspend fun getOrgUnitsWithCodeLinkedToAttributes(attribute: String): List<OrganisationUnit> {
        val linkedProgramUids = programTrackedEntityAttributeStore.selectStringColumnsWhereClause(
            ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM,
            WhereClauseBuilder().appendKeyStringValue(
                ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                attribute,
            ).build(),
        )
        val linkedOrgUnitUids = organisationUnitProgramLinkStore.selectStringColumnsWhereClause(
            OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
            WhereClauseBuilder().appendInKeyStringValues(
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                linkedProgramUids,
            ).build(),
        )
        val captureOrgUnits = userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(
            OrganisationUnit.Scope.SCOPE_DATA_CAPTURE,
        )
        linkedOrgUnitUids.toMutableList().retainAll(captureOrgUnits)
        return organisationUnitStore.selectWhere(
            WhereClauseBuilder().appendInKeyStringValues(
                IdentifiableColumns.UID,
                linkedOrgUnitUids,
            ).build(),
        ).filter { it.code() != null }
    }

    private suspend fun generatedAttributes(): List<TrackedEntityAttribute> {
        val whereClause = WhereClauseBuilder()
            .appendKeyNumberValue(TrackedEntityAttributeTableInfo.Columns.GENERATED, 1)
            .build()
        return trackedEntityAttributeStore.selectWhere(whereClause)
    }

    private suspend fun getOrganisationUnit(uid: String?): OrganisationUnit? {
        return if (uid == null) null else organisationUnitStore.selectByUid(uid)
    }

    private fun isOrgUnitDependent(pattern: String?): Boolean {
        return pattern != null && pattern.contains("ORG_UNIT_CODE")
    }

    private suspend fun getFillUpToValue(minNumberOfValuesToHave: Int?, attribute: String): Int? {
        return if (minNumberOfValuesToHave == null) {
            val reservedValueSetting = reservedValueSettingStore.selectByUid(attribute)
            if (reservedValueSetting?.numberOfValuesToReserve() == null) {
                val generalSettings = generalSettingObjectRepository.blockingGet()
                if (generalSettings?.reservedValues() == null) {
                    FILL_UP_TO
                } else {
                    generalSettings.reservedValues()
                }
            } else {
                reservedValueSetting.numberOfValuesToReserve()
            }
        } else {
            reservedValueSettingStore.updateOrInsert(
                ReservedValueSetting.builder().uid(attribute).numberOfValuesToReserve(minNumberOfValuesToHave).build(),
            )
            minNumberOfValuesToHave
        }
    }

    companion object {
        private const val FILL_UP_TO = 100
        private const val FACTOR_TO_REFILL = 0.5
    }
}
