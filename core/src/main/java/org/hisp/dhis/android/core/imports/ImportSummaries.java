package org.hisp.dhis.android.core.imports;

import java.util.List;

interface ImportSummaries<M extends ImportSummary> {

    List<M> importSummaries();

}
