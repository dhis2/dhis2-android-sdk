package org.hisp.dhis.android.sdk.utils;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

import android.view.Display;
import android.view.WindowManager;

/**
 * Created by ignac on 01/08/2017.
 */

public class ScreenSizeConfigurator {
    private static final int SCREEN_SIZE_XLARGE = 1080;
    private static final int SCREEN_SIZE_LARGE = 750;
    private static final int SCREEN_SIZE_NORMAL = 640;
    private static final int SCREEN_SIZE_SMALL = 320;
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
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        if (width <= SCREEN_SIZE_SMALL) {
            return 4;
        } else if (width <= SCREEN_SIZE_NORMAL) {
            return 8;
        } else if (width <= SCREEN_SIZE_LARGE) {
            return 10;
        } else if (width <= SCREEN_SIZE_XLARGE) {
            return 12;
        } else {
            return 14;
        }
    }

    public int getFields() {
        return getColumnsByScreen(this.windowManager);
    }
}
