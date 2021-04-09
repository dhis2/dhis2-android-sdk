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
package org.hisp.dhis.android.core.common

import org.hisp.dhis.android.core.common.valuetype.validation.validators.DefaultValidator
import org.hisp.dhis.android.core.common.valuetype.validation.validators.IntegerNegativeValidator
import org.hisp.dhis.android.core.common.valuetype.validation.validators.IntegerPositiveValidator
import org.hisp.dhis.android.core.common.valuetype.validation.validators.ValueTypeValidator
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import java.util.*

enum class ValueType(javaClass: Class<*>, val validator: ValueTypeValidator) {
    TEXT(String::class.java, DefaultValidator),
    LONG_TEXT(String::class.java, DefaultValidator),
    LETTER(String::class.java, DefaultValidator),
    BOOLEAN(Boolean::class.java, DefaultValidator),
    TRUE_ONLY(Boolean::class.java, DefaultValidator),
    DATE(Date::class.java, DefaultValidator),
    DATETIME(Date::class.java, DefaultValidator),
    TIME(String::class.java, DefaultValidator),
    NUMBER(Double::class.java, DefaultValidator),
    UNIT_INTERVAL(Double::class.java, DefaultValidator),
    PERCENTAGE(Double::class.java, DefaultValidator),
    INTEGER(Int::class.java, DefaultValidator),
    INTEGER_POSITIVE(Int::class.java, IntegerPositiveValidator),
    INTEGER_NEGATIVE(Int::class.java, IntegerNegativeValidator),
    INTEGER_ZERO_OR_POSITIVE(Int::class.java, DefaultValidator),
    FILE_RESOURCE(String::class.java, DefaultValidator),
    COORDINATE(String::class.java, DefaultValidator),
    PHONE_NUMBER(String::class.java, DefaultValidator),
    EMAIL(String::class.java, DefaultValidator),
    USERNAME(String::class.java, DefaultValidator),
    ORGANISATION_UNIT(OrganisationUnit::class.java, DefaultValidator),
    TRACKER_ASSOCIATE(TrackedEntityInstance::class.java, DefaultValidator),
    AGE(Date::class.java, DefaultValidator),
    URL(String::class.java, DefaultValidator),
    IMAGE(String::class.java, DefaultValidator);

    val isInteger: Boolean
        get() = INTEGER_TYPES.contains(this)
    val isNumeric: Boolean
        get() = NUMERIC_TYPES.contains(this)
    val isBoolean: Boolean
        get() = BOOLEAN_TYPES.contains(this)
    val isText: Boolean
        get() = TEXT_TYPES.contains(this)
    val isDate: Boolean
        get() = DATE_TYPES.contains(this)
    val isFile: Boolean
        get() = this == FILE_RESOURCE
    val isCoordinate: Boolean
        get() = this == COORDINATE

    companion object {
        private val INTEGER_TYPES: Set<ValueType> =
                HashSet(listOf(INTEGER, INTEGER_POSITIVE, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE))
        private val NUMERIC_TYPES: Set<ValueType> =
                HashSet(listOf(INTEGER, NUMBER, INTEGER_POSITIVE, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE,
                        UNIT_INTERVAL, PERCENTAGE))
        private val BOOLEAN_TYPES: Set<ValueType> = HashSet(listOf(BOOLEAN, TRUE_ONLY))
        private val TEXT_TYPES: Set<ValueType> = HashSet(listOf(TEXT, LONG_TEXT, LETTER, COORDINATE, TIME, IMAGE))
        private val DATE_TYPES: Set<ValueType> = HashSet(listOf(DATE, DATETIME))
    }
}
