package com.qtech.os.command.util;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtils {
    private FileUtils() {
        throw new UnsupportedOperationException("Tried to access constructor of utility class.");
    }

    public static boolean isValidJar(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while(e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                System.out.println(entry.getName());
            }
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}
