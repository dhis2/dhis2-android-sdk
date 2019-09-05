package org.hisp.dhis.android.core.fileresource.internal;

import android.content.Context;
import android.util.Log;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public final class FileResourceUtil {

    public static File saveFile(File sourceFile, String fileResourceUid, Context context) throws IOException {
        InputStream in = new FileInputStream(sourceFile);
        File destinationFile = new File(getFileResourceDirectory(context), fileResourceUid);
        OutputStream out = new FileOutputStream(destinationFile);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        return destinationFile;
    }

    static File getFile(Context context, String fileResourceUid) throws D2Error {
        File file = new File(getFileResourceDirectory(context), fileResourceUid);

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

    public static File getFileResourceDirectory(Context context) {
        File file = new File(context.getFilesDir(), "sdk_resources");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    static void writeFileToDisk(ResponseBody body, String generatedFileName, Context context) {
        try {
            File file = new File(FileResourceUtil.getFileResourceDirectory(context), generatedFileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[1024];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(FileResourceCallFactory.class.getCanonicalName(),
                            "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
