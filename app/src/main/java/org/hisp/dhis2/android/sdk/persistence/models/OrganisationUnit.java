package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.persistence.Dhis2Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class OrganisationUnit extends BaseModel {

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    @JsonProperty("id")
    @Column(columnType = Column.PRIMARY_KEY)
    public String id;

    @JsonProperty("label")
    @Column
    public String label;

    @JsonProperty("level")
    @Column
    public int level;

    @JsonProperty("parent")
    @Column
    public String parent;

    @JsonProperty("programs")
    public void setPrograms(List<Map<String, Object>> programs) {
        List<String> tempPrograms = new ArrayList<String>();
        for(Map<String, Object> program : programs) {
            tempPrograms.add( (String) program.get("id") );
        }
        this.programs = tempPrograms;
    }

    public List<String> programs;

    public OrganisationUnit() {}

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
