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
package org.hisp.dhis.android.core.relationship;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeFields;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class RelationshipTypeCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<RelationshipType, RelationshipTypeCollectionRepository> {

    @Inject
    RelationshipTypeCollectionRepository(final IdentifiableObjectStore<RelationshipType> store,
                                         final Map<String, ChildrenAppender<RelationshipType>> childrenAppenders,
                                         final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new RelationshipTypeCollectionRepository(store, childrenAppenders, s)));
    }

    public RelationshipTypeCollectionRepository byConstraint(@NonNull RelationshipEntityType relationshipEntityType,
                                                             @NonNull String relationshipEntityUid) {
        return cf.subQuery(IdentifiableColumns.UID).inTableWhere(
                RelationshipConstraintTableInfo.TABLE_INFO.name(),
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
                constraintClauseBuilder(relationshipEntityType, relationshipEntityUid));
    }

    public RelationshipTypeCollectionRepository byConstraint(
            @NonNull RelationshipEntityType relationshipEntityType,
            @NonNull String relationshipEntityUid,
            @NonNull RelationshipConstraintType relationshipConstraintType) {
        return cf.subQuery(IdentifiableColumns.UID).inTableWhere(
                RelationshipConstraintTableInfo.TABLE_INFO.name(),
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
                constraintClauseBuilder(relationshipEntityType, relationshipEntityUid).appendKeyStringValue(
                        RelationshipConstraintTableInfo.Columns.CONSTRAINT_TYPE, relationshipConstraintType));
    }

    public RelationshipTypeCollectionRepository withConstraints() {
        return cf.withChild(RelationshipTypeFields.CONSTRAINTS);
    }

    private WhereClauseBuilder constraintClauseBuilder(RelationshipEntityType relationshipEntityType,
                                                       String relationshipEntityUid) {
        return new WhereClauseBuilder()
                .appendKeyStringValue(
                        RelationshipConstraintTableInfo.Columns.RELATIONSHIP_ENTITY, relationshipEntityType)
                .appendKeyStringValue(getRelationshipEntityColumn(relationshipEntityType), relationshipEntityUid);
    }
    
    private String getRelationshipEntityColumn(@NonNull RelationshipEntityType relationshipEntityType) {
        switch (relationshipEntityType) {
            case TRACKED_ENTITY_INSTANCE:
                return RelationshipConstraintTableInfo.Columns.TRACKED_ENTITY_TYPE;
            case PROGRAM_INSTANCE:
                return RelationshipConstraintTableInfo.Columns.PROGRAM;
            case PROGRAM_STAGE_INSTANCE:
                return RelationshipConstraintTableInfo.Columns.PROGRAM_STAGE;
            default:
                return null;
        }
    }
}
