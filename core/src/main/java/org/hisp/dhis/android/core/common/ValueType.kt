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

import java.util.*
import org.hisp.dhis.android.core.common.valuetype.validation.validators.*
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

enum class ValueType(javaClass: Class<*>, val validator: ValueTypeValidator<*>) {
    TEXT(String::class.java, TextValidator),
    LONG_TEXT(String::class.java, LongTextValidator),
    LETTER(String::class.java, LetterValidator),
    BOOLEAN(Boolean::class.java, BooleanValidator),
    TRUE_ONLY(Boolean::class.java, TrueOnlyValidator),
    DATE(Date::class.java, DateValidator),
    DATETIME(Date::class.java, DateTimeValidator),
    TIME(String::class.java, TimeValidator),
    NUMBER(Double::class.java, NumberValidator),
    UNIT_INTERVAL(Double::class.java, UnitIntervalValidator),
    PERCENTAGE(Double::class.java, PercentageValidator),
    INTEGER(Int::class.java, IntegerValidator),
    INTEGER_POSITIVE(Int::class.java, IntegerPositiveValidator),
    INTEGER_NEGATIVE(Int::class.java, IntegerNegativeValidator),
    INTEGER_ZERO_OR_POSITIVE(Int::class.java, IntegerZeroOrPositiveValidator),
    FILE_RESOURCE(String::class.java, UidValidator),
    COORDINATE(String::class.java, CoordinateValidator),
    PHONE_NUMBER(String::class.java, PhoneNumberValidator),
    EMAIL(String::class.java, EmailValidator),
    USERNAME(String::class.java, TextValidator),
    ORGANISATION_UNIT(OrganisationUnit::class.java, UidValidator),
    TRACKER_ASSOCIATE(TrackedEntityInstance::class.java, UidValidator),
    AGE(Date::class.java, DateValidator),
    URL(String::class.java, TextValidator),
    IMAGE(String::class.java, UidValidator);

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
            HashSet(
                listOf(
                    INTEGER, NUMBER, INTEGER_POSITIVE, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE,
                    UNIT_INTERVAL, PERCENTAGE
                )
            )
        private val BOOLEAN_TYPES: Set<ValueType> = HashSet(listOf(BOOLEAN, TRUE_ONLY))
        private val TEXT_TYPES: Set<ValueType> = HashSet(listOf(TEXT, LONG_TEXT, LETTER, COORDINATE, TIME, IMAGE))
        private val DATE_TYPES: Set<ValueType> = HashSet(listOf(DATE, DATETIME))
    }
}
