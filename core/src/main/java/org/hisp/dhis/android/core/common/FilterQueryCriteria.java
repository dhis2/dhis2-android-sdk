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

package org.hisp.dhis.android.core.common;

import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.List;

public abstract class FilterQueryCriteria {

    @Nullable
    public abstract Boolean followUp();

    @Nullable
    public abstract String organisationUnit();

    @Nullable
    public abstract OrganisationUnitMode ouMode();

    @Nullable
    public abstract AssignedUserMode assignedUserMode();

    @Nullable
    public abstract String order();

    @Nullable
    public abstract List<String> displayColumnOrder();

    @Nullable
    public abstract DateFilterPeriod eventDate();

    @Nullable
    public abstract DateFilterPeriod lastUpdatedDate();

    public abstract static class Builder<T extends Builder> {

        public abstract T followUp(Boolean followUp);

        public abstract T organisationUnit(String organisationUnit);

        public abstract T ouMode(OrganisationUnitMode ouMode);

        public abstract T assignedUserMode(AssignedUserMode assignedUserMode);

        public abstract T order(String order);

        public abstract T displayColumnOrder(List<String> displayColumnOrder);

        public abstract T eventDate(DateFilterPeriod eventDate);

        public abstract T lastUpdatedDate(DateFilterPeriod lastUpdatedDate);
    }
}
