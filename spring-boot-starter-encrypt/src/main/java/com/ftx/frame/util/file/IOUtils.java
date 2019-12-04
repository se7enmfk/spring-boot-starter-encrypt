package com.ftx.frame.util.file;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
           ioe.printStackTrace();
        }
    }
}
