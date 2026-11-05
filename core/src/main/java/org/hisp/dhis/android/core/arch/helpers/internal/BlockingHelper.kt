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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * Blocks the calling thread until [block] completes, running [block] in a [Dispatchers.IO] context
 * that starts on the calling thread.
 *
 * Use it instead of a plain `runBlocking` to bridge blocking APIs to suspend code that touches the
 * database. Room 3 DAO functions start with `withContext(Dispatchers.IO)`, which does not switch
 * threads when the caller already runs in that dispatcher, so [block] runs on the calling thread
 * until it actually suspends and only resumes on an IO thread afterwards. Dispatching [block] to
 * an IO thread up front costs a thread hand-off per call, which dominates the cost of small reads
 * when the blocking API is called from an IO thread.
 *
 * Running on the calling thread also lets a call nested in a database block run by the same
 * thread reuse the connection of that block through Room's thread-local, as Room 2 did.
 */
internal fun <T> runBlockingOnIO(block: suspend CoroutineScope.() -> T): T {
    return runBlocking {
        async(Dispatchers.IO, CoroutineStart.UNDISPATCHED, block).await()
    }
}
