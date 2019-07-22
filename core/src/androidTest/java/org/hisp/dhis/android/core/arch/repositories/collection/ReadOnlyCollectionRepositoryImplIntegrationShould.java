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
import org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.arch.repositories.collection.RelationshipTypeAsserts.assertTypesWithoutConstraints;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_1;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_2;
import static org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1;

@RunWith(D2JunitRunner.class)
public class ReadOnlyCollectionRepositoryImplIntegrationShould extends BaseMockIntegrationTestEmptyDispatcher {

    private static Map<String, RelationshipType> typeMap;
    private static RelationshipTypeCollectionRepository relationshipTypeCollectionRepository;

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseMockIntegrationTestEmptyDispatcher.setUpClass();

        typeMap = RelationshipTypeSamples.typeMap();

        Handler<RelationshipType> handler = objects.d2DIComponent.relationshipTypeHandler();

        handler.handle(RELATIONSHIP_TYPE_1);
        handler.handle(RELATIONSHIP_TYPE_2);

        relationshipTypeCollectionRepository = d2.relationshipModule().relationshipTypes;
    }

    @Test
    public void get_all_relationship_types_without_children_when_calling_get() {
        List<RelationshipType> types = relationshipTypeCollectionRepository.get();
        assertThat(types.size()).isEqualTo(2);

        for (RelationshipType targetType: types) {
            RelationshipType referenceType = typeMap.get(targetType.uid());
            assertTypesWithoutConstraints(targetType, referenceType);
        }
    }

    @Test
    public void get_all_relationship_types_without_children_when_calling_get_async() {
        List<RelationshipType> types = relationshipTypeCollectionRepository.getAsync().blockingGet();
        assertThat(types.size()).isEqualTo(2);

        for (RelationshipType targetType: types) {
            RelationshipType referenceType = typeMap.get(targetType.uid());
            assertTypesWithoutConstraints(targetType, referenceType);
        }
    }

    @Test
    public void get_all_relationship_types_with_children_when_calling_get_with_children() {
        List<RelationshipType> types = relationshipTypeCollectionRepository.withAllChildren().get();
        assertThat(types.size()).isEqualTo(2);

        for (RelationshipType targetType: types) {
            RelationshipType referenceType = typeMap.get(targetType.uid());
            assertThat(targetType).isEqualTo(referenceType);
        }
    }

    @Test
    public void get_count_with_unrestricted_scope() {
        int count = relationshipTypeCollectionRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void get_count_with_restricted_scope() {
        int count = relationshipTypeCollectionRepository.byUid().eq(RELATIONSHIP_TYPE_UID_1).count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void get_isEmpty_with_unrestricted_scope() {
        boolean isEmpty = relationshipTypeCollectionRepository.isEmpty();

        assertThat(isEmpty).isEqualTo(false);
    }

    @Test
    public void get_isEmpty_with_restricted_scope() {
        boolean isEmpty = relationshipTypeCollectionRepository.byUid().eq(RELATIONSHIP_TYPE_UID_1).isEmpty();

        assertThat(isEmpty).isEqualTo(false);
    }

    @Test
    public void get_isEmpty_with_zero_elements_scope() {
        boolean isEmpty = relationshipTypeCollectionRepository.byCode().eq("non-existing").isEmpty();

        assertThat(isEmpty).isEqualTo(true);
    }
}
