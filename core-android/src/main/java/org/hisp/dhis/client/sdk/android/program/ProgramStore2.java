/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.program;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.ModelLink$Flow;
import org.hisp.dhis.client.sdk.android.flow.ModelLink$Flow$Table;
import org.hisp.dhis.client.sdk.android.flow.Program$Flow;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.ArrayList;
import java.util.List;

public class ProgramStore2 extends AbsIdentifiableObjectStore<Program, Program$Flow>
        implements IProgramStore {

    /* Relationship type between programs and organisation units */
    private static final String PROGRAM_TO_ORGANISATION_UNITS = "programToOrganisationUnits";



    public ProgramStore2(IMapper<Program, Program$Flow> mapper) {
        super(mapper);
    }

    @Override
    public boolean insert(Program object) {
        boolean isSuccess = super.insert(object);
        boolean areModelsLinked = false;

        if (isSuccess) {
            // sync organisation unit links

            List<IDbOperation<ModelLink$Flow>> dbOperations =
                    updateLinksToOrganisationUnits(object);
        }

        return isSuccess && areModelsLinked;
    }

    @Override
    public boolean update(Program object) {
        boolean isSuccess = super.update(object);
        boolean areModelsLinked = false;

        if (isSuccess) {
            // sync organisation unit links

            updateLinksToOrganisationUnits(object);
        }

        return isSuccess && areModelsLinked;
    }

    @Override
    public boolean save(Program object) {
        boolean isSuccess = super.save(object);
        boolean areModelsLinked = false;

        if (isSuccess) {
            // sync organisation unit links

            updateLinksToOrganisationUnits(object);
        }

        return isSuccess && areModelsLinked;
    }

    private List<IDbOperation<ModelLink$Flow>> updateLinksToOrganisationUnits(Program program) {
        // organisation unit to program relation ships
        // create generic link table with UID to UID mapping?
        // then it will be impossible to perform joins on tables.

        List<ModelLink$Flow> links = new ArrayList<>();
        if (program.getOrganisationUnits() != null) {
            for (OrganisationUnit orgUnit : program.getOrganisationUnits()) {
                ModelLink$Flow linkModel = new ModelLink$Flow();
                linkModel.setKeyOne(orgUnit.getUId());
                linkModel.setKeyTwo(program.getUId());
                linkModel.setLinkMimeType(PROGRAM_TO_ORGANISATION_UNITS);
                links.add(linkModel);
            }
        }

        List<ModelLink$Flow> persistedLinks = new Select()
                .from(ModelLink$Flow.class)
                .where(Condition.column(ModelLink$Flow$Table
                        .LINKMIMETYPE).is(PROGRAM_TO_ORGANISATION_UNITS))
                .queryList();

        return ModelLink$Flow.createOperations(persistedLinks, links);
    }

    // * as soon as any write type of action happens, which should consider syncing relationships.
    // * but it also means that we have to fetch related elements on each read operation
    //   (Potentially can result in performance problem)
}
