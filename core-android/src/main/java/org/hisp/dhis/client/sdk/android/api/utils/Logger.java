/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.api.utils;

import android.util.Log;

import org.hisp.dhis.client.sdk.core.common.ILogger;

public class Logger implements ILogger {
    private final ILogger customLogger;

    public Logger() {
        this(null);
    }

    public Logger(ILogger customLogger) {
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
