package org.hisp.dhis.android.core.imports;

import java.util.List;

interface ImportSummaries<M extends DataValueImportSummary> {

    List<M> importSummaries();

}
