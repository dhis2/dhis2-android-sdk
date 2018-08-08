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
package org.hisp.dhis.android.core.arch.handlers;

import org.hisp.dhis.android.core.common.HandleAction;

import java.util.Collection;

abstract class SyncHandlerBaseImpl<O> implements SyncHandler<O> {

    @Override
    public final void handle(O o) {
        if (o == null) {
            return;
        }
        O object = beforeObjectHandled(o);
        HandleAction action = deleteOrPersist(object);
        afterObjectHandled(object, action);
    }

    @Override
    public final void handleMany(Collection<O> oCollection) {
        if (oCollection != null) {
            for(O o : oCollection) {
                handle(o);
            }
            afterCollectionHandled(oCollection);
        }
    }

    protected abstract HandleAction deleteOrPersist(O o);

    protected O beforeObjectHandled(O o) {
        return o;
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void afterObjectHandled(O o, HandleAction action) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void afterCollectionHandled(Collection<O> oCollection) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }
}
