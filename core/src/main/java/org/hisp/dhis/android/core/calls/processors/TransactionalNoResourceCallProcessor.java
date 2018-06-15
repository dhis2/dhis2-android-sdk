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

package org.hisp.dhis.android.core.calls.processors;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;
import java.util.concurrent.Callable;

public class TransactionalNoResourceCallProcessor<P, M extends Model> implements CallProcessor<P> {
    private final DatabaseAdapter databaseAdapter;
    private final GenericHandler<P, M> handler;
    private final ModelBuilder<P, M> modelBuilder;

    public TransactionalNoResourceCallProcessor(DatabaseAdapter databaseAdapter,
                                                GenericHandler<P, M> handler,
                                                ModelBuilder<P, M> modelBuilder) {
        this.databaseAdapter = databaseAdapter;
        this.handler = handler;
        this.modelBuilder = modelBuilder;
    }

    public void process(final List<P> objectList) throws D2CallException {
        if (objectList != null && !objectList.isEmpty()) {
            new D2CallExecutor().executeD2CallTransactionally(databaseAdapter, new Callable<Void>() {

                @Override
                public Void call() {
                    handler.handleMany(objectList, modelBuilder);
                    return null;
                }
            });
        }
    }
}