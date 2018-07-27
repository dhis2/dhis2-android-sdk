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

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class RelationshipTypeRepository implements RelationshipTypeRepositoryInterface {

    private final ObjectWithoutUidStore<RelationshipConstraintModel> relationshipConstraintStore;
    private final IdentifiableObjectStore<RelationshipTypeModel> relationshipTypeStore;

    RelationshipTypeRepository(ObjectWithoutUidStore<RelationshipConstraintModel> relationshipConstraintStore,
                                      IdentifiableObjectStore<RelationshipTypeModel> relationshipTypeStore) {
        this.relationshipConstraintStore = relationshipConstraintStore;
        this.relationshipTypeStore = relationshipTypeStore;
    }

    public List<RelationshipType> getAll() {
        Set<RelationshipConstraintModel> relationshipConstraintModelSet =
                this.relationshipConstraintStore.selectAll(RelationshipConstraintModel.factory);

        Set<RelationshipTypeModel> relationshipTypeModelSet =
                this.relationshipTypeStore.selectAll(RelationshipTypeModel.factory);

        RelationshipTypeBuilder relationshipTypeBuilder = new RelationshipTypeBuilder(relationshipConstraintModelSet);
        int typesSize = relationshipTypeModelSet.size();
        List<RelationshipType> relationshipTypeList = new ArrayList<>(typesSize);

        for (RelationshipTypeModel model : relationshipTypeModelSet) {
            relationshipTypeList.add(relationshipTypeBuilder.buildPojo(model));
        }

        return relationshipTypeList;
    }

    static RelationshipTypeRepository create(DatabaseAdapter databaseAdapter) {
        return new RelationshipTypeRepository(
                RelationshipConstraintStore.create(databaseAdapter),
                RelationshipTypeStore.create(databaseAdapter)
        );
    }
}
