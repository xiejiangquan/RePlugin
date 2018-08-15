/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.qihoo360.replugin.helper;

import android.util.Log;

/**
 * 只在Debug环境下才输出的各种日志，只有当setDebug为true时才会出来
 * <p>
 * 注意：Release版不会输出，而且会被Proguard删除掉
 *
 * @author RePlugin Team
 */

class DefaultLogImpl implements ILogger {

    /**
     * Send a verbose log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @Override
    public int v(String tag, String msg) {
        return Log.v(TAG_PREFIX + tag, msg);
    }

    /**
     * Send a verbose log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    @Override
    public int v(String tag, String msg, Throwable tr) {
        return Log.v(TAG_PREFIX + tag, msg, tr);
    }

    /**
     * Send a debug log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @Override
    public int d(String tag, String msg) {
        return Log.d(TAG_PREFIX + tag, msg);
    }

    /**
     * Send a debug log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    @Override
    public int d(String tag, String msg, Throwable tr) {
        return Log.d(TAG_PREFIX + tag, msg, tr);
    }

    /**
     * Send an info log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @Override
    public int i(String tag, String msg) {
        return Log.i(TAG_PREFIX + tag, msg);
    }

    /**
     * Send a inifo log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    @Override
    public int i(String tag, String msg, Throwable tr) {
        return Log.i(TAG_PREFIX + tag, msg, tr);
    }

    /**
     * Send a warning log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @Override
    public int w(String tag, String msg) {
        return Log.w(TAG_PREFIX + tag, msg);
    }

    /**
     * Send a warning log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    @Override
    public int w(String tag, String msg, Throwable tr) {
        return Log.w(TAG_PREFIX + tag, msg, tr);
    }

    /**
     * Send a warning log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param tr An exception to log
     */
    @Override
    public int w(String tag, Throwable tr) {
        return Log.w(TAG_PREFIX + tag, tr);
    }

    /**
     * Send an error log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @Override
    public int e(String tag, String msg) {
        return Log.e(TAG_PREFIX + tag, msg);
    }

    /**
     * Send a error log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    @Override
    public int e(String tag, String msg, Throwable tr) {
        return Log.e(TAG_PREFIX + tag, msg, tr);
    }
}
