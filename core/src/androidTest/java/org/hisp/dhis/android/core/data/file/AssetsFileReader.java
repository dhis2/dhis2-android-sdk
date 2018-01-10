package org.hisp.dhis.android.core.data.file;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class AssetsFileReader implements IFileReader {
/*    public static String getStringFromFile(Class clazz, String filePath) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        File file = new File(classLoader.getResource("resources/" + filePath).getFile());
        return FileUtils.readFileToString(file, UTF_8);
    }*/

    @Override
    public String getStringFromFile(String filename) throws IOException {
        //FileInputStream inputStream = new FileInputStream(getFile(clazz, filename));

        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        InputStream inputStream = testContext.getAssets().open(filename);

        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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

