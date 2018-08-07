package org.hisp.dhis.android.core.datavalue;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public final class DataValueSet {

    public Collection<DataValue> dataValues;

    public DataValueSet(Collection<DataValue> dataValues) {
        this.dataValues = dataValues;
    }
}
