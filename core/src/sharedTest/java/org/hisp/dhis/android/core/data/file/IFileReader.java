package org.hisp.dhis.android.core.data.file;

import java.io.IOException;

public interface IFileReader {
    String getStringFromFile(String filename) throws IOException;
}
