/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.scopedaccess

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.internal.AccessGuard
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem
import org.hisp.dhis.android.core.category.CategoryComboCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.option.OptionCollectionRepository
import org.hisp.dhis.android.core.option.OptionSetCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.scopedaccess.internal.ScopeResolver
import org.hisp.dhis.android.core.scopedaccess.internal.ScopedAccessGuard
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCollectionRepository
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityQueryGrant
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntitySearchCollectionRepository
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo

/**
 * [D2] narrowed to a [D2DataScope].
 *
 * Every accessor returns a **real SDK repository**, already carrying the filters that express the
 * grant. Callers get the full fluent API — `by*()`, `orderBy*()`, `withChild()`, `uid()`, paging,
 * `blockingGet()` — at full granularity and with no wrapper indirection.
 *
 * ### Why the grant cannot be widened
 *
 * [RepositoryScope][org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope] filters are
 * append-only and copy-on-write: every `by*()` call routes through `RepositoryScopeHelper`, which
 * only ever does `filters + item`, and the scope itself is `protected`. No API removes, replaces or
 * resets a filter. So a caller holding a pre-narrowed repository can only narrow it further —
 * asking for a program outside the grant yields an empty result, not a wider one.
 *
 * Writes get no such protection from filters, because a create projection or value object carries
 * its own organisation unit and program regardless of the query. They are covered instead by an
 * [AccessGuard] carried on the same scope, checked at every write entry point.
 *
 * ### What is deliberately absent
 *
 * [D2] members with no safe restriction are not exposed at all, and this is a decision rather than
 * an oversight:
 *
 *  - `databaseAdapter()`, `httpServiceClient()` — raw database and network access;
 *  - `wipeModule()`, `maintenanceModule()` — destructive;
 *  - `dataStoreModule()` — privilege escalation, since host applications keep their own
 *    configuration there, including the configuration that authors scopes;
 *  - `userModule()` — credentials and account management;
 *  - `importModule()`, `metadataModule()`, `smsModule()`, `settingModule()` — sync and transport;
 *  - `analyticsModule()`, `relationshipModule()` — a free-form dimension DSL and object-graph
 *    traversal that can both reach outside a grant; deferred until they get their own design pass.
 *
 * Metadata *definitions* (option sets, category combos, attribute definitions) are exposed
 * unrestricted under [D2Capability.READ_METADATA]. The line drawn here is between metadata, which
 * describes the shape of the data, and the data itself, which is what the grant bounds.
 *
 * Obtain one with [D2.scopedTo]:
 *
 * ```kotlin
 * val sdk = d2.scopedTo(
 *     D2DataScope(
 *         programs = UidScope.of("IpHINAT79UW"),
 *         orgUnits = OrgUnitScope.of(listOf("O6uvpzGd5pu")),
 *         capabilities = setOf(D2Capability.READ_METADATA, D2Capability.READ_EVENT),
 *     ),
 * )
 * val recent = sdk.events()
 *     .byEventDate().after(lastWeek)
 *     .orderByEventDate(RepositoryScope.OrderByDirection.DESC)
 *     .blockingGet()
 * ```
 *
 * Derived parts of a grant (org unit descendants, the data elements of a data set) are resolved once
 * and cached for this instance's lifetime. Obtain a fresh `ScopedD2` after a metadata sync.
 */
