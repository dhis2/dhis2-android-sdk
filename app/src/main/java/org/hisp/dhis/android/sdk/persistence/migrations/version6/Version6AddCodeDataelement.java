/*
 * Copyright (c) 2015.
 *
 * This file is part of QA App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hisp.dhis.android.sdk.persistence.migrations.version6;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;

/**
 * Created by ignac on 11/11/2015.
 */
@Migration(version = 6, databaseName = Dhis2Database.NAME)
public class Version6AddCodeDataelement extends AlterTableMigration<DataElement> {

    public Version6AddCodeDataelement(Class<DataElement> table) {
        super(DataElement.class);
    }

    public Version6AddCodeDataelement() {
        super(DataElement.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(String.class, "code");
    }
}