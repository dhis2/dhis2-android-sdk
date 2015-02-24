package org.hisp.dhis2.android.sdk.persistence.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table
public class Option extends BaseIdentifiableObject {

    @Column
    public String optionSet;

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }
}
