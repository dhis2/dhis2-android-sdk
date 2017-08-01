package org.hisp.dhis.android.sdk.utils;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by ignac on 01/08/2017.
 */

public class ScreenSizeConfigurator {
    private Context context;
    public static ScreenSizeConfigurator screenSizeConfigurator;

    private ScreenSizeConfigurator(
            Context context) {
        this.context = context;
    }

    public static ScreenSizeConfigurator init(Context context){
        isNull(context, "context must not be null");
        if(screenSizeConfigurator == null){
            screenSizeConfigurator = new ScreenSizeConfigurator(context);
        }
        return screenSizeConfigurator;
    }

    public static ScreenSizeConfigurator getInstance(){
        isNull(screenSizeConfigurator, "screenSizeConfigurator must not be null");
        return screenSizeConfigurator;
    }

    private static int getColumnsByScreen(Context context) {int screenSize = context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return 9;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 10;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 6;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 2;
            default:
                return 3;
        }
    }

    public int getFields() {
        return getColumnsByScreen(this.context);
    }
}
