/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.arch.helpers.internal

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log

internal object D2Logger {
    private const val MAX_CAUSE_DEPTH = 5

    @Volatile
    internal var debuggable: Boolean = false
        private set

    fun init(context: Context) {
        debuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    fun v(tag: String?, message: String, throwable: Throwable? = null) {
        if (debuggable) log(Log.VERBOSE, tag, message, throwable)
    }

    fun d(tag: String?, message: String, throwable: Throwable? = null) {
        if (debuggable) log(Log.DEBUG, tag, message, throwable)
    }

    fun i(tag: String?, message: String, throwable: Throwable? = null) {
        if (debuggable) log(Log.INFO, tag, message, throwable)
    }

    fun w(tag: String?, message: String, throwable: Throwable? = null) {
        log(Log.WARN, tag, message, throwable)
    }

    fun e(tag: String?, message: String, throwable: Throwable? = null) {
        log(Log.ERROR, tag, message, throwable)
    }

    internal fun describe(throwable: Throwable?): String {
        return throwable?.let {
            generateSequence(it) { current -> current.cause?.takeIf { cause -> cause !== current } }
                .take(MAX_CAUSE_DEPTH)
                .joinToString(separator = " <- ", prefix = " [", postfix = "]") { t ->
                    t::class.simpleName ?: "Throwable"
                }
        } ?: ""
    }

    private fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        val text = when {
            throwable == null -> message
            debuggable -> message + "\n" + Log.getStackTraceString(throwable)
            else -> message + describe(throwable)
        }
        Log.println(priority, tag, text)
    }
}
