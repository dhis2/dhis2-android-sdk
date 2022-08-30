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

package org.hisp.dhis.android.core.tracker.importer.internal.interpreters

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.tracker.importer.internal.ImporterError

@Reusable
internal class InterpreterSelector @Inject internal constructor(
    private val interpreterHelper: InterpreterHelper
) {
    fun getInterpreter(error: ImporterError): ErrorCodeInterpreter {
        return when (error) {
            ImporterError.E1000 -> E1000Interpreter(interpreterHelper, error.regex)
            ImporterError.E1001 -> E1001Interpreter(interpreterHelper, error.regex)
            ImporterError.E1002 -> E1002Interpreter(interpreterHelper, error.regex)
            ImporterError.E1003 -> E1003Interpreter(interpreterHelper, error.regex)
            ImporterError.E1005 -> E1005Interpreter(interpreterHelper, error.regex)
            ImporterError.E1006 -> E1006Interpreter(interpreterHelper, error.regex)
            ImporterError.E1007 -> E1007Interpreter(error.regex)
            ImporterError.E1008 -> E1008Interpreter(interpreterHelper, error.regex)
            ImporterError.E1009 -> E1009Interpreter(error.regex)
            ImporterError.E1032 -> E1032Interpreter(interpreterHelper, error.regex)
            ImporterError.E1063 -> E1063Interpreter(interpreterHelper, error.regex)
            ImporterError.E1064 -> E1064Interpreter(interpreterHelper, error.regex)
            ImporterError.E1069 -> E1069Interpreter(interpreterHelper, error.regex)
            ImporterError.E1081 -> E1081Interpreter(error.regex)
            ImporterError.E1084 -> E1084Interpreter(error.regex)
            ImporterError.E1100 -> E1100Interpreter(interpreterHelper, error.regex)
            ImporterError.E1103 -> E1103Interpreter(error.regex)
            else -> DefaultInterpreter(error.regex)
        }
    }
}
