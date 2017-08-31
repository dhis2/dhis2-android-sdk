package org.hisp.dhis.android.sdk.utils;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by ignac on 01/08/2017.
 */

public class ScreenSizeConfigurator {
    private static final int SINGLE_PIECE = 250;
    private WindowManager windowManager;
    public static ScreenSizeConfigurator screenSizeConfigurator;

    private ScreenSizeConfigurator(
            WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public static ScreenSizeConfigurator init(WindowManager windowManager) {
        isNull(windowManager, "context must not be null");
        if (screenSizeConfigurator == null) {
            screenSizeConfigurator = new ScreenSizeConfigurator(windowManager);
        }
        return screenSizeConfigurator;
    }

    public static ScreenSizeConfigurator getInstance() {
        isNull(screenSizeConfigurator, "screenSizeConfigurator must not be null");
        return screenSizeConfigurator;
    }

    private int getColumnsByScreen(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        return width/SINGLE_PIECE;
    }

    public int getFields() {
        return getColumnsByScreen(this.windowManager);
    }
}
