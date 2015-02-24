package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table
public class OptionSet extends BaseIdentifiableObject {

    @JsonProperty("options")
    public List<Option> options;

    public List<Option> getOptions() {
        if(options == null) {
            options = Select.all(Option.class,
                    Condition.column(Option$Table.OPTIONSET).is(id));
        }
        return options;
    }
}
