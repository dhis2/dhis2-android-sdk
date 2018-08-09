package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.utils.Utils;

public final class DataSetCompleteRegistrationTableInfo {

    private DataSetCompleteRegistrationTableInfo() {}

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "DataSetCompleteRegistration";
        }

        @Override
        public BaseModel.Columns columns() {
            return new DataSetCompleteRegistrationTableInfo.Columns();
        }
    };

    static class Columns extends BaseModel.Columns {
        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    DataSetCompleteRegistrationFields.PERIOD,
                    DataSetCompleteRegistrationFields.DATA_SET,
                    DataSetCompleteRegistrationFields.ORGANISATION_UNIT,
                    DataSetCompleteRegistrationFields.ATTRIBUTE_OPTION_COMBO,
                    DataSetCompleteRegistrationFields.DATE,
                    DataSetCompleteRegistrationFields.STORED_BY);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{DataSetCompleteRegistrationFields.PERIOD,
                    DataSetCompleteRegistrationFields.DATA_SET,
                    DataSetCompleteRegistrationFields.ORGANISATION_UNIT,
                    DataSetCompleteRegistrationFields.ATTRIBUTE_OPTION_COMBO};
        }
    }
}
