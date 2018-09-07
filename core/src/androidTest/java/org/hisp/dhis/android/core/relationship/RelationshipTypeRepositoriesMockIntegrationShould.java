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
package org.hisp.dhis.android.core.relationship;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_1;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_2;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_2;

@RunWith(AndroidJUnit4.class)
public class RelationshipTypeRepositoriesMockIntegrationShould extends AbsStoreTestCase {

    private Map<String, RelationshipType> typeMap;
    private ReadOnlyIdentifiableCollectionRepository<RelationshipType> relationshipTypeCollectionRepository;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        SyncHandler<RelationshipType> handler = RelationshipTypeHandler.create(databaseAdapter());

        handler.handle(RELATIONSHIP_TYPE_1);
        handler.handle(RELATIONSHIP_TYPE_2);

        typeMap = RelationshipTypeSamples.typeMap();

        this.relationshipTypeCollectionRepository = RelationshipTypeCollectionRepository.create(databaseAdapter());
    }

    @Test
    public void get_all_relationship_types_without_children_when_calling_get_set() {
        Set<RelationshipType> types = relationshipTypeCollectionRepository.getSet();
        assertThat(types.size()).isEqualTo(2);

        for (RelationshipType targetType: types) {
            RelationshipType referenceType = typeMap.get(targetType.uid());
            assertTypesWithoutConstraints(targetType, referenceType);
        }
    }

    @Test
    public void get_all_relationship_types_with_children_when_calling_get_set_with_children() {
        Set<RelationshipType> types = relationshipTypeCollectionRepository.getSetWithAllChildren();
        assertThat(types.size()).isEqualTo(2);

        for (RelationshipType targetType: types) {
            RelationshipType referenceType = typeMap.get(targetType.uid());
            assertTypesWithConstraints(targetType, referenceType);
        }
    }

    @Test
    public void get_relationship_1_from_object_repository_without_children() {
        ReadOnlyObjectRepository<RelationshipType> type1Repository
                = relationshipTypeCollectionRepository.uid(RELATIONSHIP_TYPE_UID_1);
        RelationshipType typeFromRepository = type1Repository.get();
        assertTypesWithoutConstraints(typeFromRepository, RELATIONSHIP_TYPE_1);
    }

    @Test
    public void get_relationship_2_from_object_repository_without_children() {
        ReadOnlyObjectRepository<RelationshipType> type1Repository
                = relationshipTypeCollectionRepository.uid(RELATIONSHIP_TYPE_UID_2);
        RelationshipType typeFromRepository = type1Repository.get();
        assertTypesWithoutConstraints(typeFromRepository, RELATIONSHIP_TYPE_2);
    }

    @Test
    public void get_relationship_1_from_object_repository_with_children() {
        ReadOnlyObjectRepository<RelationshipType> type1Repository
                = relationshipTypeCollectionRepository.uid(RELATIONSHIP_TYPE_UID_1);
        RelationshipType typeFromRepository = type1Repository.getWithAllChildren();
        assertTypesWithConstraints(typeFromRepository, RELATIONSHIP_TYPE_1);
    }

    @Test
    public void get_relationship_2_from_object_repository_with_children() {
        ReadOnlyObjectRepository<RelationshipType> type1Repository
                = relationshipTypeCollectionRepository.uid(RELATIONSHIP_TYPE_UID_2);
        RelationshipType typeFromRepository = type1Repository.getWithAllChildren();
        assertTypesWithConstraints(typeFromRepository, RELATIONSHIP_TYPE_2);
    }

    private void assertTypesWithoutConstraints(RelationshipType target, RelationshipType reference) {
        assertThat(target.uid()).isEqualTo(reference.uid());
        assertThat(target.fromConstraint()).isNull();
        assertThat(target.toConstraint()).isNull();
    }

    private void assertTypesWithConstraints(RelationshipType targetWithId, RelationshipType reference) {
        RelationshipType targetWithoutId = targetWithId.toBuilder().id(null).build();
        assertThat(targetWithoutId).isEqualTo(reference);
    }
}
