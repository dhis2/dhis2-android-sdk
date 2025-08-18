/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.handlers.internal

@Suppress("TooManyFunctions")
internal abstract class HandlerBaseImpl<O> : HandlerWithTransformer<O> {
    override suspend fun handle(o: O?) {
        if (o == null) {
            return
        }
        handleMany(listOf(o))
    }

    override suspend fun handle(o: O?, transformer: (O) -> O) {
        if (o == null) {
            return
        }
        handleMany(listOf(o), transformer)
    }

    override suspend fun handleMany(oCollection: Collection<O>?) {
        if (oCollection != null) {
            val preHandledCollection = beforeCollectionHandled(oCollection)
                .map { beforeObjectHandled(it) }

            deleteOrPersist(preHandledCollection)

            afterCollectionHandled(preHandledCollection)
        }
    }

    override suspend fun handleMany(oCollection: Collection<O>?, transformer: (O) -> O) {
        if (oCollection != null) {
            val preHandledCollection = beforeCollectionHandled(oCollection)
                .map { beforeObjectHandled(it) }
                .map { transformer.invoke(it) }

            deleteOrPersist(preHandledCollection)

            afterCollectionHandled(preHandledCollection)
        }
    }

    protected abstract suspend fun deleteOrPersist(oCollection: Collection<O>)

    protected open suspend fun beforeObjectHandled(o: O): O {
        return o
    }

    protected open suspend fun afterObjectHandled(o: O, action: HandleAction) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }

    @JvmSuppressWildcards
    protected open suspend fun beforeCollectionHandled(oCollection: Collection<O>): Collection<O> {
        return oCollection
    }

    @JvmSuppressWildcards
    protected open suspend fun afterCollectionHandled(oCollection: Collection<O>?) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }
}
