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

package org.hisp.dhis.android.sdk.core.constant;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.flow.Constant$Flow;
import org.hisp.dhis.android.sdk.core.flow.Constant$Flow$Table;
import org.hisp.dhis.android.sdk.corejava.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.constant.Constant;

import java.util.List;

public final class ConstantStore implements IIdentifiableObjectStore<Constant> {

    public ConstantStore() {
        //empty constructor
    }

    @Override
    public boolean insert(Constant object) {
        Constant$Flow constantFlow = Constant$Flow.fromModel(object);
        constantFlow.insert();

        object.setId(constantFlow.getId());
        return true;
    }

    @Override
    public boolean update(Constant object) {
        Constant$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(Constant object) {
        Constant$Flow constantFlow =
                Constant$Flow.fromModel(object);
        constantFlow.save();

        object.setId(constantFlow.getId());
        return true;
    }

    @Override
    public boolean delete(Constant object) {
        Constant$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<Constant> queryAll() {
        List<Constant$Flow> constantFlows = new Select()
                .from(Constant$Flow.class)
                .queryList();
        return Constant$Flow.toModels(constantFlows);
    }

    @Override
    public Constant queryById(long id) {
        Constant$Flow constantFlow = new Select()
                .from(Constant$Flow.class)
                .where(Condition.column(Constant$Flow$Table.ID).is(id))
                .querySingle();
        return Constant$Flow.toModel(constantFlow);
    }

    @Override
    public Constant queryByUid(String uid) {
        Constant$Flow constantFlow = new Select()
                .from(Constant$Flow.class)
                .where(Condition.column(Constant$Flow$Table.UID).is(uid))
                .querySingle();
        return Constant$Flow.toModel(constantFlow);
    }
}
