package org.hisp.dhis.android.core.fileresource.internal;

import android.content.Context;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileResourceUtil {

    public static File saveFile(File sourceFile, String fileResourceUid, Context context) throws IOException {
        InputStream in = new FileInputStream(sourceFile);
        File destinationFile = new File(getFileResourceDirectory(context), generateFileName(fileResourceUid));
        OutputStream out = new FileOutputStream(destinationFile);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        return destinationFile;
    }

    static File getFile(Context context, String fileResourceUid) throws D2Error {
        File file = new File(getFileResourceDirectory(context), generateFileName(fileResourceUid));

        if (file.exists()) {
            return file;
        } else {
            throw D2Error.builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.FILE_NOT_FOUND)
                    .errorDescription("File not found for this file resource uid")
                    .build();
        }
    }

    private static String generateFileName(String fileName) {
        return String.format("%s.png", fileName);
    }

    private static File getFileResourceDirectory(Context context) {
        File file = new File(context.getFilesDir(), "sdk_resources");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
