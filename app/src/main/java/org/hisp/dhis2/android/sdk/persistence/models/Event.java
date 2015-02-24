package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
@JsonIgnoreProperties({"id"})
@Table
public class Event extends BaseModel {

    public static String STATUS_ACTIVE = "ACTIVE";

    public static String STATUS_COMPLETED = "COMPLETED";

    public static String STATUS_VISITED = "VISITED";

    public static String STATUS_FUTURE_VISIT = "FUTURE_VISIT";

    public static String STATUS_LATE_VISIT = "LATE_VISIT";

    public static String STATUS_SKIPPED = "SKIPPED";

    @Column(columnType = Column.PRIMARY_KEY)
    public String id;

    @JsonProperty("status")
    @Column
    public String status;

    @JsonProperty("program")
    @Column
    public String programId;

    @JsonProperty("programStage")
    @Column
    public String programStageId;

    @JsonProperty("orgUnit")
    @Column
    public String organisationUnitId;

    @JsonProperty("eventDate")
    @Column
    public String eventDate;

    @JsonProperty("dueDate")
    @Column
    public String dueDate;

    @JsonProperty("dataValues")
    public List<DataValue> dataValues;

    public List<DataValue> getDataValues() {
        return Select.all(DataValue.class, Condition.column(DataValue$Table.EVENTID).is(id));
    }

}
