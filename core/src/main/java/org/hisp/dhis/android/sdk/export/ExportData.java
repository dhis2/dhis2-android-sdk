package org.hisp.dhis.android.sdk.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.client.sdk.ui.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportData {

    private final String TAG = ".ExportData";

    /**
     * Temporal folder that contains all the files to send
     */
    private final String EXPORT_DATA_FOLDER = "exportdata/";
    /**
     * Temporal file to be attached
     */
    private final String EXPORT_DATA_FILE = "compressedData.zip";
    /**
     * Temporal file that contains phonemetadata and app version info
     */
    private final String EXTRA_INFO = "extrainfo.txt";
    /**
     * Databases folder
     */
    private final String DATABASE_FOLDER = "databases/";
    /**
     * Shared preferences folder
     */
    private final String SHAREDPREFERENCES_FOLDER = "shared_prefs/";

    private Context mContext;

    public static String getCommitHash(Context context) {
        String stringCommit;
        //Check if lastcommit.txt file exist, and if not exist show as unavailable.
        int layoutId = context.getResources().getIdentifier("lastcommit", "raw",
                context.getPackageName());
        if (layoutId == 0) {
            stringCommit = "";
        } else {
            InputStream commit = context.getResources().openRawResource(layoutId);
            stringCommit = convertFromInputStreamToString(commit).toString();
        }
        return stringCommit;
    }

    public static StringBuilder convertFromInputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }

    /**
     * This method create the dump and returns the intent
     */
    public Intent dumpAndSendToAIntent(Activity activity) throws IOException {
        mContext = activity.getBaseContext();
        removeDumpIfExist(activity);
        File tempFolder = new File(getCacheDir() + "/" + EXPORT_DATA_FOLDER);
        tempFolder.mkdir();
        //copy database
        dumpDatabase(Dhis2Database.NAME + ".db", tempFolder);
        //Copy the sharedPreferences
        dumpSharedPreferences(tempFolder);

        //copy phonemetadata and gradle version
        File customInformation = new File(tempFolder + "/" + EXTRA_INFO);
        dumpMetadata(customInformation, activity);

        //compress and send
        File compressedFile = compressFolder(tempFolder);
        if (compressedFile == null) {
            return null;
        }
        return createEmailIntent(activity, compressedFile);
    }

    /**
     * This method create the dump the metadata in a temporally file
     */
    private void dumpMetadata(File customInformation, Activity activity)
            throws IOException {
        customInformation.createNewFile();
        FileWriter fw = new FileWriter(customInformation.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Flavour: " + BuildConfig.FLAVOR + "\n");
        bw.write("Version code: " + BuildConfig.VERSION_CODE + "\n");
        bw.write("Version name: " + BuildConfig.VERSION_NAME + "\n");
        bw.write("Application Id: " + BuildConfig.APPLICATION_ID + "\n");
        bw.write("Build type: " + BuildConfig.BUILD_TYPE + "\n");
        bw.write("Hash: " + getCommitHash(activity));

        bw.close();
        fw.close();
    }

    /**
     * This method checks if the tempfolder contains files and zip it.
     */
    private File compressFolder(File tempFolder) throws IOException {
        if (tempFolder.listFiles() == null) {
            Log.d(TAG, "Error, nothing to convert");
            return null;
        }
        zipFolder(tempFolder.getAbsolutePath(), getCacheDir() + "/" + EXPORT_DATA_FILE);
        File file = new File(getCacheDir() + "/" + EXPORT_DATA_FILE);
        return file;
    }


    /**
     * This method compress all the files in the temporal folder to be sent
     */
    private void zipFolder(String inputFolderPath, String outputFilePath) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    /**
     * This method dump a database
     */
    private void dumpDatabase(String dbName, File tempFolder) throws IOException {
        File backupDB = null;
        if (tempFolder.canWrite()) {
            File currentDB = new File(getDatabasesFolder(), dbName);
            backupDB = new File(tempFolder, dbName);
            copyFile(currentDB, backupDB);
        }
    }

    /**
     * This method dump the sharedPreferences
     */
    private void dumpSharedPreferences(File tempFolder) throws IOException {
        File files[] = getSharedPreferencesFolder().listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            copyFile(files[i], new File(tempFolder, files[i].getName()));
        }
    }

    /**
     * This method copy a file in other file
     */
    private void copyFile(File current, File backup) throws IOException {
        if (current.exists()) {
            FileChannel src = new FileInputStream(current)
                    .getChannel();
            FileChannel dst = new FileOutputStream(backup)
                    .getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
    }

    /**
     * This method returns the app cache dir
     */
    private File getCacheDir() {
        return mContext.getCacheDir();

    }

    /**
     * This method returns the app path
     */
    private String getAppPath() {
        return "/data/data/" + mContext.getPackageName() + "/";

    }

    /**
     * This method returns the sharedPreferences app folder
     */
    private File getSharedPreferencesFolder() {
        String sharedPreferencesPath = getAppPath() + SHAREDPREFERENCES_FOLDER;
        File file = new File(sharedPreferencesPath);
        return file;
    }

    /**
     * This method returns the databases app folder
     */
    private File getDatabasesFolder() {
        String databasesPath = getAppPath() + DATABASE_FOLDER;
        File file = new File(databasesPath);
        return file;
    }

    /**
     * This method create the email intent
     */
    private Intent createEmailIntent(Activity activity, File data) {
        Log.d(TAG, data.toURI() + "");
        data.setReadable(true, false);
        final Uri uri = FileProvider.getUriForFile(activity,
                "org.hisp.dhis.android.sdk.export.ExportData", data);

        final Intent chooser = ShareCompat.IntentBuilder.from(activity)
                .setType("application/zip")
                .setSubject(mContext.getString(
                        R.string.app_name)
                        + " db " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        Calendar.getInstance().getTime()))
                .setStream(uri)
                .setChooserTitle(
                        activity.getResources().getString(R.string.export_data_name))
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return chooser;
    }

    /**
     * This method remove the dump.
     */
    public void removeDumpIfExist(Activity activity) {
        File file = new File(activity.getCacheDir() + "/" + EXPORT_DATA_FILE);
        file.delete();

        File tempFolder = new File(activity.getCacheDir() + "/" + EXPORT_DATA_FOLDER);
        File[] files = tempFolder.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
        tempFolder.delete();
    }
}