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
package org.hisp.dhis.android.core.arch.call

abstract class D2Progress {
    abstract val isComplete: Boolean

    abstract val totalCalls: Int?

    abstract val doneCalls: List<String?>

    fun lastCall(): String? {
        return if (doneCalls.isEmpty()) null else doneCalls.last()
    }

    @Suppress("MagicNumber")
    fun percentage(): Double? {
        return totalCalls?.let { 100.0 * doneCalls.size / it }
    }

    fun isComplete(): Boolean {
        return isComplete
    }

    fun totalCalls(): Int? {
        return totalCalls
    }

    fun doneCalls(): List<String?> {
        return doneCalls
    }


    abstract class Builder<T : Builder<T>> {
        abstract var isComplete: Boolean
        abstract var totalCalls: Int?
        abstract var doneCalls: List<String?>

        abstract fun build(): D2Progress

        @Suppress("UNCHECKED_CAST")
        fun isComplete(isComplete: Boolean): T = apply { this.isComplete = isComplete } as T

        @Suppress("UNCHECKED_CAST")
        fun totalCalls(totalCalls: Int?): T = apply { this.totalCalls = totalCalls } as T

        @Suppress("UNCHECKED_CAST")
        fun doneCalls(doneCalls: List<String?>?): T = apply { this.doneCalls = doneCalls ?: emptyList() } as T
    }
}
