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

import dagger.Reusable
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory
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
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@SuppressWarnings("LongParameterList", "TooManyFunctions")
@Reusable
class TrackedEntityAttributeReservedValueManager @Inject internal constructor(
    private val store: TrackedEntityAttributeReservedValueStore,
    private val organisationUnitStore: OrganisationUnitStore,
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore,
    private val organisationUnitProgramLinkStore: OrganisationUnitProgramLinkStore,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val generalSettingObjectRepository: GeneralSettingObjectRepository,
    private val reservedValueSettingStore: ReservedValueSettingStore,
    private val executor: D2CallExecutor,
    private val reservedValueQueryCallFactory: QueryCallFactory<TrackedEntityAttributeReservedValue,
        TrackedEntityAttributeReservedValueQuery>
) {
    private val d2ProgressManager = D2ProgressManager(null)

    /**
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid Organisation unit uid
     * @return Value of tracked entity attribute
     * @see .getValue
     */
    suspend fun blockingGetValue(attributeUid: String, organisationUnitUid: String): String {
        return getValue(attributeUid, organisationUnitUid)!!
    }

    /**
     * Get a reserved value and remove it from database. If the number of available values is below a threshold
     * (default [.FILL_UP_TO] * [.FACTOR_TO_REFILL]) it tries to download before returning a value.
     *
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid Organisation unit uid
     * @return Single with value of tracked entity attribute
     */

    suspend fun getValue(attributeUid: String, organisationUnitUid: String): String? {

        downloadValuesIfBelowThreshold(attributeUid, getOrganisationUnit(organisationUnitUid), null, false)

        val pattern = trackedEntityAttributeStore.selectByUid(attributeUid)!!.pattern()
        val attributeOrgunit = if (isOrgunitDependent(pattern)) organisationUnitUid else null
        val reservedValue = store.popOne(attributeUid, attributeOrgunit)
            ?: throw D2Error.builder()
                .errorCode(D2ErrorCode.NO_RESERVED_VALUES)
                .errorDescription("There are no reserved values")
                .errorComponent(D2ErrorComponent.Database).build()

        return reservedValue.value()
    }

    /**
     * @param attributeUid           Attribute uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @see .downloadReservedValues
     */
    suspend fun blockingDownloadReservedValues(
        attributeUid: String,
        numberOfValuesToFillUp: Int?
    ) {
        downloadReservedValues(attributeUid, numberOfValuesToFillUp).collect()
    }

    /**
     * Download of TrackedEntityInstance reserved values. The number of reserved values is filled up to the
     * numberOfValuesToFillUp. If not defined, it defaults to [.FILL_UP_TO].
     * <br></br><br></br>
     * The download is only triggered for this attribute passed as parameter.
     * <br></br><br></br>
     * If the attribute pattern is dependent on OrganisationUnit code (that is, it contains "ORG_UNIT_CODE"), it
     * reserves values for each orgunit assigned to the programs with this attribute. It applies the limit
     * per orgunit. Otherwise the limit is applied per attribute.
     *
     * @param attributeUid           Attribute uid
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return An Observable that notifies about the progress.
     */
    private fun downloadReservedValues(
        attributeUid: String,
        numberOfValuesToFillUp: Int?
    ) = flow<D2Progress> {
        downloadValuesForOrgUnits(attributeUid, numberOfValuesToFillUp)
    }

    /**
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @see .downloadAllReservedValues
     */
    suspend fun blockingDownloadAllReservedValues(numberOfValuesToFillUp: Int?) {
        downloadAllReservedValues(numberOfValuesToFillUp)
    }

    /**
     * Downloads reserved values for all the trackedEntityAttributeValues of type "generated", that is, it applies
     * [.downloadReservedValues] for every generated attribute.
     *
     * @param numberOfValuesToFillUp An optional maximum number of values to reserve
     * @return An Observable that notifies about the progress.
     */
    private suspend fun downloadAllReservedValues(
        numberOfValuesToFillUp: Int?
    ): List<D2Progress> = coroutineScope {
        val observables = generatedAttributes.map { attribute ->
            async { downloadValuesForOrgUnits(attribute.uid(), numberOfValuesToFillUp) }
        }

        observables.awaitAll().flatten()
    }

    /**
     * Get the count of the reserved values by attribute. If a organisation unit uid is inserted as parameter the method
     * will return the count of the reserved values by attribute and organisation unit.
     *
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid An optional organisation unit uid
     * @return Single with the reserved value count by attribute or by attribute and organisation unit.
     */
    fun count(attributeUid: String, organisationUnitUid: String?): Int =
        store.count(attributeUid, organisationUnitUid, null)

    /**
     * @param attributeUid        Attribute uid
     * @param organisationUnitUid An optional organisation unit uid
     * @return The reserved value count by attribute or by attribute and organisation unit.
     * @see .count
     */
    fun blockingCount(attributeUid: String, organisationUnitUid: String?): Int =
        count(attributeUid, organisationUnitUid)

    /**
     * Generate a list of reserved value summaries from the existing tracked entity attribute reserved values in the DB.
     *
     * @return Single with a list of the reserved value summaries
     */
    val reservedValueSummaries: Flow<List<ReservedValueSummary>> = flow {
        emit(blockingGetReservedValueSummaries())
    }

    /**
     * @return List of the reserved value summaries
     * @see .getReservedValueSummaries
     */
    suspend fun blockingGetReservedValueSummaries(): List<ReservedValueSummary> {
        val whereClause = WhereClauseBuilder()
            .appendKeyNumberValue(TrackedEntityAttributeTableInfo.Columns.GENERATED, 1).build()
        val orderByClause = OrderByClauseBuilder.orderByFromItems(
            listOf(
                RepositoryScopeOrderByItem.builder()
                    .column(IdentifiableColumns.DISPLAY_NAME)
                    .direction(RepositoryScope.OrderByDirection.ASC).build()
            ),
            CoreColumns.ID
        )
        val trackedEntityAttributes =
            trackedEntityAttributeStore.selectWhere(whereClause, orderByClause)
        val reservedValueSummaries: MutableList<ReservedValueSummary> = ArrayList()
        for (trackedEntityAttribute in trackedEntityAttributes) {
            val builder = ReservedValueSummary.builder()
                .trackedEntityAttribute(trackedEntityAttribute)
            if (isOrgunitDependent(trackedEntityAttribute.pattern())) {
                val organisationUnits = getOrgUnitsLinkedToAttribute(trackedEntityAttribute.uid())
                for (organisationUnit in organisationUnits) {
                    builder.organisationUnit(organisationUnit)
                        .count(blockingCount(trackedEntityAttribute.uid(), organisationUnit.uid()))
                        .numberOfValuesToFillUp(
                            getFillUpToValue(
                                null,
                                trackedEntityAttribute.uid()
                            )
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
            TrackedEntityAttributeReservedValue::class.java, false
        )
    }

    private suspend fun downloadValuesForOrgUnits(
        attribute: String,
        numberOfValuesToFillUp: Int?
    ): List<D2Progress> = coroutineScope {
        val pattern = trackedEntityAttributeStore.selectByUid(attribute)!!.pattern()

        if (isOrgunitDependent(pattern)) {
            val organisationUnits = getOrgUnitsLinkedToAttribute(attribute)
            val progressList = mutableListOf<D2Progress>()

            for (organisationUnit in organisationUnits) {
                downloadValuesIfBelowThreshold(
                    attribute,
                    organisationUnit,
                    numberOfValuesToFillUp,
                    true
                )
                progressList.add(increaseProgress())
            }

            progressList
        } else {
            downloadValuesIfBelowThreshold(attribute, null, numberOfValuesToFillUp, true)

            listOf(increaseProgress())
        }
    }

    private suspend fun downloadValuesIfBelowThreshold(
        attribute: String,
        organisationUnit: OrganisationUnit?,
        minNumberOfValuesToHave: Int?,
        storeError: Boolean
    ): Unit = coroutineScope {
        // Using local date. It's not worth it to make a system info call
        store.deleteExpired(Date())
        val fillUpTo = getFillUpToValue(minNumberOfValuesToHave, attribute)
        val pattern = trackedEntityAttributeStore.selectByUid(attribute)!!.pattern()
        val remainingValues = store.count(
            attribute,
            if (isOrgunitDependent(pattern)) getUidOrNull(organisationUnit) else null,
            pattern
        )

        // If number of values is explicitly specified, we use that value as threshold.
        val minNumberToTryFill = minNumberOfValuesToHave
            ?: (fillUpTo!! * FACTOR_TO_REFILL).toInt()

        if (remainingValues < minNumberToTryFill) {
            val numberToReserve = fillUpTo!! - remainingValues
            downloadValues(
                attribute,
                organisationUnit,
                numberToReserve,
                pattern,
                storeError
            )
        }
    }

    suspend fun downloadValues(
        trackedEntityAttributeUid: String,
        organisationUnit: OrganisationUnit?,
        numberToReserve: Int,
        pattern: String?,
        storeError: Boolean
    ) {
        return suspendCoroutine {
            executor.executeD2Call(
                reservedValueQueryCallFactory.create(
                    TrackedEntityAttributeReservedValueQuery.create(
                        trackedEntityAttributeUid, numberToReserve,
                        organisationUnit, pattern
                    )
                ),
                storeError
            )

            if (pattern != null) {
                store.deleteIfOutdatedPattern(trackedEntityAttributeUid, pattern)
            }
        }
    }

    private fun getOrgUnitsLinkedToAttribute(attribute: String): List<OrganisationUnit> {
        val linkedProgramUids = programTrackedEntityAttributeStore.selectStringColumnsWhereClause(
            ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM,
            WhereClauseBuilder().appendKeyStringValue(
                ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                attribute
            ).build()
        )
        val linkedOrgunitUids = organisationUnitProgramLinkStore.selectStringColumnsWhereClause(
            OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
            WhereClauseBuilder().appendInKeyStringValues(
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                linkedProgramUids
            ).build()
        )
        val captureOrgunits = userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(
            OrganisationUnit.Scope.SCOPE_DATA_CAPTURE
        )
        linkedOrgunitUids.toMutableList().retainAll(captureOrgunits)
        return organisationUnitStore.selectWhere(
            WhereClauseBuilder().appendInKeyStringValues(
                IdentifiableColumns.UID,
                linkedOrgunitUids
            ).build()
        )
    }

    private val generatedAttributes: List<TrackedEntityAttribute>
        private get() {
            val whereClause = WhereClauseBuilder()
                .appendKeyNumberValue(TrackedEntityAttributeTableInfo.Columns.GENERATED, 1).build()
            return trackedEntityAttributeStore.selectWhere(whereClause)
        }

    private fun getOrganisationUnit(uid: String?): OrganisationUnit? {
        return if (uid == null) null else organisationUnitStore.selectByUid(uid)
    }

    private fun isOrgunitDependent(pattern: String?): Boolean {
        return pattern != null && pattern.contains("ORG_UNIT_CODE")
    }

    private fun getFillUpToValue(minNumberOfValuesToHave: Int?, attribute: String): Int? {
        return if (minNumberOfValuesToHave == null) {
            val reservedValueSetting = reservedValueSettingStore.selectByUid(attribute)
            if (reservedValueSetting == null || reservedValueSetting.numberOfValuesToReserve() == null) {
                val generalSettings = generalSettingObjectRepository.blockingGet()
                if (generalSettings == null || generalSettings.reservedValues() == null) {
                    FILL_UP_TO
                } else {
                    generalSettings.reservedValues()
                }
            } else {
                reservedValueSetting.numberOfValuesToReserve()
            }
        } else {
            reservedValueSettingStore.updateOrInsert(
                ReservedValueSetting.builder()
                    .uid(attribute).numberOfValuesToReserve(minNumberOfValuesToHave).build()
            )
            minNumberOfValuesToHave
        }
    }

    companion object {
        private const val FILL_UP_TO = 100
        private const val FACTOR_TO_REFILL = 0.5
    }
}
