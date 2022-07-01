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
package org.hisp.dhis.android.realservertests

internal class EnumTestHelper {

    companion object {
        fun checkEnum(sdkEnumEntry: Map.Entry<String, List<String>>, generateConstants: List<String>?): String? {
            return if (generateConstants == null) {
                "Enum ${sdkEnumEntry.key} not found on the server"
            } else {
                val errorList = listOfNotNull(
                    constantsContained(sdkEnumEntry.value, generateConstants, sdkEnumEntry.key, "SDK"),
                    constantsContained(generateConstants, sdkEnumEntry.value, sdkEnumEntry.key, "server")
                )
                if (errorList.isNotEmpty()) errorList.joinToString() else null
            }
        }

        private fun constantsContained(
            containerList: List<String>,
            containedList: List<String>,
            enumKey: String,
            containerKey: String
        ): String? {
            val notContainedValues = containedList.filter { !containerList.contains(it) }
            return if (notContainedValues.isNotEmpty()) {
                "Constants ${notContainedValues.joinToString()} from enum " +
                    "$enumKey does not exist in the $containerKey"
            } else null
        }

        inline fun <reified E : Enum<E>> entry(apiKey: String): Pair<String, List<String>> {
            return Pair(apiKey, enumValues<E>().map { it.toString() })
        }
    }
}
