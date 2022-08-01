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

package org.hisp.dhis.android.core.arch.helpers

sealed class Result<out S, out F : Throwable> {
    data class Success<out S, out F : Throwable>(val value: S) : Result<S, F>()
    data class Failure<out S, out F : Throwable>(val failure: F) : Result<S, F>()

    val succeeded: Boolean get() = this is Success && value != null

    fun fold(onSuccess: (S) -> Unit, onFailure: (F) -> Unit) {
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(failure)
        }
    }

    fun getOrThrow(): S {
        return when (this) {
            is Success -> value
            is Failure -> throw failure
        }
    }

    fun getOrNull(): S? {
        return when (this) {
            is Success -> value
            is Failure -> null
        }
    }

    fun <T> map(transform: (value: S) -> T): Result<T, F> {
        return when (this) {
            is Success -> Success(transform(value))
            is Failure -> Failure(failure)
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[value=$value]"
            is Failure -> "Failure[failure=$failure]"
        }
    }
}
