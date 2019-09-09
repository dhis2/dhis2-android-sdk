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

    static void renameFile(File file, String newFileName, Context context) {
        File newFile = new File(context.getFilesDir(), "sdk_resources/" + newFileName);

        if (!file.renameTo(newFile)) {
            Log.d(FileResourceUtil.class.getCanonicalName(),
                    "Fail renaming " + file.getName() + " to " + newFileName);
        }
    }

    public static File saveFile(File sourceFile, String fileResourceUid, Context context) throws IOException {
        InputStream inputStream = new FileInputStream(sourceFile);

        File destinationFile = new File(getFileResourceDirectory(context), fileResourceUid);

        return writeInputStream(inputStream, destinationFile, sourceFile.length());
    }

    static void saveFileFromResponse(ResponseBody body, String generatedFileName, Context context) {
        File destinationFile = new File(FileResourceUtil.getFileResourceDirectory(context), generatedFileName);

        writeInputStream(body.byteStream(), destinationFile, body.contentLength());
    }

    private static File writeInputStream(InputStream inputStream, File file, long fileSize) {
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[1024];
            long fileSizeDownloaded = 0;
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
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
