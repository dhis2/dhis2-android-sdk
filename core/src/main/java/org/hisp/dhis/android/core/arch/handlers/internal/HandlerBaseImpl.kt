/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import java.util.ArrayList

@Suppress("TooManyFunctions")
internal abstract class HandlerBaseImpl<O> : HandlerWithTransformer<O> {
    override fun handle(o: O) {
        if (o == null) {
            return
        }
        val o2 = beforeObjectHandled(o)
        val action = deleteOrPersist(o2)
        afterObjectHandled(o2, action)
    }

    override fun handle(o: O?, transformer: (O) -> O) {
        if (o == null) {
            return
        }
        handleInternal(o, transformer)
    }

    @JvmSuppressWildcards
    protected fun handle(o: O?, transformer: (O) -> O, oTransformedCollection: MutableList<O>) {
        if (o == null) {
            return
        }
        val oTransformed = handleInternal(o, transformer)
        oTransformedCollection.add(oTransformed)
    }

    private fun handleInternal(o: O, transformer: (O) -> O): O {
        val o2 = beforeObjectHandled(o)
        val o3 = transformer(o2)
        val action = deleteOrPersist(o3)
        afterObjectHandled(o3, action)
        return o3
    }

    @JvmSuppressWildcards
    override fun handleMany(oCollection: Collection<O>?) {
        if (oCollection != null) {
            val preHandledCollection = beforeCollectionHandled(oCollection)
            for (o in preHandledCollection) {
                handle(o)
            }
            afterCollectionHandled(preHandledCollection)
        }
    }

    @JvmSuppressWildcards
    override fun handleMany(oCollection: Collection<O>?, transformer: (O) -> O) {
        if (oCollection != null) {
            val preHandledCollection = beforeCollectionHandled(oCollection)
            val oTransformedCollection: MutableList<O> = ArrayList(oCollection.size)
            for (o in preHandledCollection) {
                handle(o, transformer, oTransformedCollection)
            }
            afterCollectionHandled(oTransformedCollection)
        }
    }

    protected abstract fun deleteOrPersist(o: O): HandleAction

    protected open fun beforeObjectHandled(o: O): O {
        return o
    }

    protected open fun afterObjectHandled(o: O, action: HandleAction) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }

    @JvmSuppressWildcards
    protected open fun beforeCollectionHandled(oCollection: Collection<O>): Collection<O> {
        return oCollection
    }

    @JvmSuppressWildcards
    protected open fun afterCollectionHandled(oCollection: Collection<O>?) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }
}
