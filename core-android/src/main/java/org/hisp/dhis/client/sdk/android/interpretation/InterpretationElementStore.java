/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.interpretation;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.InterpretationElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.InterpretationFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.InterpretationElementFlow_Table;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationElementStore;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class InterpretationElementStore implements IInterpretationElementStore {

    @Override
    public boolean insert(InterpretationElement object) {
        InterpretationElementFlow elementFlow =
                InterpretationElementFlow.fromModel(object);
        elementFlow.insert();

        object.setId(elementFlow.getId());
        return true;
    }

    @Override
    public boolean update(InterpretationElement object) {
        InterpretationElementFlow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(InterpretationElement object) {
        InterpretationElementFlow elementFlow =
                InterpretationElementFlow.fromModel(object);
        elementFlow.save();

        object.setId(elementFlow.getId());
        return true;
    }

    @Override
    public boolean delete(InterpretationElement object) {
        InterpretationElementFlow.fromModel(object).delete();
        return true;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public List<InterpretationElement> queryAll() {
        List<InterpretationElementFlow> elementFlows = new Select()
                .from(InterpretationElementFlow.class)
                .queryList();
        return InterpretationElementFlow.toModels(elementFlows);
    }

    @Override
    public InterpretationElement queryById(long id) {
        InterpretationElementFlow elementFlow = new Select()
                .from(InterpretationElementFlow.class)
                .where(InterpretationElementFlow_Table.id.is(id))
                .querySingle();
        return InterpretationElementFlow.toModel(elementFlow);
    }

    @Override
    public InterpretationElement queryByUid(String uid) {
        InterpretationElementFlow elementFlow = new Select()
                .from(InterpretationElementFlow.class)
                .where(InterpretationElementFlow_Table.uId.is(uid))
                .querySingle();
        return InterpretationElementFlow.toModel(elementFlow);
    }

    @Override
    public List<InterpretationElement> queryByUids(Set<String> uids) {
        return null;
    }

    @Override
    public boolean areStored(Set<String> objects) {
        return false;
    }

    @Override
    public List<InterpretationElement> list(Interpretation interpretation) {
        InterpretationFlow interpretationFlow = null;//Interpretation_Flow.fromModel(interpretation);
        List<InterpretationElementFlow> elementFlow = new Select()
                .from(InterpretationElementFlow.class)
                .where(InterpretationElementFlow_Table
                        .interpretation.is(interpretationFlow.getUId()))
                .queryList();
        return InterpretationElementFlow.toModels(elementFlow);
    }
}
