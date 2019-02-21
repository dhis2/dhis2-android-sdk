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

package org.hisp.dhis.android.core.constant;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.constant.ConstantSamples;
import org.hisp.dhis.android.core.data.database.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.data.database.IdentifiableObjectStoreAbstractIntegrationShould;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ConstantStoreIntegrationShould
        extends IdentifiableObjectStoreAbstractIntegrationShould<Constant> {

    public ConstantStoreIntegrationShould() {
        super(ConstantStore.create(DatabaseAdapterFactory.get(false)), ConstantTableInfo.TABLE_INFO,
                DatabaseAdapterFactory.get(false));
    }

    @Override
    protected Constant buildObject() {
        return ConstantSamples.getConstant();
    }

    @Override
    protected Constant buildObjectWithId() {
        return ConstantSamples.getConstant().toBuilder()
                .id(1L)
                .build();
    }

    @Override
    protected Constant buildObjectToUpdate() {
        return ConstantSamples.getConstant().toBuilder()
                .value(25.36)
                .build();
    }
}