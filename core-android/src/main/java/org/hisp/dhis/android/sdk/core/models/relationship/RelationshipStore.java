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

import org.hisp.dhis.android.sdk.core.models.flow.Relationship$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.Relationship$Flow$Table;
import org.hisp.dhis.android.sdk.models.relationship.IRelationshipStore;
import org.hisp.dhis.android.sdk.models.relationship.Relationship;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

public final class RelationshipStore implements IRelationshipStore {

    public RelationshipStore() {
        //empty constructor
    }

    @Override
    public boolean insert(Relationship object) {
        Relationship$Flow relationshipFlow =
                Relationship$Flow.fromModel(object);
        relationshipFlow.insert();
        return true;
    }

    @Override
    public boolean update(Relationship object) {
        Relationship$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(Relationship object) {
        Relationship$Flow relationshipFlow =
                Relationship$Flow.fromModel(object);
        relationshipFlow.save();
        return true;
    }

    @Override
    public boolean delete(Relationship object) {
        Relationship$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public Relationship queryById(long id) {
        return null;
    }

    @Override
    public List<Relationship> queryAll() {
        List<Relationship$Flow> relationshipFlow = new Select()
                .from(Relationship$Flow.class)
                .queryList();
        return Relationship$Flow.toModels(relationshipFlow);
    }


    @Override
    public List<Relationship> query(TrackedEntityInstance trackedEntityInstance) {
        if(trackedEntityInstance == null) {
            return null;
        }
        List<Relationship$Flow> relationshipFlow = new Select()
                .from(Relationship$Flow.class).where(Condition.column(Relationship$Flow$Table
                        .TRACKEDENTITYINSTANCEA_TRACKEDENTITYINSTANCEA).is(trackedEntityInstance
                        )).or(Condition.column(Relationship$Flow$Table
                        .TRACKEDENTITYINSTANCEB_TRACKEDENTITYINSTANCEB).is(trackedEntityInstance
                        ))
                .queryList();
        return Relationship$Flow.toModels(relationshipFlow);
    }
}
