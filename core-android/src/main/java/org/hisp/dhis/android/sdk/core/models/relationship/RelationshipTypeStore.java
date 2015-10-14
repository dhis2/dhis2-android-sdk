/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.models.relationship;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.models.flow.RelationshipType$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.RelationshipType$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.relationship.RelationshipType;

import java.util.List;

public final class RelationshipTypeStore implements IIdentifiableObjectStore<RelationshipType> {

    public RelationshipTypeStore() {
        //empty constructor
    }

    @Override
    public boolean insert(RelationshipType object) {
        RelationshipType$Flow relationshipTypeFlow = RelationshipType$Flow.fromModel(object);
        relationshipTypeFlow.insert();

        object.setId(relationshipTypeFlow.getId());
        return true;
    }

    @Override
    public boolean update(RelationshipType object) {
        RelationshipType$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(RelationshipType object) {
        RelationshipType$Flow relationshipTypeFlow =
                RelationshipType$Flow.fromModel(object);
        relationshipTypeFlow.save();

        object.setId(relationshipTypeFlow.getId());
        return true;
    }

    @Override
    public boolean delete(RelationshipType object) {
        RelationshipType$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<RelationshipType> queryAll() {
        List<RelationshipType$Flow> relationshipTypeFlow = new Select()
                .from(RelationshipType$Flow.class)
                .queryList();
        return RelationshipType$Flow.toModels(relationshipTypeFlow);
    }

    @Override
    public RelationshipType queryById(long id) {
        RelationshipType$Flow relationshipTypeFlow = new Select()
                .from(RelationshipType$Flow.class)
                .where(Condition.column(RelationshipType$Flow$Table.ID).is(id))
                .querySingle();
        return RelationshipType$Flow.toModel(relationshipTypeFlow);
    }

    @Override
    public RelationshipType queryByUid(String uid) {
        RelationshipType$Flow relationshipTypeFlow = new Select()
                .from(RelationshipType$Flow.class)
                .where(Condition.column(RelationshipType$Flow$Table.UID).is(uid))
                .querySingle();
        return RelationshipType$Flow.toModel(relationshipTypeFlow);
    }
}
