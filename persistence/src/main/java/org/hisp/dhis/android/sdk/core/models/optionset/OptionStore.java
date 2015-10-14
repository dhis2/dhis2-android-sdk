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

package org.hisp.dhis.android.sdk.core.models.optionset;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.models.optionset.IOptionStore;
import org.hisp.dhis.android.sdk.models.optionset.Option;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;
import org.hisp.dhis.android.sdk.core.models.flow.Option$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.Option$Flow$Table;

import java.util.List;

public final class OptionStore implements IOptionStore {

    public OptionStore() {
        //empty constructor
    }

    @Override
    public boolean insert(Option object) {
        Option$Flow optionFlow = Option$Flow.fromModel(object);
        optionFlow.insert();

        object.setId(optionFlow.getId());
        return true;
    }

    @Override
    public boolean update(Option object) {
        Option$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(Option object) {
        Option$Flow optionFlow =
                Option$Flow.fromModel(object);
        optionFlow.save();

        object.setId(optionFlow.getId());
        return true;
    }

    @Override
    public boolean delete(Option object) {
        Option$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<Option> queryAll() {
        List<Option$Flow> optionFlows = new Select()
                .from(Option$Flow.class)
                .queryList();
        return Option$Flow.toModels(optionFlows);
    }

    @Override
    public Option queryById(long id) {
        Option$Flow optionFlow = new Select()
                .from(Option$Flow.class)
                .where(Condition.column(Option$Flow$Table.ID).is(id))
                .orderBy(Option$Flow$Table.SORTORDER)
                .querySingle();
        return Option$Flow.toModel(optionFlow);
    }

    @Override
    public Option queryByUid(String uid) {
        Option$Flow optionFlow = new Select()
                .from(Option$Flow.class)
                .where(Condition.column(Option$Flow$Table.UID).is(uid))
                .querySingle();
        return Option$Flow.toModel(optionFlow);
    }

    @Override
    public List<Option> query(OptionSet optionSet) {
        List<Option$Flow> optionFlows = new Select()
                .from(Option$Flow.class)
                .where(Condition.column(Option$Flow$Table
                        .OPTIONSET).is(optionSet.getUId()))
                .queryList();
        return Option$Flow.toModels(optionFlows);
    }
}
