/*
 * Copyright (c) 2016.
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

package org.hisp.dhis.android.sdk.persistence.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.Map;

@Table(databaseName = Dhis2Database.NAME)
public class DataElementAttributeValue extends AttributeValue{

    public DataElementAttributeValue(){}

    @Column(name = "dataElement")
    String dataElement;

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement){
        this.dataElement = (String) dataElement.get("id");
    }

    public DataElement getDataElement(){
        return MetaDataController.getDataElement(dataElement);
    }

    public void setDataElement(String dataElement){
        this.dataElement = dataElement;
    }

}
