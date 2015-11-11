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

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.Attribute;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.DataElementAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.ProgramAttributeValue;

/**
 * Created by ignac on 11/11/2015.
 */
@Migration(version = 6, databaseName = Dhis2Database.NAME)
public class Version6AddCodeDataelement extends BaseMigration {

    public Version6AddCodeDataelement() {
        super();
    }
    public void onPreMigrate() {
    }
    @Override
    public void migrate(SQLiteDatabase database) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(Attribute.class);
        database.execSQL(myAdapter.getCreationQuery());
        myAdapter = FlowManager.getModelAdapter(AttributeValue.class);
        database.execSQL(myAdapter.getCreationQuery());
        myAdapter = FlowManager.getModelAdapter(DataElementAttributeValue.class);
        database.execSQL(myAdapter.getCreationQuery());
        myAdapter = FlowManager.getModelAdapter(ProgramAttributeValue.class);
        database.execSQL(myAdapter.getCreationQuery());

    }
    @Override
    public void onPostMigrate() {
        //release migration resources
    }
}