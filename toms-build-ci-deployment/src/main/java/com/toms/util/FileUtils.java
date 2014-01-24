/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.toms.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URLConnection;
import java.nio.channels.Channel;
import java.util.Random;
import java.util.jar.JarFile;

/**
 * This class also encapsulates methods which allow Files to be
 * referred to using abstract path names which are translated to native
 * system file paths at runtime as well as copying files or setting
 * their last modification time.
 *
 */
public class FileUtils {
    private static final FileUtils PRIMARY_INSTANCE = new FileUtils();

    //get some non-crypto-grade randomness from various places.
    private static Random rand = new Random(System.currentTimeMillis()
            + Runtime.getRuntime().freeMemory());

    static final int BUF_SIZE = 8192;

    /**
     * The granularity of timestamps under FAT.
     */
    public static final long FAT_FILE_TIMESTAMP_GRANULARITY = 2000;

    /**
     * The granularity of timestamps under Unix.
     */
    public static final long UNIX_FILE_TIMESTAMP_GRANULARITY = 1000;

    /**
     * The granularity of timestamps under the NT File System.
     * NTFS has a granularity of 100 nanoseconds, which is less
     * than 1 millisecond, so we round this up to 1 millisecond.
     */
    public static final long NTFS_FILE_TIMESTAMP_GRANULARITY = 1;

    /**
     * Method to retrieve The FileUtils, which is shared by all users of this
     * method.
     * @return an instance of FileUtils.
     * @since Ant 1.6.3
     */
    public static FileUtils getFileUtils() {
        return PRIMARY_INSTANCE;
    }

    /**
     * Empty constructor.
     */
    protected FileUtils() {
    }


    /**
     * Close a Writer without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     * @param device output writer, can be null.
     */
    public static void close(Writer device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    /**
     * Close a Reader without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device Reader, can be null.
     */
    public static void close(Reader device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    /**
     * Close a stream without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device stream, can be null.
     */
    public static void close(OutputStream device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    /**
     * Close a stream without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device stream, can be null.
     */
    public static void close(InputStream device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    /**
     * Close a Channel without throwing any exception if something went wrong.
     * Do not attempt to close it if the argument is null.
     *
     * @param device channel, can be null.
     * @since Ant 1.8.0
     */
    public static void close(Channel device) {
        if (null != device) {
            try {
                device.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    /**
     * Closes an URLConnection if its concrete implementation provides
     * a way to close it that Ant knows of.
     *
     * @param conn connection, can be null
     * @since Ant 1.8.0
     */
    public static void close(URLConnection conn) {
        if (conn != null) {
            try {
                if (conn instanceof JarURLConnection) {
                    JarURLConnection juc = (JarURLConnection) conn;
                    JarFile jf = juc.getJarFile();
                    jf.close();
                    jf = null;
                } else if (conn instanceof HttpURLConnection) {
                    ((HttpURLConnection) conn).disconnect();
                }
            } catch (IOException exc) {
                //ignore
            }
        }
    }

    /**
     * Delete the file with {@link File#delete()} if the argument is not null.
     * Do nothing on a null argument.
     * @param file file to delete.
     */
    public static void delete(File file) {
        if (file != null) {
            file.delete();
        }
    }
}
