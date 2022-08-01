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

package org.hisp.dhis.android.core.organisationunit.internal;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroupCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModule;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class OrganisationUnitModuleImpl implements OrganisationUnitModule {

    private final OrganisationUnitCollectionRepository organisationUnits;
    private final OrganisationUnitGroupCollectionRepository organisationUnitGroups;
    private final OrganisationUnitLevelCollectionRepository organisationUnitLevels;
    private final OrganisationUnitService organisationUnitService;

    @Inject
    OrganisationUnitModuleImpl(OrganisationUnitCollectionRepository organisationUnits,
                               OrganisationUnitGroupCollectionRepository organisationUnitGroups,
                               OrganisationUnitLevelCollectionRepository organisationUnitLevels,
                               OrganisationUnitService organisationUnitService) {
        this.organisationUnits = organisationUnits;
        this.organisationUnitGroups = organisationUnitGroups;
        this.organisationUnitLevels = organisationUnitLevels;
        this.organisationUnitService = organisationUnitService;
    }

    @Override
    public OrganisationUnitCollectionRepository organisationUnits() {
        return organisationUnits;
    }

    @Override
    public OrganisationUnitGroupCollectionRepository organisationUnitGroups() {
        return organisationUnitGroups;
    }

    @Override
    public OrganisationUnitLevelCollectionRepository organisationUnitLevels() {
        return organisationUnitLevels;
    }

    @Override
    public OrganisationUnitService organisationUnitService() {
        return organisationUnitService;
    }
}