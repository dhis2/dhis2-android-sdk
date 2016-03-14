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

import org.hisp.dhis.client.sdk.android.flow.InterpretationFlow;
import org.hisp.dhis.client.sdk.android.flow.InterpretationFlow_Table;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;

import java.util.List;

public final class InterpretationStore implements IIdentifiableObjectStore<Interpretation> {

    public InterpretationStore() {
        // empty constructor
    }

    @Override
    public boolean insert(Interpretation object) {
        InterpretationFlow interpretationFlow
                = null;//Interpretation_Flow.fromModel(object);
        interpretationFlow.insert();

        object.setId(interpretationFlow.getId());
        return true;
    }

    @Override
    public boolean update(Interpretation object) {
        //Interpretation_Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(Interpretation object) {
        InterpretationFlow interpretationFlow =
                null;//Interpretation_Flow.fromModel(object);
        interpretationFlow.save();

        object.setId(interpretationFlow.getId());
        return true;
    }

    @Override
    public boolean delete(Interpretation object) {
        //Interpretation_Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<Interpretation> queryAll() {
        List<InterpretationFlow> interpretationFlows = new Select()
                .from(InterpretationFlow.class)
                .queryList();
        return null;//Interpretation_Flow.toModels(interpretationFlows);
    }

    @Override
    public Interpretation queryById(long id) {
        InterpretationFlow interpretationFlow = new Select()
                .from(InterpretationFlow.class)
                .where(InterpretationFlow_Table.id.is(id))
                .querySingle();
        return null;//Interpretation_Flow.toModel(interpretationFlow);
    }

    @Override
    public Interpretation queryByUid(String uid) {
        InterpretationFlow interpretationFlow = new Select()
                .from(InterpretationFlow.class)
                .where(InterpretationFlow_Table.uId.is(uid))
                .querySingle();
        return null;//Interpretation_Flow.toModel(interpretationFlow);
    }

    /* @Override
    public List<Interpretation> filter(Action action) {
        List<Interpretation_Flow> interpretationFlows = new Select()
                .from(Interpretation_Flow.class)
                .where(Condition.column(Interpretation_Flow_Table
                        .ACTION).isNot(action.toString()))
                .queryList();
        return Interpretation_Flow.toModels(interpretationFlows);
    } */
}
