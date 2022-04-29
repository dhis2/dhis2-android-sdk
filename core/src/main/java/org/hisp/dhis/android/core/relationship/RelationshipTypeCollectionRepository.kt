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
package org.hisp.dhis.android.core.relationship

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCollectionRepositoryHelper.availableForEnrollmentRawQuery
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCollectionRepositoryHelper.availableForEventRawQuery
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCollectionRepositoryHelper.availableForTrackedEntityInstanceRawQuery
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore

@Reusable
class RelationshipTypeCollectionRepository @Inject internal constructor(
    store: IdentifiableObjectStore<RelationshipType>,
    private val teiStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<RelationshipType>>,
    scope: RepositoryScope
) : ReadOnlyIdentifiableCollectionRepositoryImpl<RelationshipType, RelationshipTypeCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory<RelationshipTypeCollectionRepository>(scope) { s: RepositoryScope ->
        RelationshipTypeCollectionRepository(
            store, teiStore, enrollmentStore, eventStore,
            childrenAppenders, s
        )
    }
) {
    fun byBidirectional(): BooleanFilterConnector<RelationshipTypeCollectionRepository> {
        return cf.bool(RelationshipTypeTableInfo.Columns.BIDIRECTIONAL)
    }

    @JvmOverloads
    fun byConstraint(
        relationshipEntityType: RelationshipEntityType,
        relationshipEntityUid: String,
        relationshipConstraintType: RelationshipConstraintType? = null
    ): RelationshipTypeCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inTableWhere(
            RelationshipConstraintTableInfo.TABLE_INFO.name(),
            RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
            constraintClauseBuilder(relationshipEntityType, relationshipEntityUid).apply {
                if (relationshipConstraintType != null) {
                    appendKeyStringValue(
                        RelationshipConstraintTableInfo.Columns.CONSTRAINT_TYPE,
                        relationshipConstraintType
                    )
                }
            }
        )
    }

    /**
     * Filter RelationshipTypes by those that meets the requirements for this trackedEntityInstance:
     * - the TEI might be assigned to the FROM component
     * - or the TEI might be assigned to the TO component and the RelationshipType is bidirectional
     */
    fun byAvailableForTrackedEntityInstance(trackedEntityInstanceUid: String): RelationshipTypeCollectionRepository {
        val trackedEntityInstance = teiStore.selectByUid(trackedEntityInstanceUid)
        return cf.subQuery(IdentifiableColumns.UID).rawSubQuery(
            FilterItemOperator.IN,
            availableForTrackedEntityInstanceRawQuery(trackedEntityInstance)
        )
    }

    /**
     * Filter RelationshipTypes by those that meets the requirements for this enrollment:
     * - the enrollment might be assigned to the FROM component
     * - or the enrollment might be assigned to the TO component and the RelationshipType is bidirectional
     */
    fun byAvailableForEnrollment(enrollmentUid: String): RelationshipTypeCollectionRepository {
        val enrollment = enrollmentStore.selectByUid(enrollmentUid)
        return cf.subQuery(IdentifiableColumns.UID).rawSubQuery(
            FilterItemOperator.IN,
            availableForEnrollmentRawQuery(enrollment)
        )
    }

    /**
     * Filter RelationshipTypes by those that meets the requirements for this event:
     * - the event might be assigned to the FROM component
     * - or the event might be assigned to the TO component and the RelationshipType is bidirectional
     */
    fun byAvailableForEvent(eventUid: String): RelationshipTypeCollectionRepository {
        val event = eventStore.selectByUid(eventUid)
        return cf.subQuery(IdentifiableColumns.UID).rawSubQuery(
            FilterItemOperator.IN,
            availableForEventRawQuery(event)
        )
    }

    fun withConstraints(): RelationshipTypeCollectionRepository {
        return cf.withChild(RelationshipTypeFields.CONSTRAINTS)!!
    }

    private fun constraintClauseBuilder(
        relationshipEntityType: RelationshipEntityType,
        relationshipEntityUid: String
    ): WhereClauseBuilder {
        return WhereClauseBuilder()
            .appendKeyStringValue(
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_ENTITY, relationshipEntityType
            )
            .appendKeyStringValue(getRelationshipEntityColumn(relationshipEntityType), relationshipEntityUid)
    }

    private fun getRelationshipEntityColumn(relationshipEntityType: RelationshipEntityType): String {
        return when (relationshipEntityType) {
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE ->
                RelationshipConstraintTableInfo.Columns.TRACKED_ENTITY_TYPE
            RelationshipEntityType.PROGRAM_INSTANCE ->
                RelationshipConstraintTableInfo.Columns.PROGRAM
            RelationshipEntityType.PROGRAM_STAGE_INSTANCE ->
                RelationshipConstraintTableInfo.Columns.PROGRAM_STAGE
        }
    }
}
