/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

public final class OrganisationUnitFields {

    static final String PARENT = "parent";
    static final String PATH = "path";
    static final String OPENING_DATE = "openingDate";
    static final String CLOSED_DATE = "closedDate";
    static final String LEVEL = "level";
    static final String PROGRAMS = "programs";
    static final String DATA_SETS = "dataSets";
    static final String ANCESTORS = "ancestors";
    static final String ORGANISATION_UNIT_GROUPS = "organisationUnitGroups";

    private static final FieldsHelper<OrganisationUnit> fh = new FieldsHelper<>();

    static final Field<OrganisationUnit, String> uid = fh.uid();
    private static final Field<OrganisationUnit, String> displayName = fh.displayName();
    private static final Field<OrganisationUnit, String> path = Field.create(PATH);
    private static final Field<OrganisationUnit, String> openingDate = Field.create(OPENING_DATE);
    private static final Field<OrganisationUnit, String> closedDate = Field.create(CLOSED_DATE);

    public static final Fields<OrganisationUnit> allFields = Fields.<OrganisationUnit>builder()
            .fields(fh.getNameableFields())
            .fields(
                    path,
                    openingDate,
                    closedDate,
                    fh.<String>field(LEVEL),
                    fh.nestedFieldWithUid(PARENT),
                    fh.nestedFieldWithUid(PROGRAMS),
                    fh.nestedFieldWithUid(DATA_SETS),
                    fh.<OrganisationUnit>nestedField(ANCESTORS).with(uid, displayName),
                    fh.<OrganisationUnitGroup>nestedField(ORGANISATION_UNIT_GROUPS)
                            .with(OrganisationUnitGroupFields.allFields)
            ).build();

    public static final Fields<OrganisationUnit> fieldsInUserCall = Fields.<OrganisationUnit>builder()
            .fields(fh.getNameableFields())
            .fields(
                    uid,
                    path,
                    openingDate,
                    closedDate
            ).build();

    public static final Fields<OrganisationUnit> teiSearchFieldsInUserCall = Fields.<OrganisationUnit>builder()
            .fields(fh.getNameableFields())
            .fields(
                    uid,
                    path
            ).build();

    private OrganisationUnitFields() {
    }
}