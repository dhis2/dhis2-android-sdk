/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.category.CategoryModuleWiper;
import org.hisp.dhis.android.core.dataelement.DataElementModuleWiper;
import org.hisp.dhis.android.core.dataset.DataSetModuleWiper;
import org.hisp.dhis.android.core.datavalue.DataValueModuleWiper;
import org.hisp.dhis.android.core.maintenance.MaintenanceModuleWiper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleWiper;
import org.hisp.dhis.android.core.program.ProgramModuleWiper;
import org.hisp.dhis.android.core.relationship.RelationshipModuleWiper;
import org.hisp.dhis.android.core.resource.ResourceModuleWiper;
import org.hisp.dhis.android.core.settings.SystemSettingModuleWiper;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModuleWiper;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModuleWiper;
import org.hisp.dhis.android.core.user.UserModuleWiper;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class D2ModuleWipers {
    final List<ModuleWiper> wipers;

    @Inject
    D2ModuleWipers(SystemInfoModuleWiper systemInfo,
                   SystemSettingModuleWiper systemSetting,
                   RelationshipModuleWiper relationship,
                   CategoryModuleWiper category,
                   DataSetModuleWiper dataSet,
                   DataElementModuleWiper dataElement,
                   DataValueModuleWiper dataValue,
                   UserModuleWiper user,
                   MaintenanceModuleWiper maintenance,
                   OrganisationUnitModuleWiper organisationUnit,
                   ResourceModuleWiper resource,
                   ProgramModuleWiper program,
                   TrackedEntityModuleWiper trackedEntity,
                   D2StoresWithoutModuleModuleWiper storesWithoutModule) {

        this.wipers = Arrays.asList(
                systemInfo,
                systemSetting,
                relationship,
                category,
                dataSet,
                dataElement,
                dataValue,
                user,
                maintenance,
                organisationUnit,
                resource,
                program,
                trackedEntity,
                storesWithoutModule);
    }
}