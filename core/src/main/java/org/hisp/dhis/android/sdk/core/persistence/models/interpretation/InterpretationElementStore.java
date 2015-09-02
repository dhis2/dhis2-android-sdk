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

package org.hisp.dhis.android.sdk.core.persistence.models.interpretation;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.InterpretationElement$Flow$Table;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationElementStore;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationElement;

import java.util.List;

public class InterpretationElementStore implements IInterpretationElementStore {

    @Override
    public void insert(InterpretationElement object) {
        InterpretationElement$Flow elementFlow =
                InterpretationElement$Flow.fromModel(object);
        elementFlow.insert();

        object.setId(elementFlow.getId());
    }

    @Override
    public void update(InterpretationElement object) {
        InterpretationElement$Flow.fromModel(object).update();
    }

    @Override
    public void save(InterpretationElement object) {
        InterpretationElement$Flow elementFlow =
                InterpretationElement$Flow.fromModel(object);
        elementFlow.save();

        object.setId(elementFlow.getId());
    }

    @Override
    public void delete(InterpretationElement object) {
        InterpretationElement$Flow.fromModel(object).delete();
    }

    @Override
    public List<InterpretationElement> query() {
        List<InterpretationElement$Flow> elementFlows = new Select()
                .from(InterpretationElement$Flow.class)
                .queryList();
        return InterpretationElement$Flow.toModels(elementFlows);
    }

    @Override
    public InterpretationElement query(long id) {
        InterpretationElement$Flow elementFlow = new Select()
                .from(InterpretationElement$Flow.class)
                .where(Condition.column(InterpretationElement$Flow$Table.ID).is(id))
                .querySingle();
        return InterpretationElement$Flow.toModel(elementFlow);
    }

    @Override
    public InterpretationElement query(String uid) {
        InterpretationElement$Flow elementFlow = new Select()
                .from(InterpretationElement$Flow.class)
                .where(Condition.column(InterpretationElement$Flow$Table.UID).is(uid))
                .querySingle();
        return InterpretationElement$Flow.toModel(elementFlow);
    }

    @Override
    public List<InterpretationElement> query(Interpretation interpretation) {
        Interpretation$Flow interpretationFlow = Interpretation$Flow.fromModel(interpretation);
        List<InterpretationElement$Flow> elementFlow = new Select()
                .from(InterpretationElement$Flow.class)
                .where(Condition.column(InterpretationElement$Flow$Table
                        .INTERPRETATION_INTERPRETATION).is(interpretationFlow.getId()))
                .queryList();
        return InterpretationElement$Flow.toModels(elementFlow);
    }
}
