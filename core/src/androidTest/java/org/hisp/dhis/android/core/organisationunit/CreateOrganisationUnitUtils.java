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

package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentValues;

public class CreateOrganisationUnitUtils {

    public static ContentValues createOrgUnit(long id, String uid) {
        ContentValues organisationUnit = new ContentValues();
        organisationUnit.put(OrganisationUnitModel.Columns.ID, id);
        organisationUnit.put(OrganisationUnitModel.Columns.UID, uid);
        organisationUnit.put(OrganisationUnitModel.Columns.CODE, "test_code");
        organisationUnit.put(OrganisationUnitModel.Columns.NAME, "test_name");
        organisationUnit.put(OrganisationUnitModel.Columns.DISPLAY_NAME, "test_display_name");
        organisationUnit.put(OrganisationUnitModel.Columns.CREATED, "test_created");
        organisationUnit.put(OrganisationUnitModel.Columns.LAST_UPDATED, "test_last_updated");
        organisationUnit.put(OrganisationUnitModel.Columns.SHORT_NAME, "test_short_name");
        organisationUnit.put(OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        organisationUnit.put(OrganisationUnitModel.Columns.DESCRIPTION, "test_description");
        organisationUnit.put(OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION, "test_display_description");
        organisationUnit.put(OrganisationUnitModel.Columns.PATH, "test_path");
        organisationUnit.put(OrganisationUnitModel.Columns.OPENING_DATE, "test_opening_date");
        organisationUnit.put(OrganisationUnitModel.Columns.CLOSED_DATE, "test_closed_date");
        organisationUnit.put(OrganisationUnitModel.Columns.LEVEL, "test_level");

        // foreign keys
        organisationUnit.putNull(OrganisationUnitModel.Columns.PARENT);

        return organisationUnit;
    }
}
