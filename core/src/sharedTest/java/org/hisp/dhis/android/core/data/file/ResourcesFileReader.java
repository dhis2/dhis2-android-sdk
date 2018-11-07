package org.hisp.dhis.android.core.data.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class ResourcesFileReader implements IFileReader {
    @Override
    public String getStringFromFile(String filename) throws IOException {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        Scanner scanner = new java.util.Scanner(fileStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}

