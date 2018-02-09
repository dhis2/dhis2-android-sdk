package org.hisp.dhis.android.core.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public final class ResourcesFileReader implements IFileReader {
    @Override
    public String getStringFromFile(String filename) throws IOException {
        FileInputStream inputStream = new FileInputStream(getFile(getClass(), filename));

        InputStreamReader isr = new InputStreamReader(inputStream, Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static File getFile(Class clazz, String filename) {
        ClassLoader classLoader = clazz.getClassLoader();
        URL resource = classLoader.getResource(filename);
        return new File(resource.getPath());
    }
}

