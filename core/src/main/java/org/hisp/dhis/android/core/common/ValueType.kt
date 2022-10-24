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
package org.hisp.dhis.android.core.common

import java.util.*
import org.hisp.dhis.android.core.common.valuetype.validation.validators.*

enum class ValueType(val validator: ValueTypeValidator<*>) {
    TEXT(TextValidator),
    LONG_TEXT(LongTextValidator),
    LETTER(LetterValidator),
    PHONE_NUMBER(PhoneNumberValidator),
    EMAIL(EmailValidator),
    BOOLEAN(BooleanValidator),
    TRUE_ONLY(TrueOnlyValidator),
    DATE(DateValidator),
    DATETIME(DateTimeValidator),
    TIME(TimeValidator),
    NUMBER(NumberValidator),
    UNIT_INTERVAL(UnitIntervalValidator),
    PERCENTAGE(PercentageValidator),
    INTEGER(IntegerValidator),
    INTEGER_POSITIVE(IntegerPositiveValidator),
    INTEGER_NEGATIVE(IntegerNegativeValidator),
    INTEGER_ZERO_OR_POSITIVE(IntegerZeroOrPositiveValidator),
    TRACKER_ASSOCIATE(UidValidator),
    USERNAME(TextValidator),
    COORDINATE(CoordinateValidator),
    ORGANISATION_UNIT(UidValidator),
    REFERENCE(TextValidator),
    AGE(DateValidator),
    URL(TextValidator),
    FILE_RESOURCE(UidValidator),
    IMAGE(UidValidator),
    GEOJSON(TextValidator);

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
        get() = FILE_TYPES.contains(this)
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
        private val TEXT_TYPES: Set<ValueType> = HashSet(listOf(TEXT, LONG_TEXT, LETTER, COORDINATE, TIME))
        private val DATE_TYPES: Set<ValueType> = HashSet(listOf(DATE, DATETIME))

        // FILE_RESOURCE is not supported yet ANDROSDK-1531
        private val FILE_TYPES: Set<ValueType> = HashSet(listOf(IMAGE, FILE_RESOURCE))
    }
}