@Suppress("TooManyFunctions")
class ScopedD2 internal constructor(
    private val d2: D2,
    /** The grant this instance enforces. Read-only; nothing here can widen it. */
    val scope: D2DataScope,
) {

    private val resolver = ScopeResolver(d2, scope)
    private val guard: AccessGuard = ScopedAccessGuard(scope, resolver)

    // ── Metadata ─────────────────────────────────────────────────────────────

    /** Programs in the grant. */
    fun programs(): ProgramCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        var repo = d2.programModule().programs()
        scope.programs.uidsOrNull()?.let { repo = repo.byUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Program stages belonging to programs in the grant. */
    fun programStages(): ProgramStageCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        var repo = d2.programModule().programStages()
        scope.programs.uidsOrNull()?.let { repo = repo.byProgramUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Data sets in the grant. */
    fun dataSets(): DataSetCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        var repo = d2.dataSetModule().dataSets()
        scope.dataSets.uidsOrNull()?.let { repo = repo.byUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Data elements reachable through the granted data sets, or all of them if unrestricted. */
    fun dataElements(): DataElementCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        var repo = d2.dataElementModule().dataElements()
        resolver.readableDataElements()?.let { repo = repo.byUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Tracked entity types in the grant. */
    fun trackedEntityTypes(): TrackedEntityTypeCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        var repo = d2.trackedEntityModule().trackedEntityTypes()
        scope.trackedEntityTypes.uidsOrNull()?.let { repo = repo.byUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Organisation units in the grant, with any hierarchy mode already expanded. */
    fun organisationUnits(): OrganisationUnitCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        var repo = d2.organisationUnitModule().organisationUnits()
        resolver.readableOrgUnits()?.let { repo = repo.byUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Tracked entity attribute *definitions*. Their values are scoped by [trackedEntityAttributeValues]. */
    fun trackedEntityAttributes(): TrackedEntityAttributeCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        return d2.trackedEntityModule().trackedEntityAttributes().guarded()
    }

    /** Option sets. Metadata, unrestricted under [D2Capability.READ_METADATA]. */
    fun optionSets(): OptionSetCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        return d2.optionModule().optionSets().guarded()
    }

    /** Options. Metadata, unrestricted under [D2Capability.READ_METADATA]. */
    fun options(): OptionCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        return d2.optionModule().options().guarded()
    }

    /** Category combos. Metadata, unrestricted under [D2Capability.READ_METADATA]. */
    fun categoryCombos(): CategoryComboCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        return d2.categoryModule().categoryCombos().guarded()
    }

    /** Category option combos. Metadata, unrestricted under [D2Capability.READ_METADATA]. */
    fun categoryOptionCombos(): CategoryOptionComboCollectionRepository {
        requireCapability(D2Capability.READ_METADATA)
        return d2.categoryModule().categoryOptionCombos().guarded()
    }

    // ── Tracker data ─────────────────────────────────────────────────────────

    /**
     * Tracked entity instances enrolled in a granted program and held in a granted organisation
     * unit.
     *
     * The program restriction is a sub-select through the enrollment table — a TEI has no program
     * column of its own — using the SDK's own `byProgramUids`.
     */
    fun trackedEntityInstances(): TrackedEntityInstanceCollectionRepository {
        requireCapability(D2Capability.READ_TRACKED_ENTITY)
        var repo = d2.trackedEntityModule().trackedEntityInstances()
        scope.programs.uidsOrNull()?.let { repo = repo.byProgramUids(it.toList()) }
        resolver.readableOrgUnits()?.let { repo = repo.byOrganisationUnitUid().`in`(it.toList()) }
        scope.trackedEntityTypes.uidsOrNull()?.let { repo = repo.byTrackedEntityType().`in`(it.toList()) }
        return repo.guarded()
    }

    /**
     * Attribute values of tracked entities inside the grant.
     *
     * This table has no program or organisation unit column, only a tracked entity reference, so the
     * restriction has to be a sub-select through the enrollment table. Without it a caller could
     * read the attribute values of any tracked entity on the device by UID.
     */
    fun trackedEntityAttributeValues(): TrackedEntityAttributeValueCollectionRepository {
        requireCapability(D2Capability.READ_TRACKED_ENTITY)
        val repo = d2.trackedEntityModule().trackedEntityAttributeValues()
        val programs = scope.programs.uidsOrNull() ?: return repo.guarded()
        return repo.guardedWith(enrolledInProgramsClause(programs))
    }

    /** Enrollments into granted programs, in granted organisation units. */
    fun enrollments(): EnrollmentCollectionRepository {
        requireCapability(D2Capability.READ_ENROLLMENT)
        var repo = d2.enrollmentModule().enrollments()
        scope.programs.uidsOrNull()?.let { repo = repo.byProgram().`in`(it.toList()) }
        resolver.readableOrgUnits()?.let { repo = repo.byOrganisationUnit().`in`(it.toList()) }
        return repo.guarded()
    }

    /** Events of granted programs, in granted organisation units. */
    fun events(): EventCollectionRepository {
        requireCapability(D2Capability.READ_EVENT)
        var repo = d2.eventModule().events()
        scope.programs.uidsOrNull()?.let { repo = repo.byProgramUid().`in`(it.toList()) }
        resolver.readableOrgUnits()?.let { repo = repo.byOrganisationUnitUid().`in`(it.toList()) }
        return repo.guarded()
    }

    /**
     * Data values of events inside the grant.
     *
     * Like attribute values this table only references its parent, so the program restriction is a
     * sub-select through the event table.
     */
    fun trackedEntityDataValues(): TrackedEntityDataValueCollectionRepository {
        requireCapability(D2Capability.READ_EVENT)
        val repo = d2.trackedEntityModule().trackedEntityDataValues()
        val programs = scope.programs.uidsOrNull() ?: return repo.guarded()
        return repo.guardedWith(eventInProgramsClause(programs))
    }

    /**
     * Tracked entity search, restricted to the grant and answered from the local database.
     *
     * Unlike the repositories above, this one's scope is a record of fields that `by*()` calls
     * replace rather than append to, so the grant is re-applied on every repository the fluent API
     * produces — see [TrackedEntityQueryGrant]. `onlineOnly()` and `onlineFirst()` are forced back
     * to offline: a server-side search is answered where none of these restrictions exist.
     */
    fun trackedEntitySearch(): TrackedEntitySearchCollectionRepository {
        requireCapability(D2Capability.SEARCH_TRACKED_ENTITY)
        return d2.trackedEntityModule().trackedEntitySearch().withGrant(
            TrackedEntityQueryGrant(
                programs = scope.programs.uidsOrNull(),
                orgUnits = resolver.readableOrgUnits(),
                trackedEntityTypes = scope.trackedEntityTypes.uidsOrNull(),
            ),
        )
    }

    // ── Aggregate data ───────────────────────────────────────────────────────

    /**
     * Data values for data elements of the granted data sets, in granted organisation units.
     *
     * `DataValue` has no data-set column, so the grant is resolved to the data elements those data
     * sets contain.
     */
    fun dataValues(): DataValueCollectionRepository {
        requireCapability(D2Capability.READ_DATA_VALUE)
        var repo = d2.dataValueModule().dataValues()
        resolver.readableDataElements()?.let { repo = repo.byDataElementUid().`in`(it.toList()) }
        resolver.readableOrgUnits()?.let { repo = repo.byOrganisationUnitUid().`in`(it.toList()) }
        return repo.guarded()
    }

    // ── Internals ────────────────────────────────────────────────────────────

    private fun requireCapability(capability: D2Capability) {
        if (!scope.has(capability)) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.SCOPE_VIOLATION)
                .errorDescription("This D2DataScope does not grant the $capability capability")
                .build()
        }
    }

    /** Installs the write guard, leaving the read filters exactly as the caller built them. */
    private fun <R : BaseRepository> BaseRepositoryImpl<R>.guarded(): R =
        withScope { it.toBuilder().accessGuard(guard).build() }

    /** Installs the write guard and one mandatory sub-select the public filters cannot express. */
    private fun <R : BaseRepository> BaseRepositoryImpl<R>.guardedWith(whereClause: String): R =
        withScope { current ->
            current.toBuilder()
                .complexFilters(current.complexFilters() + RepositoryScopeComplexFilterItem(whereClause))
                .accessGuard(guard)
                .build()
        }

    private fun enrolledInProgramsClause(programs: Set<String>): String =
        "${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} IN (" +
            "SELECT ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
            "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EnrollmentTableInfo.Columns.PROGRAM} IN ${sqlList(programs)})"

    private fun eventInProgramsClause(programs: Set<String>): String =
        "${TrackedEntityDataValueTableInfo.Columns.EVENT} IN (" +
            "SELECT ${IdentifiableColumns.UID} " +
            "FROM ${EventTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EventTableInfo.Columns.PROGRAM} IN ${sqlList(programs)})"

    /** DHIS2 UIDs are alphanumeric, but quotes are escaped anyway rather than trusted. */
    private fun sqlList(uids: Set<String>): String =
        uids.joinToString(prefix = "(", postfix = ")") { "'${it.replace("'", "''")}'" }
}
