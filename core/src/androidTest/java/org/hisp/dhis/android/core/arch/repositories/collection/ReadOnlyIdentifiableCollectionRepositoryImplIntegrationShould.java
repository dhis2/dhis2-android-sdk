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

package org.hisp.dhis.android.core.arch.repositories.collection;

import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyOneObjectRepositoryFinalImpl;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityTypeSamples;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipEntityType;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.arch.repositories.collection.RelationshipTypeAsserts.assertTypesWithoutConstraints;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_1;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_2;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_3;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_2;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.TET_FOR_RELATIONSHIP_3_UID;

@RunWith(D2JunitRunner.class)
public class ReadOnlyIdentifiableCollectionRepositoryImplIntegrationShould extends BaseMockIntegrationTestEmptyDispatcher {

    private static RelationshipTypeCollectionRepository relationshipTypeCollectionRepository;

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseMockIntegrationTestEmptyDispatcher.setUpClass();

        Handler<RelationshipType> handler = objects.d2DIComponent.relationshipTypeHandler();
        Handler<TrackedEntityType> tetHandler = objects.d2DIComponent.trackedEntityTypeHandler();

        tetHandler.handle(TrackedEntityTypeSamples.get());
        handler.handle(RELATIONSHIP_TYPE_1);
        handler.handle(RELATIONSHIP_TYPE_2);
        handler.handle(RELATIONSHIP_TYPE_3);

        relationshipTypeCollectionRepository = d2.relationshipModule().relationshipTypes();
    }

    @Test
    public void get_relationship_1_from_object_repository_without_children() {
        ReadOnlyOneObjectRepositoryFinalImpl<RelationshipType> type1Repository
                = relationshipTypeCollectionRepository.uid(RELATIONSHIP_TYPE_UID_1);
        RelationshipType typeFromRepository = type1Repository.blockingGet();
        assertTypesWithoutConstraints(typeFromRepository, RELATIONSHIP_TYPE_1);
    }

    @Test
    public void get_relationship_by_from_tracked_entity_type() {
        List<RelationshipType> typesFromRepository = relationshipTypeCollectionRepository
                .byConstraint(RelationshipEntityType.TRACKED_ENTITY_INSTANCE, TET_FOR_RELATIONSHIP_3_UID)
                .withConstraints()
                .blockingGet();

        assertThat(typesFromRepository.size()).isEqualTo(2);
    }

    @Test
    public void get_relationship_by_from_tracked_entity_type_and_constraint_type() {
        List<RelationshipType> typesFromRepository = relationshipTypeCollectionRepository
                .byConstraint(
                        RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
                        TET_FOR_RELATIONSHIP_3_UID,
                        RelationshipConstraintType.FROM)
                .withConstraints()
                .blockingGet();

        assertThat(typesFromRepository.size()).isEqualTo(1);
        assertThat(typesFromRepository.get(0).uid()).isEqualTo(RELATIONSHIP_TYPE_3.uid());
        assertThat(typesFromRepository.get(0).fromConstraint().trackedEntityType().uid())
                .isEqualTo(TET_FOR_RELATIONSHIP_3_UID);
    }

    @Test
    public void get_relationship_2_from_object_repository_without_children() {
        ReadOnlyOneObjectRepositoryFinalImpl<RelationshipType> type1Repository
                = relationshipTypeCollectionRepository.uid(RELATIONSHIP_TYPE_UID_2);
        RelationshipType typeFromRepository = type1Repository.blockingGet();
        assertTypesWithoutConstraints(typeFromRepository, RELATIONSHIP_TYPE_2);
    }

    @Test
    public void get_relationship_1_from_object_repository_with_children() {
        RelationshipType typeFromRepository = relationshipTypeCollectionRepository
                .withConstraints()
                .uid(RELATIONSHIP_TYPE_UID_1)
                .blockingGet();
        assertThat(typeFromRepository).isEqualTo(RELATIONSHIP_TYPE_1);
    }

    @Test
    public void get_relationship_2_from_object_repository_with_children() {
        RelationshipType typeFromRepository = relationshipTypeCollectionRepository
                .withConstraints()
                .uid(RELATIONSHIP_TYPE_UID_2)
                .blockingGet();
        assertThat(typeFromRepository).isEqualTo(RELATIONSHIP_TYPE_2);
    }
}
