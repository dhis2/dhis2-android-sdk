/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.scopedaccess

/**
 * A feature area a [D2DataScope] may unlock.
 *
 * Capabilities are opt-in: a scope with an empty capability set exposes nothing, no matter which
 * UIDs it grants. This keeps the default surface closed, so widening it is always an explicit act by
 * whoever authors the scope.
 */
enum class D2Capability {

    /** Read metadata: programs, data sets, org units, data elements, attributes, option sets. */
    READ_METADATA,

    /** Read tracked entity instances and their attribute values. */
    READ_TRACKED_ENTITY,

    /** Read enrollments. */
    READ_ENROLLMENT,

    /** Read events and their data values. */
    READ_EVENT,

    /** Read aggregated data values and complete registrations. */
    READ_DATA_VALUE,

    /** Use the tracked entity search API. Always resolved against the local database. */
    SEARCH_TRACKED_ENTITY,

    /** Read relationships between tracked entities and events. */
    READ_RELATIONSHIP,

    /** Read file resources. */
    READ_FILE_RESOURCE,

    /** Create and update tracked entity instances. */
    WRITE_TRACKED_ENTITY,

    /** Create and update enrollments. */
    WRITE_ENROLLMENT,

    /** Create and update events and their data values. */
    WRITE_EVENT,

    /** Create, update and delete aggregated data values. */
    WRITE_DATA_VALUE,
    ;

    /** True if this capability grants write access of any kind. */
    fun isWrite(): Boolean = name.startsWith("WRITE_")
}
