/*
 *  Copyright (c) 2004-2021, University of Oslo
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
            ImporterError.E1007 -> E1007Interpreter(interpreterHelper, error.regex)
            ImporterError.E1008 -> E1008Interpreter(interpreterHelper, error.regex)
            ImporterError.E1009 -> E1009Interpreter(interpreterHelper, error.regex)
            ImporterError.E1010 -> TODO()
            ImporterError.E1011 -> TODO()
            ImporterError.E1012 -> TODO()
            ImporterError.E1013 -> TODO()
            ImporterError.E1014 -> TODO()
            ImporterError.E1015 -> TODO()
            ImporterError.E1016 -> TODO()
            ImporterError.E1018 -> TODO()
            ImporterError.E1019 -> TODO()
            ImporterError.E1020 -> TODO()
            ImporterError.E1021 -> TODO()
            ImporterError.E1022 -> TODO()
            ImporterError.E1023 -> TODO()
            ImporterError.E1025 -> TODO()
            ImporterError.E1029 -> TODO()
            ImporterError.E1030 -> TODO()
            ImporterError.E1031 -> TODO()
            ImporterError.E1032 -> E1032Interpreter(interpreterHelper, error.regex)
            ImporterError.E1033 -> TODO()
            ImporterError.E1035 -> TODO()
            ImporterError.E1039 -> TODO()
            ImporterError.E1041 -> TODO()
            ImporterError.E1042 -> TODO()
            ImporterError.E1048 -> TODO()
            ImporterError.E1049 -> TODO()
            ImporterError.E1050 -> TODO()
            ImporterError.E1055 -> TODO()
            ImporterError.E1056 -> TODO()
            ImporterError.E1057 -> TODO()
            ImporterError.E1063 -> E1063Interpreter(interpreterHelper, error.regex)
            ImporterError.E1064 -> E1064Interpreter(interpreterHelper, error.regex)
            ImporterError.E1068 -> TODO()
            ImporterError.E1069 -> E1069Interpreter(interpreterHelper, error.regex)
            ImporterError.E1070 -> TODO()
            ImporterError.E1074 -> TODO()
            ImporterError.E1075 -> TODO()
            ImporterError.E1076 -> TODO()
            ImporterError.E1077 -> TODO()
            ImporterError.E1079 -> TODO()
            ImporterError.E1080 -> TODO()
            ImporterError.E1081 -> E1081Interpreter(interpreterHelper, error.regex)
            ImporterError.E1082 -> TODO()
            ImporterError.E1083 -> TODO()
            ImporterError.E1084 -> TODO()
            ImporterError.E1085 -> TODO()
            ImporterError.E1086 -> TODO()
            ImporterError.E1087 -> TODO()
            ImporterError.E1088 -> TODO()
            ImporterError.E1089 -> TODO()
            ImporterError.E1090 -> TODO()
            ImporterError.E1091 -> TODO()
            ImporterError.E1095 -> TODO()
            ImporterError.E1096 -> TODO()
            ImporterError.E1099 -> TODO()
            ImporterError.E1100 -> TODO()
            ImporterError.E1102 -> TODO()
            ImporterError.E1103 -> TODO()
            ImporterError.E1104 -> TODO()
            ImporterError.E1112 -> TODO()
            ImporterError.E1113 -> TODO()
            ImporterError.E1114 -> TODO()
            ImporterError.E1115 -> TODO()
            ImporterError.E1116 -> TODO()
            ImporterError.E1117 -> TODO()
            ImporterError.E1118 -> TODO()
            ImporterError.E1119 -> TODO()
            ImporterError.E1120 -> TODO()
            ImporterError.E1121 -> TODO()
            ImporterError.E1122 -> TODO()
            ImporterError.E1123 -> TODO()
            ImporterError.E1124 -> TODO()
            ImporterError.E1125 -> TODO()
            ImporterError.E1126 -> TODO()
            ImporterError.E1127 -> TODO()
            ImporterError.E1128 -> TODO()
            ImporterError.E1094 -> TODO()
            ImporterError.E1110 -> TODO()
            ImporterError.E1045 -> TODO()
            ImporterError.E1043 -> TODO()
            ImporterError.E1044 -> TODO()
            ImporterError.E1046 -> TODO()
            ImporterError.E1047 -> TODO()
            ImporterError.E1300 -> TODO()
            ImporterError.E1301 -> TODO()
            ImporterError.E1302 -> TODO()
            ImporterError.E1303 -> TODO()
            ImporterError.E1304 -> TODO()
            ImporterError.E1305 -> TODO()
            ImporterError.E1306 -> TODO()
            ImporterError.E1307 -> TODO()
            ImporterError.E1308 -> TODO()
            ImporterError.E1309 -> TODO()
            ImporterError.E1310 -> TODO()
            ImporterError.E4000 -> TODO()
            ImporterError.E4001 -> TODO()
            ImporterError.E4003 -> TODO()
            ImporterError.E4004 -> TODO()
            ImporterError.E4005 -> TODO()
            ImporterError.E4006 -> TODO()
            ImporterError.E4007 -> TODO()
            ImporterError.E4008 -> TODO()
            ImporterError.E4009 -> TODO()
            ImporterError.E4010 -> TODO()
            ImporterError.E4011 -> TODO()
            ImporterError.E4012 -> TODO()
            ImporterError.E4013 -> TODO()
            ImporterError.E4014 -> TODO()
            ImporterError.E4015 -> TODO()
            ImporterError.E9999 -> TODO()
        }
    }
}
