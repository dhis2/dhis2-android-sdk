package org.hisp.dhis.client.sdk.core;

import android.util.Log;

import org.hisp.dhis.client.sdk.utils.Logger;

public class LoggerImpl implements Logger {
    private final Logger customLogger;

    public LoggerImpl() {
        this(null);
    }

    public LoggerImpl(Logger customLogger) {
        this.customLogger = customLogger;
    }

    @Override
    public void v(String tag, String message) {
        if (customLogger == null) {
            Log.v(tag, message);
        } else {
            customLogger.v(tag, message);
        }
    }

    @Override
    public void v(String tag, String message, Throwable throwable) {
        if (customLogger == null) {
            Log.v(tag, message, throwable);
        } else {
            customLogger.v(tag, message, throwable);
        }
    }

    @Override
    public void d(String tag, String message) {
        if (customLogger == null) {
            Log.d(tag, message);
        } else {
            customLogger.d(tag, message);
        }
    }

    @Override
    public void d(String tag, String message, Throwable throwable) {
        if (customLogger == null) {
            Log.d(tag, message, throwable);
        } else {
            customLogger.d(tag, message, throwable);
        }
    }

    @Override
    public void i(String tag, String message) {
        if (customLogger == null) {
            Log.i(tag, message);
        } else {
            customLogger.i(tag, message);
        }
    }

    @Override
    public void i(String tag, String message, Throwable throwable) {
        if (customLogger == null) {
            Log.i(tag, message, throwable);
        } else {
            customLogger.i(tag, message, throwable);
        }
    }

    @Override
    public void w(String tag, String message) {
        if (customLogger == null) {
            Log.w(tag, message);
        } else {
            customLogger.w(tag, message);
        }
    }

    @Override
    public void w(String tag, String message, Throwable throwable) {
        if (customLogger == null) {
            Log.w(tag, message, throwable);
        } else {
            customLogger.w(tag, message, throwable);
        }
    }

    @Override
    public void e(String tag, String message) {
        if (customLogger == null) {
            Log.e(tag, message);
        } else {
            customLogger.e(tag, message);
        }
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        if (customLogger == null) {
            Log.e(tag, message, throwable);
        } else {
            customLogger.e(tag, message, throwable);
        }
    }
}
