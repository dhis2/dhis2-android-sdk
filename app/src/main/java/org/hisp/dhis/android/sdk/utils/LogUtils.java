package org.hisp.dhis.android.sdk.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by erling on 8/28/15.
 */
public class LogUtils
{

    public static boolean writeErrorLogToSDCard(String path, String errorLog)
    {
        if( !(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) ) // check if SD card is mounted
            return false;

        File file;
        file = new File(path);
        if(!file.exists())
        {
            try
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                return false;
            }
        }

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); //true means append to file
            writer.append(errorLog);
            writer.newLine();
            writer.append("-------------------------------"); // seperator
            writer.newLine();
            writer.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }
}
